package org.maxgamer.rs.io.classparse;

public class Simple{
	public static void main(String[] args){
		Nested.run();
	}
	public static class Nested{
		public static void run(){
			System.out.println(Nested.class.getName());
		}
	}
}