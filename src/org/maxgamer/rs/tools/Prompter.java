package org.maxgamer.rs.tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.maxgamer.rs.structure.Util;

/**
 * Prompter class that asks the server console for input, such as for Setup information,
 * like database passwords, etc.
 * 
 * @author Dirk Jamieson
 * @date 18 Nov 2015
 */
public class Prompter {
	/**
	 * The scanner we use to read from standard in
	 */
	private Scanner scanner;
	
	/**
	 * The old System.out, we steal this on construction, and restore it on close.
	 */
	private PrintStream oldSysOut;
	
	/**
	 * The temporary, cached, system output stream
	 */
	private ByteArrayOutputStream newSysOut = new ByteArrayOutputStream(4096);
	
	/**
	 * The temporary printstream that points to the cached output stream.
	 */
	private PrintStream newSysOutPrintStream;
	
	/**
	 * Constructs a new Prompter. This constructs a new Scanner that will read
	 * from System.in.
	 */
	public Prompter(){
		this.scanner = new Scanner(System.in);
		
		/* Now we steal System.out */
		oldSysOut = System.out;
		oldSysOut.flush();
		newSysOutPrintStream = new PrintStream(this.newSysOut);
		
		System.setOut(newSysOutPrintStream);
	}
	
	public void println(){
		this.oldSysOut.println();
	}
	
	public void print(String s){
		this.oldSysOut.print(s);
	}
	
	public void println(String s){
		this.oldSysOut.println(s);
	}
	
	public void printf(String s, Object... args){
		this.oldSysOut.printf(s, args);
	}
	
	/**
	 * Reads an integer value. If the user enters an invalid number, they are
	 * informed the number was invalid and prompted until they give a valid
	 * value.  If they supply an empty String, then the request is repeated.
	 * @return the number the user entered.
	 */
	public int getInt(){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				this.print("An integer is required: ");
				continue;
			}
			
			try{
				return Integer.parseInt(s);
			}
			catch(NumberFormatException e){
				if(s.contains(".")){
					this.println("Integers may not contain decimals.");
				}
				this.print("An integer is required: ");
				continue;
			}
		}
	}
	

	
	/**
	 * Reads an integer value. If the user enters an invalid number, they are
	 * informed the number was invalid and prompted until they give a valid
	 * value.  If they supply an empty String, then the fallback number is
	 * returned.
	 * @param fallback the default value if the user leaves it blank
	 * @return the number the user entered
	 */
	public int getInt(int fallback){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				return fallback;
			}
			
			try{
				return Integer.parseInt(s);
			}
			catch(NumberFormatException e){
				if(s.contains(".")){
					this.println("Integers may not contain decimals.");
				}
				this.print("An integer is required: ");
				continue;
			}
		}
	}
	
	/**
	 * Reads an String value. If they supply an empty String, then the request 
	 * is repeated.
	 * @return the number the user entered.
	 */
	public String getString(){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				this.print("A String is required: ");
				continue;
			}
			
			return s;
		}
	}
	
	/**
	 * Reads a String value.If they supply an empty String, then the fallback
	 * is returned.
	 * @param fallback the default value if the user leaves it blank
	 * @return the number the user entered.
	 */
	public String getString(String fallback){
		String s = this.scanner.nextLine();
		if(s.isEmpty()){
			return fallback;
		}
		
		return s;
	}
	
	/**
	 * Reads a String value. If the user enters an invalid String, they are
	 * informed the String was invalid and prompted until they give a valid
	 * String.  If they supply an empty String, then the request is repeated.
	 * @param pattern the RegEx pattern that the string must conform to
	 * @return the number the user entered.
	 */
	public String getString(Pattern pattern){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				this.print("A String is required: ");
				continue;
			}
			
			if(s.matches(pattern.pattern()) == false){
				this.print("String was not of the correct format: ");
				continue;
			}
			
			return s;
		}
	}
	
	/**
	 * Reads a String value. If the user enters an invalid String, they are
	 * informed the String was invalid and prompted until they give a valid
	 * String.  If they supply an empty String, then the fallback is returned
	 * @param pattern the RegEx pattern that the string must conform to
	 * @param fallback the default value if left blank
	 * @return the number the user entered.
	 */
	public String getString(Pattern pattern, String fallback){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				return fallback;
			}
			
			if(s.matches(pattern.pattern()) == false){
				this.print("String was not of the correct format: ");
				continue;
			}
			
			return s;
		}
	}
	
	/**
	 * Reads an boolean value. If the user enters an invalid boolean, they are
	 * informed the boolean was invalid and prompted until they give a valid
	 * boolean.  If they supply an empty String, then the request is repeated.
	 * @return the boolean the user entered.
	 */
	public boolean getBoolean(){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				this.print("A true/false value is required: ");
				continue;
			}
			
			try{
				return Util.parseBoolean(s);
			}
			catch(ParseException e){
				this.print("A true/false value is required: ");
				continue;
			}
		}
	}
	
	/**
	 * Reads an boolean value. If the user enters an invalid boolean, they are
	 * informed the boolean was invalid and prompted until they give a valid
	 * boolean.  If they supply an empty String, then the fallback is returned
	 * @param fallback The fallback value if the user left it blank
	 * @return the boolean the user entered.
	 */
	public boolean getBoolean(boolean fallback){
		while(true){
			String s = this.scanner.nextLine();
			if(s.isEmpty()){
				return fallback;
			}
			
			try{
				return Util.parseBoolean(s);
			}
			catch(ParseException e){
				this.print("A true/false value is required: ");
				continue;
			}
		}
	}

	/**
	 * Allows standard output to be printed again.  Anything that was printed to
	 * standard out while running this prompter will be printed immediately following.
	 */
	public void close() {
		this.newSysOutPrintStream.flush();
		byte[] data = this.newSysOut.toByteArray();
		System.setOut(this.oldSysOut);
		this.oldSysOut.print(String.valueOf(data));
		this.oldSysOut.flush();
		
		this.newSysOut = null;
		this.newSysOutPrintStream = null;
		this.oldSysOut = null;
		this.scanner = null;
	}
}
