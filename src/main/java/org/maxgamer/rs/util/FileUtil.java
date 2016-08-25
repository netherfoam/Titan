package org.maxgamer.rs.util;

import java.io.File;

public final class FileUtil {
	public static String getExtension(String name){
		int start = name.lastIndexOf('.');
		if(start < 0){
			return "";
		}
		
		return name.substring(start + 1);
	}
	
	public static String getExtension(File f){
		return getExtension(f.getName());
	}
	
	private FileUtil(){
		//Private constructor
	}
}
