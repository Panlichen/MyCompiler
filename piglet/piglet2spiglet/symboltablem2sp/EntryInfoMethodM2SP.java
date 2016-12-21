package piglet.piglet2spiglet.symboltablem2sp;

import java.util.Hashtable;
import java.util.Vector;
import minijava.typecheck.symboltable.*;
import piglet.piglet2spiglet.codesketch.*;

public class EntryInfoMethodM2SP extends EntryInfoMethod{
	Vector<String> localDefinedVariables;
	Vector<String> parameters;
	VariableZone variableZone;
	
	public EntryInfoMethodM2SP()
	{
		super();
		this.localDefinedVariables = new Vector<String>();
		this.parameters = new Vector<String>();
		this.variableZone = new VariableZone();
	}
	
	public void v_put_m2sp(String name, EntryInfoVariable value)
	{
		super.v_put(name, value);
		this.variableZone.add_variable(name);
	}
	
	public VariableZone get_variable_zone()
	{
		return this.variableZone;
	}
	
	public void add_local_defined_variable(String name)
	{
		this.localDefinedVariables.add(name);
	}
	
	public Vector<String> get_all_local_defined_variables()
	{
		return this.localDefinedVariables;
	}
	
	public int get_num_local_defined_variable()
	{
		return this.localDefinedVariables.size();
	}
	//public int get_num_paras(){}  already implemented
	public int get_num_local_variable()
	{
		return this.get_variable_zone().get_num_local_variable();
	}
	
	
	public void add_parameter(String name)
	{
		this.parameters.add(name);
	}
	public Vector<String> get_all_parameters()
	{
		return this.parameters;
	}
	
	public PigletCodeSet get_variable_info(String varName, ResourceManager r)
	{
		PigletCodeSet ret = new PigletCodeSet();
		if(this.localDefinedVariables.contains(varName) || this.parameters.contains(varName))
		{
			ret.set_temp_address(new PigletCode("TEMP " + (this.variableZone.get_variable_offset(varName) + 1)));
			ret.set_memory_address(null);
		}
		else
		{
			PigletCode tempAddress = new PigletCode(r.get_new_temp());
			EntryInfoClassM2SP classInfo = SymbolTableM2SP.symbolTableM2SP.get(this.get_belong_class_name());
			int varOffset = classInfo.get_variable_zone().get_variable_offset(varName);
			PigletCode memoryAddress = new PigletCode("TEMP 0 " + 4 * (varOffset + 1));
			ret.emit("HLOAD " + tempAddress + " " + memoryAddress);
			ret.set_memory_address(memoryAddress);
			ret.set_temp_address(tempAddress);
		}
		ret.set_value_type(this.v_get(varName).get_type());
		return ret;
	}
	
}
