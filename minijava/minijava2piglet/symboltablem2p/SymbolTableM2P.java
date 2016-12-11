package minijava.minijava2piglet.symboltablem2p;

import java.util.Hashtable;

import minijava.typecheck.symboltable.*;

public class SymbolTableM2P extends SymbolTable{
	
	static public Hashtable<String, EntryInfoClassM2P>symbolTableM2P;
	
	//do we need to override this? yes, because the "symboltable" is a static variable, so if we do c_put again here, something bad will happen
	public void c_put(String name, EntryInfoClassM2P value)
	{
		if(symbolTableM2P == null)
		{
			symbolTableM2P = new Hashtable<String, EntryInfoClassM2P>();
		}
		symbolTableM2P.put(name,  value);
	}
	
	public EntryInfoClassM2P c_get(String key)
	{
		return (symbolTableM2P.get(key) == null ? null : symbolTableM2P.get(key));
	}
	
	public void inherit_from_ancestors()
	{
		for(String key : symbolTableM2P.keySet())
		{
			EntryInfoClassM2P classInfo = this.c_get(key);
			classInfo.inherit_from_ancestors(this);
		}
	}
}
