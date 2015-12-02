package org.maxgamer.rs.model.javascript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.ImporterTopLevel;
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
	
	/**
	 * Library JavaScript files which should be loaded in all Fibers
	 */
	private static final String[] LIBS = new String[]{ "lib/core.js" };
	
	/**
	 * The file that this Script originated from
	 */
	private final File file;
	
	/**
	 * The scope of this script. This contains all functions and variables
	 * in the script.
	 */
	private Scriptable scope;
	
	/**
	 * Created when execution is paused / pause() is called.
	 */
	private ContinuationPending pending;
	
	/**
	 * Constructs a new JavaScriptFiber, loading the given file
	 * when execution starts. This also loads the core.js library
	 * file raising a RuntimeException if the file can't be loaded.
	 * can't be loaded. 
	 * @param file The file that contains the script that is to be run.
	 */
	public JavaScriptFiber(File file) {
		this.file = file;
		try{
			Context context = Context.enter();
			
			/* Create our scope, ImporterTopLevel allows references to classes by full name */
			this.scope = context.initStandardObjects(new ImporterTopLevel(context)); 
			
			/* Load our library files - Currently only lib/core.js */
			for (String s : LIBS) {
				this.include(s);
			}
		}
		catch(IOException e){
			/* This shouldn't happen unless development is being done in core.js */
			throw new RuntimeException(e);
		}
		finally{
			Context.exit();
		}
	}
	
	/**
	 * Includes the script by the given path.  This first searches the plugin_folder/js/path
	 * for the file, loading it if found. Else, this searches the JAR archive for the file,
	 * loading it if found. Else, this raises a {@link FileNotFoundException}.
	 * @param path the path to the file, without js/ prefix. Eg lib/dialogue.js or npc/doomsayer.js
	 * @throws IOException
	 */
	public void include(String path) throws IOException{
		File f = new File(SCRIPT_FOLDER, path);
		InputStream in;
		String name = f.getPath();
		if(f.exists()){
			in = new FileInputStream(f);
		}
		else{
			throw new FileNotFoundException(f.getPath());
		}
		
		this.load(name, in);
	}
	
	/**
	 * Interprets the given file.  This should not be used on suspendable scripts.
	 * @param f the file to load
	 * @throws IOException if an {@link IOException} occurs.
	 */
	public void load(File f) throws IOException{
		FileInputStream in = new FileInputStream(f);
		load(f.getPath(), in);
		in.close();
	}
	
	/**
	 * Interprets the given InputStream.  This should not be used on suspendable scripts.
	 * @param in the {@link InputStream} to load
	 * @throws IOException if an {@link IOException} occurs.
	 */
	public void load(String name, InputStream in) throws IOException{
		Context context = Context.enter();
		InputStreamReader reader = new InputStreamReader(in);
		context.evaluateReader(this.scope, reader, name, 1, null);
		reader.close();
		Context.exit();
	}
	
	/**
	 * Sets the given field to the given value inside the script.
	 * @param field the field to set
	 * @param value the value to set
	 */
	public void set(String field, Object value){
		ScriptableObject.putProperty(scope, field, value);
	}
	
	/**
	 * Starts the {@link JavaScriptFiber}.  This will return when the script
	 * either raises a {@link ContinuationPending} exception (through pause())
	 * or finishes.
	 * @throws IOException if an {@link IOException} occurs.
	 */
	public void start() throws IOException {
		try {
			Context context = Context.enter();
			
			/* Sets interpreted mode, allowing us to pause the script during execution */
			context.setOptimizationLevel(-1);
			
			/* Reference required to "this" to pause. */
			this.set("fiber", this);
			
			/* Parse the entire file */
			FileReader reader = new FileReader(this.file);
			Script script = context.compileReader(reader, this.file.getPath(), 1, null);
			reader.close();
			
			/* Begins executing, until it finishes or pause() is called, which throws ContinuationPending */
			context.executeScriptWithContinuations(script, this.scope);
		}
		catch (ContinuationPending pending) {
			return;
		}
		finally {
			Context.exit();
		}
		
		/* This is only reached if the script finished peacefully */
		stop();
	}
	
	/**
	 * Terminates this JavaScript fiber. If the fiber is running, this will 
	 * terminate at the next pause() call.
	 */
	public void stop(){
		scope = null;
		pending = null;
	}
	
	/**
	 * Pauses the JavaScriptFiber. This should only be called as a result of the
	 * JavaScriptFiber's calls. This throws {@link ContinuationPending}.
	 */
	public void pause() throws ContinuationPending {
		if(isFinished()){
			return;
		}
		
		Context context = Context.enter();
		try {
			this.pending = context.captureContinuation();
			throw pending;
		}
		finally {
			Context.exit();
		}
	}
	
	/**
	 * Unpauses the JavaScriptFiber. This resumes, after {@link ContinuationPending}
	 * is caught from a pause().
	 * @param response
	 */
	public void unpause(Object response) {
		if(isFinished()) return;
		
		/* Prevent double unpauses() or unpausing finished fibers */
		if(this.pending == null){
			throw new IllegalStateException("Cannot unpause, as JavaScriptFiber is currently not paused! Maybe it finished?");
		}
		
		try {
			Context context = Context.enter();
			
			/* Remove our reference to pending */
			ContinuationPending pending = this.pending;
			this.pending = null;
			
			/* Resumes the script. This runs until the script finishes or is paused again. */
			context.resumeContinuation(pending.getContinuation(), this.scope, response);
		}
		catch (ContinuationPending pending) {
			/* Script was paused, it will be resumed later. */
			return;
		}
		finally {
			Context.exit();
		}
		
		/* This is only reached if the script finished peacefully */
		stop();
	}
	
	/**
	 * Returns true if this {@link JavaScriptFiber} has terminated.
	 * @return true if this {@link JavaScriptFiber} has terminated.
	 */
	public boolean isFinished(){
		if(scope == null){
			return true;
		}
		
		return false;
	}
}
