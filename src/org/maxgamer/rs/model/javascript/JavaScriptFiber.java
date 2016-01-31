package org.maxgamer.rs.model.javascript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;

import org.maxgamer.rs.core.Core;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JavaScriptFiber {
	public static final File SCRIPT_FOLDER = new File("javascripts");
	static{
		if(SCRIPT_FOLDER.isDirectory() == false){
			SCRIPT_FOLDER.mkdirs();
		}
	}
	
	private final JavaScriptContextFactory factory;
	
	/**
	 * Maximum number of millisecond credits for this script to hold at once
	 */
	private static final long MAX_CREDITS = 300; 
	
	/**
	 * How many millisecond credits this script is rationed when called
	 */
	private static final long CREDITS_PER_CALL = 5;
	
	/**
	 * The scope of this script. This contains all functions and variables
	 * in the script.
	 */
	private Scriptable scope;
	
	private HashMap<File, Long> sources = new HashMap<File, Long>();
	
	/**
	 * The number of millisecond credits this script holds. Every time it is called,
	 * an amount of credits are supplied. Up to MAX_CREDITS may be stored at once. 
	 * If this drops below 0, then the script will throw a TimeoutError.
	 */
	private long credits = MAX_CREDITS;
	
	/**
	 * Constructs a new JavaScriptFiber, loading the given file
	 * when execution starts. This also loads the core.js library
	 * file raising a RuntimeException if the file can't be loaded.
	 * can't be loaded.  This loads the Core.CLASS_LOADER instead of
	 * allowing for a custom one.
	 * @param file The file that contains the script that is to be run.
	 */
	public JavaScriptFiber(){
		this(Core.CLASS_LOADER);
	}
	
	/**
	 * Constructs a new JavaScriptFiber, loading the given file
	 * when execution starts. This also loads the core.js library
	 * file raising a RuntimeException if the file can't be loaded.
	 * can't be loaded. 
	 * @param file The file that contains the script that is to be run.
	 */
	public JavaScriptFiber(ClassLoader loader) {
		factory = new JavaScriptContextFactory(loader){
			@Override
			public long credits(){
				return credits;
			}
			@Override
			public void pay(long expense){
				credits -= expense;
			}
		};
		
		try{
			JavaScriptContext context = factory.enterContext();
			
			/* Create our scope, ImporterTopLevel allows references to classes by full name */
			this.scope = context.initStandardObjects(new ImporterTopLevel(context)); 
			
			/* Reference required to "this" to pause. */
			this.set("fiber", this);
		}
		finally{
			Context.exit();
		}
	}
	
	public JavaScriptContext context(){
		return factory.enterContext();
	}
	
	/**
	 * Invokes the given method with the given parameters in this JavaScriptFiber
	 * @param method the method to invoke
	 * @param params the parameters
	 * @return the result of the call, may be null.
	 */
	public JavaScriptCall invoke(String method, Object... params) throws NoSuchMethodException {
		JavaScriptCall call = new JavaScriptCall(this);
		try{
			JavaScriptContext ctx = factory.enterContext();
			ctx.setCall(call);
			
			Object o = this.scope.get(method, scope);
			
			if(o == Function.NOT_FOUND){
				throw new NoSuchMethodException("Method not found: " + method + "(...)");
			}
			
			if(o instanceof Function == false){
				throw new RuntimeException(o + " is not of type " + Function.class.getCanonicalName() + ", cannot call " + method + "()");
			}
			
			Function func = (Function) this.scope.get(method, scope);
			
			Object result = ctx.callFunctionWithContinuations(func, this.scope, params);
			call.setResult(result);
			return call;
		}
		catch(ContinuationPending e){
			call.setState(e);
		}
		finally{
			Context.exit();
		}
		return call;
	}
	
	/**
	 * Includes the script by the given path.  This first searches the plugin_folder/js/path
	 * for the file, loading it if found. Else, this searches the JAR archive for the file,
	 * loading it if found. Else, this raises a {@link FileNotFoundException}.
	 * @param path the path to the file, without js/ prefix. Eg lib/dialogue.js or npc/doomsayer.js
	 * @throws IOException
	 */
	public JavaScriptCall parse(String path) throws IOException {
		File f = new File(SCRIPT_FOLDER, path);
		InputStream in;
		String name = f.getPath();
		if(f.exists()){
			in = new FileInputStream(f);
		}
		else{
			throw new FileNotFoundException(f.getPath());
		}
		
		return this.parse(name, in);
	}
	
	/**
	 * Interprets the given file.  This should not be used on suspendable scripts.
	 * @param f the file to load
	 * @throws IOException if an {@link IOException} occurs.
	 */
	public JavaScriptCall parse(File f) throws IOException{
		FileInputStream in = new FileInputStream(f);
		try{
			sources.put(f, System.currentTimeMillis());
			return parse(f.getPath(), in);
		}
		finally{
			in.close();
		}
	}
	
	/**
	 * Interprets the given InputStream.  This should not be used on suspendable scripts.
	 * @param in the {@link InputStream} to load
	 * @throws IOException if an {@link IOException} occurs.
	 */
	public JavaScriptCall parse(String name, InputStream in) throws IOException{
		JavaScriptContext context = factory.enterContext();
		
		NativeFunction script = null;
		JavaScriptCall call = new JavaScriptCall(this);
		context.setCall(call);
		
		try{
			/* Sets interpreted mode, allowing us to pause the script during execution */
			context.setOptimizationLevel(-1);
			
			InputStreamReader reader = new InputStreamReader(in);
			script = (NativeFunction) context.compileReader(reader, name, 1, null);
			reader.close();
			
			Object result = context.executeScriptWithContinuations((Script) script, this.scope);
			
			call.setResult(result);
		}
		catch(ContinuationPending e){
			call.setState(e);
		}
		finally{
			Context.exit();
		}
		return call;
	}
	
	/**
	 * Parses the given file but does not allow for {@link ContinuationPending} to be thrown
	 * @param path the path of the file to parse, as in Titan/javascripts/{path}
	 * @throws IOException if an {@link IOException} occurs
	 */
	public JavaScriptCall include(String path) throws IOException{
		File f = new File(SCRIPT_FOLDER, path);
		InputStream in;
		String name = f.getPath();
		if(f.exists()){
			in = new FileInputStream(f);
		}
		else{
			throw new FileNotFoundException(f.getPath());
		}
		
		Context context = factory.enterContext();
		JavaScriptCall call = new JavaScriptCall(null);
		
		try{
			/* Sets interpreted mode, allowing us to pause the script during execution */
			context.setOptimizationLevel(-1);
			
			sources.put(f, System.currentTimeMillis());
			
			InputStreamReader reader = new InputStreamReader(in);
			Object result = context.evaluateReader(this.scope, reader, name, 1, null);
			reader.close();
			
			call.setResult(result);
		}
		catch(ContinuationPending e){
			call.setState(e); 
		}
		finally{
			Context.exit();
		}
		return call;
	}
	
	/**
	 * Returns true if any of the loaded files have been modified since they
	 * were loaded.
	 * @return true if any of the loaded files have been modified
	 */
	public boolean modified(){
		for(Entry<File, Long> entry : this.sources.entrySet()){
			File f = entry.getKey();
			long loaded = entry.getValue();
			
			if(f.lastModified() > loaded){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Reloads any loaded files.  If force is true, then all files will be reloaded.
	 * If force is false, they will only be reloaded if modified.  Remanents of old
	 * code are not removed from the scope. You may need to reset continuations if this
	 * occurs.
	 * @param force true to reload everything, false to reload only changes
	 */
	public void reload(boolean force){
		for(Entry<File, Long> entry : this.sources.entrySet()){
			File f = entry.getKey();
			long loaded = entry.getValue();
			
			if(force || f.lastModified() > loaded){
				try{
					if(this.parse(f).isFinished() == false){
						throw new RuntimeException("Cannot reload() a script that raises ContinuationPending! Script: " + f);
					}
				}
				catch(IOException e){
					// Skip parsing it
				}
			}
		}
	}
	
	/**
	 * Sets the given field to the given value inside the script.
	 * @param field the field to set
	 * @param value the value to set
	 */
	public void set(String field, Object value){
		ScriptableObject.putProperty(scope, field, value);
	}
	

	public ContinuationPending state() {
		Context context = factory.enterContext();
		try {
			return context.captureContinuation();
		}
		finally {
			Context.exit();
		}
	}
	
	/**
	 * Pauses the JavaScriptFiber. This should only be called as a result of the
	 * JavaScriptFiber's calls. This throws {@link ContinuationPending}.
	 */
	public void pause() throws ContinuationPending{
		throw state();
	}
	
	/**
	 * Unpauses the JavaScriptFiber. This resumes, after {@link ContinuationPending}
	 * is caught from a pause().
	 * @param response
	 */
	public void unpause(JavaScriptCall call, Object response) {
		/* Prevent double unpauses() or unpausing finished fibers */
		if(call == null){
			throw new NullPointerException("JavaScriptCall may not be null for unpause()");
		}
		
		if(call.isFinished()){
			return;
		}
		
		credits = Math.min(credits + CREDITS_PER_CALL, MAX_CREDITS);
		
		JavaScriptContext context = factory.enterContext();
		context.setCall(call);
		try {
			/* Resumes the script. This runs until the script finishes or is paused again. */
			Object result = context.resumeContinuation(call.getState().getContinuation(), this.scope, response);
			call.setResult(result);
		}
		catch (ContinuationPending pending) {
			/* Script was paused, it will be resumed later. */
			call.setState(pending);
		}
		finally {
			Context.exit();
		}
	}
}
