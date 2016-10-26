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
	
	public void v_put(String name, EntryInfoVariable value)
	{
		if(this.varTable == null)
		{
			this.varTable = new Hashtable<String, EntryInfoVariable>();
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
		this.mthdTable.put(name, value);
	}
	public EntryInfoMethod m_get(String key)
	{
		return this.mthdTable.get(key);
	}
	
	public void set_parent_class(String s)
	{
		this.sParentClass = s;
	}
	public String get_parent_class()
	{
		return this.sParentClass;
	}
	public boolean has_parent()
	{
		return this.sParentClass != null;
	}
}
