package minijava.minijava2piglet.symboltablem2p;

import minijava.typecheck.symboltable.*;
import java.util.*;
import minijava.minijava2piglet.codesketch.*;

public class EntryInfoClassM2P extends EntryInfoClass{
	Hashtable<String, EntryInfoMethodM2P>mthdTableM2P;
	VariableZone variableZone;
	MethodZone methodZone;
	
	public EntryInfoClassM2P()
	{
		super();
		this.methodZone = new MethodZone();
		this.variableZone = new VariableZone();
		this.mthdTableM2P = new Hashtable<String, EntryInfoMethodM2P>();
	}
	
	public VariableZone get_variable_zone()
	{
		return this.variableZone;
	}
	
	public MethodZone get_method_zone()
	{
		return this.methodZone;
	}
	
	public void v_put_m2p(String name, EntryInfoVariable value)
	{
		super.v_put(name, value);
		this.variableZone.add_variable(name);
	}
	public int get_num_member_variable()
	{
		return this.get_variable_zone().get_num_local_variable();
	}
	
	public void m_put_m2p(String name, EntryInfoMethodM2P value)
	{
		this.mthdTableM2P.put(name, value);
	}
	
	public void m_zone_put(String name, String label)
	{
		this.methodZone.add_method(name, label);
	}

	
	public EntryInfoMethodM2P m_get_m2p(String key)
	{
		return this.mthdTableM2P.get(key);
	}
	
	public Hashtable<String, EntryInfoMethodM2P> get_mthd_table_m2p()
	{
		return this.mthdTableM2P;
	}
	
	public void inherit_from_ancestors(SymbolTableM2P topTable)
	{
		String name = this.get_parent_class();
		while(name != null)
		{
			EntryInfoClassM2P classInfo = topTable.c_get_m2p(name);
			if(classInfo.get_var_table() != null)
				for(String key: classInfo.get_var_table().keySet())
				{
					if(this.v_get(key) == null)
					{
						this.v_put_m2p(key, classInfo.v_get(key));
					}
				}
			if(classInfo.get_mthd_table_m2p() != null)
				for(String key: classInfo.get_mthd_table_m2p().keySet())
				{
					if(this.m_get_m2p(key) == null)
					{
						this.m_put_m2p(key, classInfo.m_get_m2p(key));
						this.m_zone_put(key, name + "_" + key);
					}
				}
			name = classInfo.get_parent_class();
		}
		
		if(this.get_mthd_table_m2p() != null)
			for(String key : this.get_mthd_table_m2p().keySet())
			{
				this.m_get_m2p(key).add_member_vars(this.get_var_table());
			}
	}
	
}
