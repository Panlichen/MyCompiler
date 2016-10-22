package minijava.typecheck.symboltable;

import java.util.Hashtable;

public class TopTable implements Table{
	public Hashtable<String, ClassEntryInfo> top;
	protected String mainClassName;
	
	public TopTable()
	{
		top = new Hashtable<String, ClassEntryInfo>();
	}
	
	public void addEntry(String name, EntryInfo ceInfo)
	{
		
	}
	
	public void setMainClass(String name)
	{
		this.mainClassName = name;
	}
	
	public String getMainClass()
	{
		return this.mainClassName;
	}
}
