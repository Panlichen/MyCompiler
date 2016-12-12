package minijava.minijava2piglet.codesketch;

import java.util.*;
import java.io.*;

public class PigletCodeSet extends PigletCodeAbstract{
	private Vector<PigletCode> codeSet;
	private String valueType;
	private PigletCode tempAddress;
	private PigletCode memoryAddress;
	
	private Vector<Integer> nextList;
	private Vector<Integer> trueList;
	private Vector<Integer> falseList;
	
	public PigletCodeSet()
	{
		this.codeSet = new Vector<PigletCode>();
		this.nextList = new Vector<Integer>();
		this.trueList = new Vector<Integer>();
		this.falseList = new Vector<Integer>();
	}
	
	public void set_temp_address(PigletCode address)
	{
		this.tempAddress = address;
	}
	public PigletCode get_temp_address()
	{
		return this.tempAddress;
	}
	
	public void set_memory_address(PigletCode address)
	{
		this.memoryAddress = address;
	}
	public PigletCode get_memory_address()
	{
		return this.memoryAddress;
	}
	
	public void set_value_type(String type)//int, int[], boolean, void, class
	{
		this.valueType = type;
	}
	public String get_value_type()
	{
		return this.valueType;
	}
	
	public int get_num_code()
	{
		return this.codeSet.size();
	}
	
	public void add_code(PigletCode code)
	{
		this.codeSet.addElement(code);
	}
	
	public void emit(String sCode)
	{
		this.codeSet.addElement(new PigletCode(sCode));
	}
	
	public void append_label(String label)
	{
		this.add_code(new PigletCode(label + "\tNOOP"));
	}
	
	public void merge_code_set(PigletCodeSet set)
	{
		if(set == null)
		{
			return;
		}
		int base = this.get_num_code();
		for(int i = 0; i < set.codeSet.size(); i++)
		{
			this.add_code(set.codeSet.elementAt(i));
		}
		for(int i = 0; i < set.nextList.size(); i++)
		{
			this.nextList.addElement(set.nextList.elementAt(i) + base);
		}
		for(int i = 0; i < set.trueList.size(); i++)
		{
			this.trueList.addElement(set.trueList.elementAt(i) + base);
		}
		for(int i = 0; i < set.falseList.size(); i++)
		{
			this.falseList.addElement(set.falseList.elementAt(i) + base);
		}
	}
	
	public boolean has_next()
	{
		return this.nextList.size() > 0;
	}
	
	public void set_back_patch_point(String category, int line)
	{
		switch(category)
		{
		case "nextList" :
			this.nextList.addElement(line);
			break;
		case "trueList" :
			this.trueList.addElement(line);
			break;
		case "falseList" :
			this.falseList.addElement(line);
			break;
		default:
		}
	}
	
	public void back_patch(String category, String label)
	{
		switch(category)
		{
		case "nextList" :
			for(int i = 0; i < this.nextList.size(); i++)
			{
				PigletCode tmpCode = this.codeSet.elementAt(this.nextList.elementAt(i));
				this.codeSet.setElementAt(new PigletCode(tmpCode + label), this.nextList.elementAt(i));
			}
			this.nextList.clear();
			break;
		case "trueList" :
			for(int i = 0; i < this.trueList.size(); i++)
			{
				PigletCode tmpCode = this.codeSet.elementAt(this.trueList.elementAt(i));
				this.codeSet.setElementAt(new PigletCode(tmpCode + label), this.trueList.elementAt(i));
			}
			this.trueList.clear();
			break;
		case "falseList" :
			for(int i = 0; i < this.falseList.size(); i++)
			{
				PigletCode tmpCode = this.codeSet.elementAt(this.falseList.elementAt(i));
				this.codeSet.setElementAt(new PigletCode(tmpCode + label), this.falseList.elementAt(i));
			}
			this.falseList.clear();
			break;
		default:
		}
	}
	
	public void print_all(OutputStream out)
	{
		boolean needTab = false;
		String huge = new String();
		for(int i = 0; i < this.codeSet.size(); i++)
		{
			if(this.codeSet.elementAt(i).toString().equals("END"))
				needTab = false;
			if(needTab)
				huge += "\t";
		
			huge += this.codeSet.elementAt(i) + "\n";
			
			if(this.codeSet.elementAt(i).toString().equals("BEGIN")
				||this.codeSet.elementAt(i).toString().equals("MAIN"))
				needTab = true;
		}
		try
		{
			out.write(huge.getBytes());
			System.out.println(huge);
		}
		catch(Exception e){}
	}
}
