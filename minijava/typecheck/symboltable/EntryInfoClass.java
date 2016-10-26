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

public class EntryInfoClass extends EntryInfo{

	Hashtable<String, EntryInfoVariable> varTable;
	Hashtable<String, EntryInfoMethod> mthdTable;
	String sParentClass;
	int extendsParentsline;//the line number where the extends statement lies, it should be the same with or near the class define line
	
	public void v_put(String name, EntryInfoVariable value)
	{
		if(this.varTable == null)
		{
			this.varTable = new Hashtable<String, EntryInfoVariable>();
		}
		if(this.varTable.get(name) != null)
		{
			ErrorPrinter.add_error(value.get_line_number(), "redefined variable: " + value.get_name() + "in class " + this.get_name());
			//error recovery: we add the redefined variable, and it replaces the former one
		}
		this.varTable.put(name, value);
	}
	public EntryInfoVariable v_get(String key)
	{
		return this.varTable.get(key);
	}
	
	public void m_put(String name, EntryInfoMethod value)
	{
		if(this.mthdTable == null)
		{
			this.mthdTable = new Hashtable<String, EntryInfoMethod>();
		}
		if(this.mthdTable.get(name) != null)
		{
			ErrorPrinter.add_error(value.get_line_number(), "redefined method: " + value.get_name() + "in class " + this.get_name());
			//error recovery: we add the redefined method, and it replaces the former one
		}
		this.mthdTable.put(name, value);
	}
	public EntryInfoMethod m_get(String key)
	{
		return this.mthdTable.get(key);
	}
	
	public void set_parent_class(String s)
	{
		this.sParentClass = s;
		this.extendsParentsline = this.get_line_number();
	}
	public String get_parent_class()
	{
		return this.sParentClass;
	}
	public boolean has_parent()
	{
		return this.sParentClass != null;
	}
	
	public Hashtable<String, EntryInfoMethod> get_mthd_table()
	{
		return this.mthdTable;
	}
	
	public Hashtable<String, EntryInfoVariable> get_var_table()
	{
		return this.varTable;
	}
	
	public void check_undefined_class(SymbolTable topTable)
	{
		if(this.has_parent())
		{
			if(topTable.c_get(this.sParentClass) == null)
			{
				ErrorPrinter.add_error(extendsParentsline, "undifined super class: " + this.sParentClass);
			}
		}
		for(String key : this.varTable.keySet())
		{
			String varType = this.v_get(key).sVarType;
			if(this.is_class_type(varType))
			{
				if(topTable.c_get(varType) == null)
				{
					ErrorPrinter.add_error(this.v_get(key).get_line_number(), "undifined class found in member variables: " + varType);
				}
			}
		}
		for(String key : mthdTable.keySet())
		{
			this.m_get(key).check_undefined_class(topTable);
		}
	}
	
	public void inherit_from_ancestors(SymbolTable topTable)
	{
		String name = this.sParentClass;
		while(name != null)
		{
			EntryInfoClass classInfo = topTable.c_get(name);
			for(String key: classInfo.get_var_table().keySet())
			{
				if(this.v_get(key) == null)
				{
					this.v_put(key, classInfo.v_get(key));
				}
			}
			for(String key: classInfo.get_mthd_table().keySet())
			{
				if(this.m_get(key) == null)
				{
					this.m_put(key, classInfo.m_get(key));
				}
			}
			name = classInfo.get_parent_class();
		}
		
		for(String key : this.mthdTable.keySet())
		{
			this.m_get(key).add_member_vars(varTable);
		}
	}
}
