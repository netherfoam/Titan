package org.maxgamer.rs.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author netherfoam
 */
public class Logger extends PrintStream {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public Logger(File file) throws FileNotFoundException {
		super(new FileOutputStream(file, true));
	}
	
	@Override
	public void print(String s) {
		Date date = new Date();
		String data = "[" + dateFormat.format(date) + "] " + s;
		super.print(data);
	}
}