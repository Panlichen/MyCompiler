package minijava.typecheck.symboltable;
import java.util.Hashtable;
import java.util.HashSet;

public class SymbolTable extends EntryInfo{
	static public Hashtable<String, EntryInfoClass> symbolTable;
	public String mainClassName;
	
	public void c_put(String name, EntryInfoClass value)
	{
		if(this.symbolTable == null)
		{
			this.symbolTable = new Hashtable<String, EntryInfoClass>();
		}
		if(this.symbolTable.get(name) != null)
		{
			ErrorPrinter.add_error(value.get_line_number(), "redefined class: " + value.get_name());
			//error recovery: we add the redefined class, and it replaces the former one
		}
		this.symbolTable.put(name, value);
	}
	public EntryInfoClass c_get(String key)
	{
		return (this.symbolTable.get(key) == null ? null : this.symbolTable.get(key));
	}

	public String get_main_class()
	{
		return this.mainClassName;
	}
	public void set_main_class(String name)
	{
		this.mainClassName = name;
	}
	
	static public Hashtable<String, EntryInfoClass> get_symbol_table()
	{
		return symbolTable;//get the top table, in case we invoke a method from other class or use other class' variable.
	}
	
	public void check_undefined_class(SymbolTable topTable)
	{
		for(String key : symbolTable.keySet())
		{
			this.c_get(key).check_undefined_class(topTable);
		}
	}
	
	public void check_inheritance_loop()
	{
		for(String key : symbolTable.keySet())
		{
			HashSet<String> set = new HashSet<String>();
			set.add(key);
			EntryInfoClass classInfo = this.c_get(key);
			String name = classInfo.get_parent_class();
			while(name != null && !set.contains(name))
			{
				EntryInfoClass tmp = this.c_get(name);
				if(tmp == null) break;
				set.add(name);
				name = tmp.get_parent_class();
			}
			if(name == null)
			{
				classInfo.inherit_from_ancestors(this);
				continue;
			}
			if(set.contains(name))
				ErrorPrinter.add_error(classInfo.get_line_number(), "inheritance loop detected.");
		}
	}
}
