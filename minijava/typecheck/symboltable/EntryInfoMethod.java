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

	Type rtnType;
	Hashtable<String, EntryInfoVariable> varTable;// symbol for vars and paras
	Type[] paraType;//record the para list
	int numParas;

	public void setRtnType(Type theType)
	{
		this.rtnType = theType;
	}
	public Type getRtnType()
	{
		return this.rtnType;
	}
	
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
	
	public void addPara(int idx, Type paraType)
	{
		if(idx > 0 && idx < numParas)
			this.paraType[idx] = paraType;
	}
	public Type[] getParaArray()
	{
		return this.paraType;
	}
	
	public void set_numParas(int n)
	{
		this.numParas = n;
	}
	public int get_numparas()
	{
		return this.numParas;
	}
	
}
