package piglet.piglet2spiglet.codesketch;

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
		this.variableOffset.put(name,this.variableVector.size());//bug fix: test03, the offset should be size - 1.
		this.variableVector.add(name);
	}
	
	public int get_num_local_variable()
	{
		return this.variableVector.size();
	}
	
	public Vector<String> get_variable_vector()
	{
		return this.variableVector;
	}
	public int get_variable_offset(String vName)
	{
		return this.variableOffset.get(vName);
	}
	
}
