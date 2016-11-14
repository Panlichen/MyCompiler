package minijava.typecheck.symboltable;

import java.util.Hashtable;
import minijava.syntaxtree.AllocationExpression;
import minijava.syntaxtree.AndExpression;
import minijava.syntaxtree.ArrayAllocationExpression;
import minijava.syntaxtree.ArrayAssignmentStatement;
import minijava.syntaxtree.ArrayLength;
import minijava.syntaxtree.ArrayLookup;
import minijava.syntaxtree.ArrayType;
import minijava.syntaxtree.AssignmentStatement;
import minijava.syntaxtree.Block;
import minijava.syntaxtree.BooleanType;
import minijava.syntaxtree.BracketExpression;
import minijava.syntaxtree.ClassDeclaration;
import minijava.syntaxtree.ClassExtendsDeclaration;
import minijava.syntaxtree.CompareExpression;
import minijava.syntaxtree.Expression;
import minijava.syntaxtree.ExpressionList;
import minijava.syntaxtree.ExpressionRest;
import minijava.syntaxtree.FalseLiteral;
import minijava.syntaxtree.FormalParameter;
import minijava.syntaxtree.FormalParameterList;
import minijava.syntaxtree.FormalParameterRest;
import minijava.syntaxtree.Goal;
import minijava.syntaxtree.Identifier;
import minijava.syntaxtree.IfStatement;
import minijava.syntaxtree.IntegerLiteral;
import minijava.syntaxtree.IntegerType;
import minijava.syntaxtree.MainClass;
import minijava.syntaxtree.MessageSend;
import minijava.syntaxtree.MethodDeclaration;
import minijava.syntaxtree.MinusExpression;
import minijava.syntaxtree.NodeChoice;
import minijava.syntaxtree.NodeListOptional;
import minijava.syntaxtree.NodeOptional;
import minijava.syntaxtree.NodeToken;
import minijava.syntaxtree.NotExpression;
import minijava.syntaxtree.PlusExpression;
import minijava.syntaxtree.PrimaryExpression;
import minijava.syntaxtree.PrintStatement;
import minijava.syntaxtree.Statement;
import minijava.syntaxtree.ThisExpression;
import minijava.syntaxtree.TimesExpression;
import minijava.syntaxtree.TrueLiteral;
import minijava.syntaxtree.Type;
import minijava.syntaxtree.TypeDeclaration;
import minijava.syntaxtree.VarDeclaration;
import minijava.syntaxtree.WhileStatement;


public class EntryInfoMethod extends EntryInfo{

	String sRtnType;
	String sBelongClassName;
	Hashtable<String, EntryInfoVariable> varTable;// symbol for vars and paras
	String[] paraType;//record the para list
	int paraNum = 0;
	
	public EntryInfoMethod()
	{
		this.varTable = new Hashtable<String, EntryInfoVariable>();
	}
	
	/*for typecheck*/
	int paraIdx;
	public void init_para_idx()
	{
		this.paraIdx = 0;
	}
	public String next_para()
	{
		if(this.paraIdx >= this.paraNum) return null;
		return paraType[paraIdx++];
	}
	public boolean para_all_matched()
	{
		return this.paraIdx == this.paraNum;
	}
	/*end*/

	public void set_belong_class_name(String s)
	{
		this.sBelongClassName = s;
	}
	public String get_belong_class_name()
	{
		return this.sBelongClassName;
	}
	
	public void set_rtn_type(Type theType)
	{
		this.sRtnType = Type2String.type_to_string(theType);
	}
	public void set_rtn_type(String sType)
	{
		this.sRtnType = sType;
	}
	public String get_rtn_type()
	{
		return this.sRtnType;
	}
	
	public void v_put(String name, EntryInfoVariable value)
	{
		if(this.varTable == null)
		{
			this.varTable = new Hashtable<String, EntryInfoVariable>();
		}
		if(this.varTable.get(name) != null)
		{
			ErrorPrinter.add_error(value.get_line_number(), "redefined variable: " + value.get_name() + "in method " + this.get_name());
			//error recovery: we add the redefined variable, and it replaces the former one
		}
		this.varTable.put(name, value);
	}
	public EntryInfoVariable v_get(String key)
	{
		return this.varTable.get(key);
	}
	
	public void add_para(String paraType)
	{
		this.paraType[paraNum++] = paraType;
	}
	public String[] get_para_array()
	{
		return this.paraType;
	}
	
	public int get_num_paras()
	{
		return this.paraNum;
	}
	
	
	public Hashtable<String, EntryInfoVariable> get_var_table()
	{
		return this.varTable;
	}
	
	public void add_member_vars(Hashtable<String, EntryInfoVariable> varTable)
	{
		if(varTable != null)
			for(String key : varTable.keySet())
			{
				if(this.v_get(key) == null)
				{
					this.v_put(key, varTable.get(key));
				}
			}
	}
	
	public void check_undefined_class(SymbolTable topTable)
	{
		if(this.is_class_type(this.sRtnType))
		{
			if(topTable.c_get(sRtnType) == null)
			{
				ErrorPrinter.add_error(this.get_line_number(), "unddfined return type: " + this.sRtnType);
			}
		}
		if(this.varTable != null)
			for(String key : this.varTable.keySet())
			{
				String type = this.v_get(key).get_type();
				if(this.is_class_type(type))
				{
					if(topTable.c_get(type) == null)
					{
						ErrorPrinter.add_error(this.v_get(key).get_line_number(), "undefined class: " + type);
					}
				}
			}
	}
}
