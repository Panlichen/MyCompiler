package minijava.typecheck.symboltable;

import java.util.Vector;

public class ErrorPrinter {
	
	static Vector<String> errors = new Vector<String>();
	static Vector<String> warnings = new Vector<String>();
	
	public static boolean error_exists() 
	{
		return (errors.size() > 0);
	}
	
	public static void add_error(int line, String error_msg) 
	{
		String msg = "Error: Line " + line + ": " + error_msg;
		errors.addElement(msg); 
	}
	
	public static void add_warning(int line, String error_msg) 
	{
		String msg = "Warning: Line " + line + ": " + error_msg;
		warnings.addElement(msg); 
	}
	
	public static void print_all_error() 
	{
		int sz = errors.size();
		for (int i = 0; i < sz; i++) {
			System.out.println(errors.elementAt(i));
		}
	}
}
