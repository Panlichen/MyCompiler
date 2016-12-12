package minijava.typecheck.symboltable;

import java.util.Hashtable;

import java.util.Vector;

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
import minijava.minijava2piglet.symboltablem2p.*;

public class EntryInfo {
	
	Identifier IDInfo;
	
	public Identifier get_id_info()
	{
		return this.IDInfo;
	}
	public void set_id_info(Identifier nId)
	{
		this.IDInfo = nId;
	}
	public String get_name()
	{
		return this.IDInfo.f0.tokenImage;
	}
	public int get_line_number()
	{
		return this.IDInfo.f0.beginLine;
	}
	
	public boolean is_class_type(String type)
	{//bug fix : TreeVisitor-error.java line #340
		return type != null && type != "int" && type != "int[]" && type != "boolean" && type != "void";//so the type should be a self-defined class, but it can be undefined
	}
	
	public void check_undefined_class(SymbolTable topTable){}
	public void check_inheritance_loop(){}
	
	
	public void inherit_from_ancestors(SymbolTable topTable){}
	

	/*methods in SymbolTable.java*/
	public void c_put(String name, EntryInfoClass value){}
	public EntryInfoClass c_get(String key){return null;}
	public void set_main_class(String name){}
	public String get_main_class(){return null;}
	static public Hashtable<String, EntryInfoClass> get_symbol_table(){return null;}

	/*methods in EntryInfoClass.java*/
	public void v_put(String name, EntryInfoVariable value){}
	public EntryInfoVariable v_get(String key){return null;}
	public void m_put(String name, EntryInfoMethod value){}
	public EntryInfoMethod m_get(String key){return null;}
	public void set_parent_class(String s){}
	public String get_parent_class(){return null;}
	public boolean has_parent(){return false;}
	public Hashtable<String, EntryInfoVariable> get_var_table(){return null;}
	public Hashtable<String, EntryInfoMethod> get_mthd_table(){return null;}
	
	/*methods in EntryInfoMethods.java*/
	//public void v_put(String name, EntryInfoVariable value){}
	//public EntryInfoVariable v_get(String key){return null;}
	public void set_belong_class_name(String s){}
	public String get_belong_class_name(){return null;}
	public void add_member_vars(Hashtable<String, EntryInfoVariable> varTable){};
	public void set_rtn_type(Type theType){}
	public void set_rtn_type(String sType){}//for the main class, we manually set the return type as "void".
	public String get_rtn_type(){return null;}
	public void add_para(String paraType){}
	public Vector<String> get_para_array(){return null;}
	public int get_num_paras(){return 0;}
	public void init_para_idx(){}
	public String next_para(){return null;}
	public boolean para_all_matched(){return false;}
	
	/*methods in EntryInfoVariable.java*/
	public void set_type(Type theType){};
	public String get_type(){return null;}
	
	/*methods in minijava.minijava2piglet*/
	public void v_put_m2p(String name, EntryInfoVariable value){}
	public void m_put_m2p(String name, EntryInfoMethodM2P value){}
	public EntryInfoMethodM2P m_get_m2p(String key){return null;}
	public void c_put_m2p(String name, EntryInfoClassM2P value){}
	public EntryInfoClass c_get_m2p(String key){return null;}
	
	
	
}
