package minijava.minijava2piglet.codesketch;

import java.util.*;

public class VariableZone {
	public Hashtable<String, Integer> variableOffset;
	public Vector<String> variableVector;
	
	public VariableZone()
	{
		this.variableOffset = new Hashtable<String, Integer>();
		this.variableVector = new Vector<String>();
	}
	
	public void add_variable(String name)
	{
		this.variableVector.add(name);
		this.variableOffset.put(name,this.variableVector.size());
	}
	
	public int get_num_variable()
	{
		return this.variableVector.size();
	}
	
	public Vector<String> get_variable_vecor()
	{
		return this.variableVector;
	}
	public int get_variable_offset(String vName)
	{
		return this.variableOffset.get(vName);
	}
	
}
