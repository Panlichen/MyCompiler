package minijava.minijava2piglet.codesketch;

import java.util.*;

public class MethodZone {
	public Hashtable<String, String> methodLabel;
	public Hashtable<String, Integer>methodOffset;
	public Vector<String> methodVector;
	
	public MethodZone()
	{
		this.methodLabel = new Hashtable<String, String>();
		this.methodOffset = new Hashtable<String, Integer>();
		this.methodVector = new Vector<String>();
	}
	
	public void add_method(String methodName, String methodLabel)
	{
		this.methodVector.add(methodName);
		this.methodOffset.put(methodName, this.methodVector.size() - 1);
		this.methodLabel.put(methodName, methodLabel);
	}
	
	public int get_num_method()
	{
		return this.methodVector.size();
	}
	
	public Vector<String> get_method_vector()
	{
		return this.methodVector;
	}
	
	public int get_method_offset(String mName)
	{
		return this.methodOffset.get(mName);
	}
	
	public String get_method_label(String mName)
	{
		return this.methodLabel.get(mName);
	}
	
}
