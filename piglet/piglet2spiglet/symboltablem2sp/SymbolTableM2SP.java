package piglet.piglet2spiglet.symboltablem2sp;

import java.util.Hashtable;

import minijava.typecheck.symboltable.*;

public class SymbolTableM2SP extends SymbolTable{
	
	static public Hashtable<String, EntryInfoClassM2SP>symbolTableM2SP;
	
	//do we need to override this? yes, because the "symboltable" is a static variable, so if we do c_put_m2sp again here, something bad will happen
	public void c_put_m2sp(String name, EntryInfoClassM2SP value)
	{
		if(symbolTableM2SP == null)
		{
			symbolTableM2SP = new Hashtable<String, EntryInfoClassM2SP>();
		}
		symbolTableM2SP.put(name,  value);
	}
	
	public EntryInfoClassM2SP c_get_m2sp(String key)
	{
		return (symbolTableM2SP.get(key) == null ? null : symbolTableM2SP.get(key));
	}
	
	public void inherit_from_ancestors()
	{
		for(String key : symbolTableM2SP.keySet())
		{
			EntryInfoClassM2SP classInfo = this.c_get_m2sp(key);
			classInfo.inherit_from_ancestors(this);
		}
	}
}
