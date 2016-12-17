package spiglet.spiglet2kanga.codesketch;

import java.util.*;
import java.io.*;

public class KangaCodeSet{
	private Vector<KangaCode> codeSet;
	private KangaCode regAddress;
	private KangaCode expAddress;
	private KangaCode memoryAddress;
	
	public KangaCodeSet()
	{
		this.codeSet = new Vector<KangaCode>();
	}
	
	public void set_reg_address(String address)
	{
		this.regAddress = new KangaCode(address);
	}
	public KangaCode get_reg_address()
	{
		return this.regAddress;
	}
	
	public void set_exp_address(String address)
	{
		this.expAddress = new KangaCode(address);
	}
	public KangaCode get_exp_address()
	{
		return this.expAddress;
	}
	
	public void set_memory_address(String address)
	{
		this.memoryAddress = new KangaCode(address);
	}
	public KangaCode get_memory_address()
	{
		return this.memoryAddress;
	}
	
	public int get_num_code()
	{
		return this.codeSet.size();
	}
	
	public void add_code(KangaCode code)
	{
		this.codeSet.addElement(code);
	}
	
	public void emit(String sCode)
	{
		this.codeSet.addElement(new KangaCode(sCode));
	}
	
	public void merge_code_set(KangaCodeSet set)
	{
		if(set == null)
		{
			return;
		}
		for(int i = 0; i < set.codeSet.size(); i++)
		{
			this.add_code(set.codeSet.elementAt(i));
		}
	}
	
	public void print_all(OutputStream out)
	{
		boolean needTab = false;
		boolean needSpace = false;
		String huge = new String();
		for(int i = 0; i < this.codeSet.size(); i++)
		{
			if(this.codeSet.elementAt(i).toString().equals("END"))
				needTab = false;
			if(this.codeSet.elementAt(i).isLabel())
			{
				needTab = false;
				needSpace = true;
			}
			if(needTab)
				huge += "\t";
			if(needSpace)
				huge += "  ";
		
			huge += this.codeSet.elementAt(i) + "\n";
			
			if(this.codeSet.elementAt(i).toString().endsWith("]"))
				needTab = true;
			if(this.codeSet.elementAt(i).isLabel())
			{
				needTab = true;
				needSpace = false;
			}
		}
		try
		{
			/*
			File file = new File("D:\\PKU\\compile_practice\\testCasesKanga\\trivial1.kg");
			FileOutputStream fos = new FileOutputStream(file, true);
			OutputStreamWriter osWriter = new OutputStreamWriter(fos);
            BufferedWriter bWriter = new BufferedWriter(osWriter);
			bWriter.write(huge);
			bWriter.flush();
			
			
			bWriter.close();
			*/
			out.write(huge.getBytes());
			System.out.println(huge);
		}
		catch(Exception e){}
	}
}
