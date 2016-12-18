package kanga.kanga2mips.codesketch;

import java.util.*;
import java.io.*;

public class MIPSCodeSet{
	private Vector<MIPSCode> codeSet;
	private MIPSCode regAddress;
	private MIPSCode expAddress;
	private String expType;
	
	public MIPSCodeSet()
	{
		this.codeSet = new Vector<MIPSCode>();
	}
	
	public void set_reg_address(String address)
	{
		this.regAddress = new MIPSCode(address);
	}
	public MIPSCode get_reg_address()
	{
		return this.regAddress;
	}
	
	public void set_exp_address(String address)
	{
		this.expAddress = new MIPSCode(address);
	}
	public MIPSCode get_exp_address()
	{
		return this.expAddress;
	}
	
	public void set_expType(String type)
	{
		this.expType = type;
	}
	public String get_expType()
	{
		return this.expType;
	}
	
	public int get_num_code()
	{
		return this.codeSet.size();
	}
	
	public void add_code(MIPSCode code)
	{
		this.codeSet.addElement(code);
	}
	
	public void emit(String sCode)
	{
		this.codeSet.addElement(new MIPSCode(sCode));
	}
	
	public void merge_code_set(MIPSCodeSet set)
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

		String huge = new String();
		

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
