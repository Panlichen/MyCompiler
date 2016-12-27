package minijava.minijava2piglet.visitor;

import java.util.*;
import java.io.*;
import java.lang.Integer;

import minijava.minijava2piglet.symboltablem2p.*;
import minijava.minijava2piglet.codesketch.*;
import minijava.syntaxtree.*;
import minijava.typecheck.symboltable.*;
import minijava.visitor.*;

public class VisitorTranslateM2P extends GJDepthFirst<PigletCodeAbstract, EntryInfo> implements ResourceManager{
	private int tempIndex;
	private int labelIndex;
	private OutputStream out;
	private Vector<PigletCodeSet> messageSendParasVecCodeSet;
	
	public VisitorTranslateM2P(OutputStream out)
	{
		this.tempIndex = 21;
		this.labelIndex = 0;
		this.out = out;
		this.messageSendParasVecCodeSet = new Vector<PigletCodeSet>();		
	}
	
	public void init_temp_idx(int i)
	{
		this.tempIndex = i;
	}
	
	@Override
	public String get_new_label()
	{
		return "_label_" + this.labelIndex++ + "_";
	}
	@Override
	public String get_new_temp()
	{
		return "TEMP " + this.tempIndex++;
	}
	public String get_int_type()
	{
		return "int";
	}
	public String get_array_type()
	{
		return "int[]";
	}
	public String get_boolean_type()
	{
		return "boolean";
	}
	
	
	/**
	 * Represents a grammar list, e.g. ( A )+
	 */
	public PigletCodeAbstract visit(NodeList n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
		{
			PigletCodeSet tmpSet = (PigletCodeSet)e.nextElement().accept(this, argu);
			ret.merge_code_set(tmpSet);
		}
		return ret;
	}
	
	/**
	 * Represents an optional grammar list, e.g. ( A )*
	 */
	public PigletCodeAbstract visit(NodeListOptional n, EntryInfo argu)
	{
		if(n.present())
		{
			PigletCodeSet ret = new PigletCodeSet();
			for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			{
				PigletCodeSet tmpSet = (PigletCodeSet)e.nextElement().accept(this, argu);
				ret.merge_code_set(tmpSet);
			}
			return ret;
		}
		else
			return null;
	}
	
	/**
	* f0 -> MainClass()
	* f1 -> ( TypeDeclaration() )*
	* f2 -> <EOF>
	*/
	public PigletCodeAbstract visit(Goal n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return null;
	}
	
	/**
	* f0 -> "class"
	* f1 -> Identifier()
	* f2 -> "{"
	* f3 -> "public"
	* f4 -> "static"
	* f5 -> "void"
	* f6 -> "main"
	* f7 -> "("
	* f8 -> "String"
	* f9 -> "["
	* f10 -> "]"
	* f11 -> Identifier()
	* f12 -> ")"
	* f13 -> "{"
	* f14 -> PrintStatement()
	* f15 -> "}"
	* f16 -> "}"
	*/
	public PigletCodeAbstract visit(MainClass n, EntryInfo argu)//argu should be a topTable
	{
		PigletCodeSet mainClassCodeSet = new PigletCodeSet();
		mainClassCodeSet.emit("MAIN");
		
		EntryInfoClassM2P classInfo = SymbolTableM2P.symbolTableM2P.get(argu.get_main_class());
		PigletCodeSet printStatementCodeSet = (PigletCodeSet)n.f14.accept(this, classInfo);
		
		mainClassCodeSet.merge_code_set(printStatementCodeSet);
		mainClassCodeSet.emit("END");
		
		mainClassCodeSet.print_all(this.out);
		return null;
		
	}
	
	/**
	* f0 -> "class"
	* f1 -> Identifier()
	* f2 -> "{"
	* f3 -> ( VarDeclaration() )*
	* f4 -> ( MethodDeclaration() )*
	* f5 -> "}"
	*/
	public PigletCodeAbstract visit(ClassDeclaration n, EntryInfo argu)
	{
		PigletCodeAbstract ret = null;
		EntryInfoClassM2P classInfo = (EntryInfoClassM2P) argu.c_get_m2p(n.f1.f0.tokenImage);
		n.f4.accept(this, classInfo);
		return ret;
	}
	
	/**
	* f0 -> "class"
	* f1 -> Identifier()
	* f2 -> "extends"
	* f3 -> Identifier()
	* f4 -> "{"
	* f5 -> ( VarDeclaration() )*
	* f6 -> ( MethodDeclaration() )*
	* f7 -> "}"
	*/
	public PigletCodeAbstract visit(ClassExtendsDeclaration n, EntryInfo argu)
	{
		PigletCodeAbstract ret = null;
		EntryInfoClassM2P classInfo = (EntryInfoClassM2P) argu.c_get_m2p(n.f1.f0.tokenImage);
		n.f6.accept(this, classInfo);
		return ret;
	}
	
	/**
	* f0 -> "public"
	* f1 -> Type()
	* f2 -> Identifier()
	* f3 -> "("
	* f4 -> ( FormalParameterList() )?
	* f5 -> ")"
	* f6 -> "{"
	* f7 -> ( VarDeclaration() )*
	* f8 -> ( Statement() )*
	* f9 -> "return"
	* f10 -> Expression()
	* f11 -> ";"
	* f12 -> "}"
	*/
	public PigletCodeAbstract visit(MethodDeclaration n, EntryInfo argu)
	{
		PigletCodeSet methodCodeSet = new PigletCodeSet();
		EntryInfoMethodM2P methodInfo = (EntryInfoMethodM2P) argu.m_get_m2p(n.f2.f0.tokenImage);
		
		this.init_temp_idx(methodInfo.get_num_local_variable() + 1);
		
		//think there are less than 20 parameters
		methodCodeSet.emit(methodInfo.get_belong_class_name() + "_" + n.f2.f0.tokenImage + "\t[ " + (methodInfo.get_num_paras() + 1) + " ] ");
		
		methodCodeSet.emit("BEGIN");
		
		for(int i = 1 + methodInfo.get_num_paras(); i <= methodInfo.get_num_local_variable(); i++)
		{
			methodCodeSet.emit("MOVE TEMP " + i + " 0");
		}
		
		PigletCodeSet statementCodeSet = (PigletCodeSet) n.f8.accept(this, methodInfo);
		methodCodeSet.merge_code_set(statementCodeSet);
		
		PigletCodeSet rtnExpressionCodeSet = (PigletCodeSet) n.f10.accept(this, methodInfo);
		methodCodeSet.merge_code_set(rtnExpressionCodeSet);
		
		methodCodeSet.emit("RETURN " + rtnExpressionCodeSet.get_temp_address());
		methodCodeSet.emit("END");
		
		methodCodeSet.print_all(this.out);
		return null;
		
	}
	
	/**
	* f0 -> Block()
	*       | AssignmentStatement()
	*       | ArrayAssignmentStatement()
	*       | IfStatement()
	*       | WhileStatement()
	*       | PrintStatement()
	*/
	public PigletCodeAbstract visit(Statement n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f0.accept(this, argu);
		if(ret.has_next())
		{
			String stmtNextLabel = this.get_new_label();
			ret.back_patch("nextList", stmtNextLabel);
			ret.append_label(stmtNextLabel);
		}
		return ret;
	}
	
	/**
	* f0 -> "{"
	* f1 -> ( Statement() )*
	* f2 -> "}"
	*/
	public PigletCodeAbstract visit(Block n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f1.accept(this, argu);
		return ret;
	}
	
	/**
	* f0 -> Identifier()
	* f1 -> "="
	* f2 -> Expression()
	* f3 -> ";"
	*/
	public PigletCodeAbstract visit(AssignmentStatement n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet expCodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		ret.merge_code_set(expCodeSet);
		
		PigletCodeSet IDInfo = ((EntryInfoMethodM2P) argu).get_variable_info(n.f0.f0.tokenImage, this);
		
		if(IDInfo.get_memory_address() == null)//local variable
		{
			ret.emit("MOVE " + IDInfo.get_temp_address() + " " + expCodeSet.get_temp_address());
		}
		else
		{
			ret.emit("HSTORE " + IDInfo.get_memory_address() + " " + expCodeSet.get_temp_address());
		}
		
		return ret;
	}
	
	/**
	* f0 -> Identifier()
	* f1 -> "["
	* f2 -> Expression()
	* f3 -> "]"
	* f4 -> "="
	* f5 -> Expression()
	* f6 -> ";"
	*/
	public PigletCodeAbstract visit(ArrayAssignmentStatement n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet IDCodeSet = ((EntryInfoMethodM2P) argu).get_variable_info(n.f0.f0.tokenImage, this);
		ret.merge_code_set(IDCodeSet);
		PigletCodeSet idxCodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		ret.merge_code_set(idxCodeSet);
		PigletCodeSet expCodeSet = (PigletCodeSet) n.f5.accept(this, argu);
		ret.merge_code_set(expCodeSet);
		
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		ret.emit("HLOAD " + tempAdd + " " + IDCodeSet.get_temp_address() + " 0");
		//now tempAdd holds the length
		
		String legalLabel = this.get_new_label();
		
		ret.emit("CJUMP " + "LT " + tempAdd + " PLUS 1 " + idxCodeSet.get_temp_address() + " " + legalLabel);
		ret.emit("ERROR");
		
		ret.append_label(legalLabel);
		ret.emit("HSTORE PLUS " + IDCodeSet.get_temp_address() + " TIMES PLUS " + idxCodeSet.get_temp_address() + " 1 4 0 " + expCodeSet.get_temp_address());
		
		
		return ret;
		
	}
	
	/**
	* f0 -> "if"
	* f1 -> "("
	* f2 -> Expression()
	* f3 -> ")"
	* f4 -> Statement()
	* f5 -> "else"
	* f6 -> Statement()
	*/
	public PigletCodeAbstract visit(IfStatement n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet condCodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		PigletCodeSet stat1CodeSet = (PigletCodeSet) n.f4.accept(this, argu);
		PigletCodeSet stat2CodeSet = (PigletCodeSet) n.f6.accept(this, argu);
		ret.merge_code_set(condCodeSet);
		//maybe we can add some true/false labels in the condCodeSet
		
		String falseLabel = this.get_new_label();
		ret.emit("CJUMP " + condCodeSet.get_temp_address() + " " + falseLabel);
		
		ret.merge_code_set(stat1CodeSet);
		ret.emit("JUMP ");
		ret.set_back_patch_point("nextList", ret.get_num_code() - 1);
		
		ret.append_label(falseLabel);
		
		ret.merge_code_set(stat2CodeSet);
		return ret;
	}
	
	/**
	* f0 -> "while"
	* f1 -> "("
	* f2 -> Expression()
	* f3 -> ")"
	* f4 -> Statement()
	*/
	public PigletCodeAbstract visit(WhileStatement n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		String startLabel = this.get_new_label();
		String falseLabel = this.get_new_label();
		
		ret.append_label(startLabel);
		
		PigletCodeSet expCodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		PigletCodeSet statCodeSet = (PigletCodeSet) n.f4.accept(this, argu);
		
		ret.merge_code_set(expCodeSet);
		ret.emit("CJUMP " + expCodeSet.get_temp_address() + " "  + falseLabel);
		
		ret.merge_code_set(statCodeSet);
		ret.emit("JUMP " + startLabel);
		ret.append_label(falseLabel);
		
		return ret;
	}
	
	/**
	* f0 -> "System.out.println"
	* f1 -> "("
	* f2 -> Expression()
	* f3 -> ")"
	* f4 -> ";"
	*/
	public PigletCodeAbstract visit(PrintStatement n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet expCodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		ret.merge_code_set(expCodeSet);
		ret.emit("PRINT " + expCodeSet.get_temp_address());
		
		return ret;
	}
	
	/**
	* f0 -> AndExpression()
	*       | CompareExpression()
	*       | PlusExpression()
	*       | MinusExpression()
	*       | TimesExpression()
	*       | ArrayLookup()
	*       | ArrayLength()
	*       | MessageSend()
	*       | PrimaryExpression()
	*/
	public PigletCodeAbstract visit(Expression n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f0.accept(this, argu);
		return ret;
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "&&"
	* f2 -> PrimaryExpression()
	*/
	public PigletCodeAbstract visit(AndExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		
		PigletCodeSet expCodeSet1 = (PigletCodeSet) n.f0.accept(this, argu);
		PigletCodeSet expCodeSet2 = (PigletCodeSet) n.f2.accept(this, argu);
		String falseLabel = this.get_new_label();
		String doneLabel =  this.get_new_label();
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		
		ret.merge_code_set(expCodeSet1);
		ret.emit("CJUMP " + expCodeSet1.get_temp_address() + " " + falseLabel);
		
		ret.merge_code_set(expCodeSet2);
		ret.emit("CJUMP " + expCodeSet2.get_temp_address() + " " + falseLabel);
		
		ret.emit("MOVE " + tempAdd + " 1");
		ret.emit("JUMP " + doneLabel);
		
		ret.append_label(falseLabel);
		ret.emit("MOVE " + tempAdd + " 0");
		
		ret.append_label(doneLabel);
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_boolean_type());
		
		return ret;
		
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "<"
	* f2 -> PrimaryExpression()
	*/
	public PigletCodeAbstract visit(CompareExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet exp1CodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		PigletCodeSet exp2CodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		
		ret.merge_code_set(exp1CodeSet);
		ret.merge_code_set(exp2CodeSet);
		ret.emit("MOVE " + tempAdd + " LT " + exp1CodeSet.get_temp_address() + " " + exp2CodeSet.get_temp_address());
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_boolean_type());
		
		return ret;
		
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "+"
	* f2 -> PrimaryExpression()
	*/
	public PigletCodeAbstract visit(PlusExpression n, EntryInfo argu)
	{
		PigletCodeSet ret  = new PigletCodeSet();
		PigletCodeSet exp1CodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		PigletCodeSet exp2CodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		
		ret.merge_code_set(exp1CodeSet);
		ret.merge_code_set(exp2CodeSet);
		ret.emit("MOVE " + tempAdd + " PLUS " + exp1CodeSet.get_temp_address() + " " + exp2CodeSet.get_temp_address());
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_int_type());
		
		return ret;
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "-"
	* f2 -> PrimaryExpression()
	*/
	public PigletCodeAbstract visit(MinusExpression n, EntryInfo argu)
	{
		PigletCodeSet ret  = new PigletCodeSet();
		PigletCodeSet exp1CodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		PigletCodeSet exp2CodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		
		ret.merge_code_set(exp1CodeSet);
		ret.merge_code_set(exp2CodeSet);
		ret.emit("MOVE " + tempAdd + " MINUS " + exp1CodeSet.get_temp_address() + " " + exp2CodeSet.get_temp_address());
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_int_type());
		
		return ret;
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "*"
	* f2 -> PrimaryExpression()
	*/
	public PigletCodeAbstract visit(TimesExpression n, EntryInfo argu)
	{
		PigletCodeSet ret  = new PigletCodeSet();
		PigletCodeSet exp1CodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		PigletCodeSet exp2CodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		
		ret.merge_code_set(exp1CodeSet);
		ret.merge_code_set(exp2CodeSet);
		ret.emit("MOVE " + tempAdd + " TIMES " + exp1CodeSet.get_temp_address() + " " + exp2CodeSet.get_temp_address());
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_int_type());
		
		return ret;
	}
	

	/**
	* f0 -> PrimaryExpression()
	* f1 -> "["
	* f2 -> PrimaryExpression()
	* f3 -> "]"
	*/
	public PigletCodeAbstract visit(ArrayLookup n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet nameCodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		PigletCodeSet idxCodeSet = (PigletCodeSet) n.f2.accept(this, argu);
		
		ret.merge_code_set(nameCodeSet);
		ret.merge_code_set(idxCodeSet);
		
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		ret.emit("HLOAD " + tempAdd + " " + nameCodeSet.get_temp_address() + " 0");
		//now tempAdd holds the length
		
		String legalLabel = this.get_new_label();
		
		ret.emit("CJUMP " + "LT " + tempAdd + " PLUS 1 " + idxCodeSet.get_temp_address() + " " + legalLabel);
		ret.emit("ERROR");
		
		ret.append_label(legalLabel);
		ret.emit("HLOAD " + tempAdd + " PLUS " + nameCodeSet.get_temp_address() + " TIMES PLUS " 
				+ idxCodeSet.get_temp_address() + " 1 4 0");
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_int_type());
		
		return ret;
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "."
	* f2 -> "length"
	*/
	public PigletCodeAbstract visit(ArrayLength n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet nameCodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		ret.merge_code_set(nameCodeSet);
		
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		ret.emit("HLOAD " + tempAdd + " " + nameCodeSet.get_temp_address() + " 0");
		
		ret.set_temp_address(tempAdd);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_int_type());
		
		return ret;		
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "."
	* f2 -> Identifier()
	* f3 -> "("
	* f4 -> ( ExpressionList() )?
	* f5 -> ")"
	*/
	public PigletCodeAbstract visit(MessageSend n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet instCodeSet = (PigletCodeSet) n.f0.accept(this, argu);
		//the visitor of ExpressionList will help to fill the vector.
		PigletCodeSet paraCodeSet = (PigletCodeSet) n.f4.accept(this, argu);
		ret.merge_code_set(instCodeSet);
		ret.merge_code_set(paraCodeSet);
	

		String instBelongClassName = instCodeSet.get_value_type();
		//totally rely on the implementation of the visitor for Identifier. 
		
		EntryInfoClassM2P classInfo = SymbolTableM2P.symbolTableM2P.get(instBelongClassName);
		int methodOffset = classInfo.get_method_zone().get_method_offset(n.f2.f0.tokenImage);
		
		PigletCode tempAdd = new PigletCode(this.get_new_temp());
		ret.emit("HLOAD " + tempAdd + " " + instCodeSet.get_temp_address() + " 0");//er ci jian zhi (Chinese PinYin)
		//now the tempAdd holds the address of the method table
		
		PigletCode tempAdd1 = new PigletCode(this.get_new_temp());
		ret.emit("HLOAD " + tempAdd1 + " " + tempAdd + " " + (methodOffset * 4));
		//now we get the entry of the method.
		
		String strPara = "( " + instCodeSet.get_temp_address() + " ";
		for(int i = 0; i < this.messageSendParasVecCodeSet.size(); i++)
		{
			strPara += this.messageSendParasVecCodeSet.elementAt(i).get_temp_address() + " ";
		}
		strPara += ")";
		PigletCode tempAdd2 = new PigletCode(this.get_new_temp());
		ret.emit("MOVE " + tempAdd2 + " " + "CALL " + tempAdd1 + " " + strPara);
		
		ret.set_temp_address(tempAdd2);
		ret.set_memory_address(null);
		
		String rtnType = classInfo.m_get_m2p(n.f2.f0.tokenImage).get_rtn_type();
		ret.set_value_type(rtnType);
		
		messageSendParasVecCodeSet.removeAllElements();
		
		return ret;
	}

	/**
	* f0 -> Expression()
	* f1 -> ( ExpressionRest() )*
	*/
	public PigletCodeAbstract visit(ExpressionList n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f0.accept(this, argu);
		this.messageSendParasVecCodeSet.addElement(ret);
		PigletCodeSet restCodeSet = (PigletCodeSet) n.f1.accept(this, argu);
		ret.merge_code_set(restCodeSet);
		return ret;
	}
	
	/**
	* f0 -> ","
	* f1 -> Expression()
	*/
	public PigletCodeAbstract visit(ExpressionRest n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f1.accept(this, argu);
		this.messageSendParasVecCodeSet.addElement(ret);
		return ret;
	}
	

	/**
	* f0 -> IntegerLiteral()
	*       | TrueLiteral()
	*       | FalseLiteral()
	*       | Identifier()
	*       | ThisExpression()
	*       | ArrayAllocationExpression()
	*       | AllocationExpression()
	*       | NotExpression()
	*       | BracketExpression()
	*/
	public PigletCodeAbstract visit(PrimaryExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f0.accept(this, argu);
		return ret;
	}
	
	/**
	* f0 -> <INTEGER_LITERAL>
	*/
	public PigletCodeAbstract visit(IntegerLiteral n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		
		ret.set_temp_address(new PigletCode( n.f0.tokenImage ));
		ret.set_memory_address(null);
		ret.set_value_type(this.get_int_type());
		return ret;
	}
	
	/**
	* f0 -> "true"
	*/
	public PigletCodeAbstract visit(TrueLiteral n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		
		ret.set_temp_address(new PigletCode(" 1"));
		ret.set_memory_address(null);
		ret.set_value_type(this.get_boolean_type());
		return ret;
	}
	
	/**
	* f0 -> "false"
	*/
	public PigletCodeAbstract visit(FalseLiteral n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		
		ret.set_temp_address(new PigletCode(" 0"));
		ret.set_memory_address(null);
		ret.set_value_type(this.get_boolean_type());
		return ret;
	}
	
	/**
	* f0 -> <IDENTIFIER>
	*/
	public PigletCodeSet visit(Identifier n, EntryInfo argu)
	{
		PigletCodeSet ret = ((EntryInfoMethodM2P) argu).get_variable_info(n.f0.tokenImage, this);
		return ret;
	}
	
	/**
	* f0 -> "this"
	*/
	public PigletCodeSet visit(ThisExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCode tempCode = new PigletCode(this.get_new_temp());
		ret.emit("MOVE " + tempCode + " TEMP 0");
		
		ret.set_temp_address(tempCode);
		ret.set_memory_address(null);
		ret.set_value_type(((EntryInfoMethodM2P) argu).get_belong_class_name());
		return ret;
	}
	
	/**
	* f0 -> "new"
	* f1 -> "int"
	* f2 -> "["
	* f3 -> Expression()
	* f4 -> "]"
	*/
	public PigletCodeSet visit(ArrayAllocationExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		PigletCodeSet sizeCodeSet = (PigletCodeSet) n.f3.accept(this, argu);
		ret.merge_code_set(sizeCodeSet);
		
		//allocate memory for the array
		PigletCode baseTemp = new PigletCode(this.get_new_temp());
		ret.emit("MOVE " + baseTemp + " HALLOCATE TIMES PLUS " + sizeCodeSet.get_temp_address() + " 1 4");
		//now base holds the base address of the array
		ret.emit("HSTORE " + baseTemp + " 0 " + sizeCodeSet.get_temp_address());
		//store the length
		
		//set initial value as 0 for the array
		PigletCode idxTemp = new PigletCode(this.get_new_temp());
		String startLabel = this.get_new_label();
		String doneLabel = this.get_new_label();
		
		ret.emit("MOVE " + idxTemp + " 4");
		ret.append_label(startLabel);
		ret.emit("CJUMP " + "LT " + idxTemp + " TIMES PLUS " + sizeCodeSet.get_temp_address() + " 1 4 " + doneLabel);
		ret.emit("HSTORE PLUS " + baseTemp + " " + idxTemp + " 0 0");
		ret.emit("MOVE " + idxTemp + " PLUS " + idxTemp + " 4");
		ret.emit("JUMP " + startLabel);
		ret.append_label(doneLabel);
		
		ret.set_temp_address(baseTemp);
		ret.set_memory_address(null);
		ret.set_value_type(this.get_array_type());
		return ret;		
	}
	
	/**
	* f0 -> "new"
	* f1 -> Identifier()
	* f2 -> "("
	* f3 -> ")"
	*/
	public PigletCodeAbstract visit(AllocationExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = new PigletCodeSet();
		EntryInfoClassM2P classInfo = SymbolTableM2P.symbolTableM2P.get(n.f1.f0.tokenImage);
		
		int sizeVarsPlus = 4 * (classInfo.get_num_member_variable() + 1);
		int sizeMthds = 4 * (classInfo.get_method_zone().get_num_method());
		
		PigletCode classBaseTemp = new PigletCode(this.get_new_temp());
		PigletCode mthdsBaseTemp = new PigletCode(this.get_new_temp());
		
		ret.emit("MOVE " + classBaseTemp + " HALLOCATE " + sizeVarsPlus);
		ret.emit("MOVE " + mthdsBaseTemp + " HALLOCATE " + sizeMthds);
		
		ret.emit("HSTORE " + classBaseTemp + " 0 " + mthdsBaseTemp);
		for(int i = 4; i < sizeVarsPlus; i += 4)
		{
			ret.emit("HSTORE " + classBaseTemp + " " + i + " 0");
		}
		
		for(int i = 0; i < sizeMthds; i += 4)
		{
			ret.emit("HSTORE " + mthdsBaseTemp + " " + i + " " +
				classInfo.get_method_zone().get_method_label(classInfo.get_method_zone().get_method_vector().elementAt(i / 4)));
		}
		
		ret.set_temp_address(classBaseTemp);
		ret.set_memory_address(null);
		ret.set_value_type(n.f1.f0.tokenImage);
		return ret;
	}
	
	/**
	* f0 -> "!"
	* f1 -> Expression()
	*/
	public PigletCodeAbstract visit(NotExpression n, EntryInfo argu)
	{
		PigletCodeSet ret = (PigletCodeSet) n.f1.accept(this, argu);
		PigletCode tempCode = new PigletCode(this.get_new_temp());
		ret.emit("MOVE " + tempCode + " MINUS 1 " + ret.get_temp_address());
		
		ret.set_temp_address(tempCode);
		ret.set_memory_address(null);
		ret.set_value_type(get_boolean_type());
		
		return ret;
	}
	
	/**
	* f0 -> "("
	* f1 -> Expression()
	* f2 -> ")"
	*/
	public PigletCodeAbstract visit(BracketExpression n, EntryInfo argu)
	{
		return (PigletCodeSet) n.f1.accept(this, argu);
	}
	
}
