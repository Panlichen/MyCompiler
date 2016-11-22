package minijava.minijava2piglet.symboltablem2p;

import java.util.Vector;
import minijava.typecheck.symboltable.*;

public class EntryInfoMethodM2P extends EntryInfoMethod{
	Vector<String> localDefinedVariables;
	Vector<String> parameters;
	
	public EntryInfoMethodM2P()
	{
		this.localDefinedVariables = new Vector<String>();
		this.parameters = new Vector<String>();
	}
	
	public void add_local_defined_variable(String name)
	{
		this.localDefinedVariables.add(name);
	}
	public Vector<String> get_all_local_defined_variables()
	{
		return this.localDefinedVariables;
	}
	
	public void add_parameter(String name)
	{
		this.localDefinedVariables.add(name);
	}
	public Vector<String> get_all_parameters()
	{
		return this.parameters;
	}
	
}
