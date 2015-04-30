package org.maxgamer.rs.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.classpath.BshClassPath;
import bsh.classpath.BshClassPath.MappingFeedback;
import bsh.classpath.ClassManagerImpl;

/**
 * @author netherfoam
 */
public class ScriptUtil {
	public static final NameSpace GLOBAL_SPACE;
	
	static {
		BshClassPath.addMappingFeedback(new MappingFeedback() {
			@Override
			public void startClassMapping() {
			}
			
			@Override
			public void errorWhileMapping(String arg0) {
			}
			
			@Override
			public void endClassMapping() {
			}
			
			@Override
			public void classMapping(String arg0) {
			}
		});
		
		try {
			ClassManagerImpl m = new ClassManagerImpl();
			m.getClassPath().setPath(new URL[] { new File("bin").toURI().toURL() });
			GLOBAL_SPACE = new NameSpace(m, "GLOBAL_SPACE");
			GLOBAL_SPACE.doSuperImport();
		}
		catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static Interpreter getScript(File file) {
		//TODO: Can be optimised. Takes upto 100ms, probably because of IO in main thread
		//Serious potential optimisation here for some easy speed.
		NameSpace space = new NameSpace(GLOBAL_SPACE, file.getPath());
		
		Interpreter environment;
		try {
			environment = new Interpreter(null, System.out, System.err, false, space);
			environment.eval(new FileReader(file));
			return environment;
		}
		catch (FileNotFoundException e) {
			return null;
		}
		catch (EvalError e) {
			e.printStackTrace();
			return null;
		}
	}
}