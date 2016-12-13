package piglet.piglet2spiglet.symboltablem2sp;

import minijava.typecheck.symboltable.*;
import java.util.*;
import piglet.piglet2spiglet.codesketch.*;

public class EntryInfoClassM2SP extends EntryInfoClass{
	Hashtable<String, EntryInfoMethodM2SP>mthdTableM2SP;
	VariableZone variableZone;
	MethodZone methodZone;
	
	public EntryInfoClassM2SP()
	{
		super();
		this.methodZone = new MethodZone();
		this.variableZone = new VariableZone();
		this.mthdTableM2SP = new Hashtable<String, EntryInfoMethodM2SP>();
	}
	
	public VariableZone get_variable_zone()
	{
		return this.variableZone;
	}
	
	public MethodZone get_method_zone()
	{
		return this.methodZone;
	}
	
	public void v_put_m2sp(String name, EntryInfoVariable value)
	{
		super.v_put(name, value);
		this.variableZone.add_variable(name);
	}
	public int get_num_member_variable()
	{
		return this.get_variable_zone().get_num_local_variable();
	}
	
	public void m_put_m2sp(String name, EntryInfoMethodM2SP value)
	{
		this.mthdTableM2SP.put(name, value);
	}
	
	public void m_zone_put(String name, String label)
	{
		this.methodZone.add_method(name, label);
	}

	
	public EntryInfoMethodM2SP m_get_m2sp(String key)
	{
		return this.mthdTableM2SP.get(key);
	}
	
	public Hashtable<String, EntryInfoMethodM2SP> get_mthd_table_m2sp()
	{
		return this.mthdTableM2SP;
	}
	
	public void inherit_from_ancestors(SymbolTableM2SP topTable)
	{
		String name = this.get_parent_class();
		while(name != null)
		{
			EntryInfoClassM2SP classInfo = topTable.c_get_m2sp(name);
			if(classInfo.get_var_table() != null)
				for(String key: classInfo.get_var_table().keySet())
				{
					if(this.v_get(key) == null)
					{
						this.v_put_m2sp(key, classInfo.v_get(key));
					}
				}
			if(classInfo.get_mthd_table_m2sp() != null)
				for(String key: classInfo.get_mthd_table_m2sp().keySet())
				{
					if(this.m_get_m2sp(key) == null)
					{
						this.m_put_m2sp(key, classInfo.m_get_m2sp(key));
						this.m_zone_put(key, name + "_" + key);
					}
				}
			name = classInfo.get_parent_class();
		}
		
		if(this.get_mthd_table_m2sp() != null)
			for(String key : this.get_mthd_table_m2sp().keySet())
			{
				this.m_get_m2sp(key).add_member_vars(this.get_var_table());
			}
	}
	
}
