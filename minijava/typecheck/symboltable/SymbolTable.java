package minijava.typecheck.symboltable;
import java.util.Hashtable;

public class SymbolTable extends EntryInfo{
	public Hashtable<String, EntryInfoClass> symbolTable;
	public String mainClassName;
	
	public void c_put(String name, EntryInfoClass value)
	{
		if(this.symbolTable == null)
		{
			this.symbolTable = new Hashtable<String, EntryInfoClass>();
		}
		this.symbolTable.put(name, value);
	}
	public EntryInfoClass c_get(String key)
	{
		return this.symbolTable.get(key);
	}

	public String get_main_class()
	{
		return this.mainClassName;
	}
	public void set_main_class(String name)
	{
		this.mainClassName = name;
	}
	
	public Hashtable<String, EntryInfoClass> get_symbol_table()
	{
		return this.symbolTable;//get the top table, in case we invoke a method from other class or use other class' variable.
	}
}
