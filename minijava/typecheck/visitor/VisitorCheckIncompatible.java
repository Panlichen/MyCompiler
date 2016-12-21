package minijava.typecheck.visitor;

import java.util.*;
import minijava.visitor.*;
import minijava.syntaxtree.*;
import minijava.typecheck.symboltable.*;

public class VisitorCheckIncompatible extends GJDepthFirst<String, EntryInfo>{
	private Stack<EntryInfoMethod> messageTable = new Stack<EntryInfoMethod>();
	
	public boolean is_match(EntryInfo argu, String type1, String type2)
	{
		if(type1 == null || type2 == null)
		{
			return false;
		}
		if((argu.is_class_type(type1) && !argu.is_class_type(type2))
				|| (!argu.is_class_type(type1) && argu.is_class_type(type2)))
			return false;
		else if(argu.is_class_type(type1) && argu.is_class_type(type2))//���԰�����Ķ��󸳸����ࡣ������Ӣ��˵����type1 can be father of type2
		{
			String name = type2;
			while(name != null)
			{
				if(type1.equals(name))
					return true;
				if(SymbolTable.get_symbol_table().get(name) == null) 
					return false;
				name = SymbolTable.get_symbol_table().get(name).get_parent_class();
			}
			return false;
		}
		else return type1.equals(type2);
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
	public String visit(MainClass n, EntryInfo argu)//here argu should be the topTable
	{
		String _ret = null;
		EntryInfoClass classInfo = argu.c_get(argu.get_main_class());
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		n.f9.accept(this, argu);
		n.f10.accept(this, argu);
		n.f11.accept(this, argu);
		n.f12.accept(this, argu);
		n.f13.accept(this, argu);
		n.f14.accept(this, classInfo);
		n.f15.accept(this, argu);
		n.f16.accept(this, argu);
		
		return _ret;
	}
	
	/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
	public String visit(ClassDeclaration n, EntryInfo argu)
	{
		EntryInfoClass classInfo = argu.c_get(n.f1.f0.tokenImage);
		String _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, classInfo);
		n.f2.accept(this, argu);
		n.f3.accept(this, classInfo);
		n.f4.accept(this, classInfo);
		n.f5.accept(this, argu);
		
		return _ret;
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
	public String visit(ClassExtendsDeclaration n, EntryInfo argu)
	{
		EntryInfoClass classInfo = argu.c_get(n.f1.f0.tokenImage);
		String _ret = null;
		n.f0.accept(this, argu);
		n.f1.accept(this, classInfo);
		n.f2.accept(this, argu);
		n.f3.accept(this, classInfo);
		n.f4.accept(this, argu);
		n.f5.accept(this, classInfo);
		n.f6.accept(this, classInfo);
		n.f7.accept(this, argu);
		
		return _ret;
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
	public String visit(MethodDeclaration n, EntryInfo argu)//here argu should be an EntryInfoClass
	{
		EntryInfoMethod methodInfo = argu.m_get(n.f2.f0.tokenImage);
		
		n.f0.accept(this, argu);
		String rtnTypeDef = n.f1.accept(this, methodInfo);
		n.f2.accept(this, methodInfo);
		n.f3.accept(this, argu);
		n.f4.accept(this, methodInfo);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f7.accept(this, methodInfo);
		n.f8.accept(this, methodInfo);
		n.f9.accept(this, argu);
		String rtnTypeReal = n.f10.accept(this, methodInfo);
		if(!this.is_match(argu, rtnTypeDef, rtnTypeReal))
		{
			ErrorPrinter.add_error(n.f2.f0.beginLine, "in method \"" +  n.f2.f0.tokenImage + "\", the return expression is not compatible with defined.");
		}
		n.f11.accept(this, argu);
		n.f12.accept(this, argu);
		return rtnTypeReal;
	}
	
	/**
	* f0 -> ArrayType()
	*       | BooleanType()
	*       | IntegerType()
	*       | Identifier()
	*/
	public String visit(Type n, EntryInfo argu)
	{
		String _ret = n.f0.accept(this, argu);
		return _ret;
	}
	
	/**
	* f0 -> "int"
	* f1 -> "["
	* f2 -> "]"
	*/
	public String visit(ArrayType n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return "int[]";
	}
	
	/**
	* f0 -> "boolean"
	*/
	public String visit(BooleanType n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		return "boolean";
	}
	
	/**
	* f0 -> "int"
	*/
	public String visit(IntegerType n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		return "int";
	}
	
	/**
	* f0 -> "{"
	* f1 -> ( Statement() )*
	* f2 -> "}"
	*/
	public String visit(Block n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		String _ret = n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return _ret;
	}
	
	/**
	* f0 -> Identifier()
	* f1 -> "="
	* f2 -> Expression()
	* f3 -> ";"
	*/
	public String visit(AssignmentStatement n, EntryInfo argu)//here argu should be an EntryInfoMethod
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String leftType = (argu.v_get(n.f0.f0.tokenImage) == null ?
				null : argu.v_get(n.f0.f0.tokenImage).get_type());
		String rightType = n.f2.accept(this, argu);
			//bug fix : test17 in case that one of the operand is undefined  
		if(!(leftType == null || rightType == null) && !is_match(argu, leftType, rightType))
		{
			ErrorPrinter.add_error(n.f0.f0.beginLine, "the assigment is not compatiable with the variable \"" + n.f0.f0.tokenImage + "\".");
		}
		n.f3.accept(this, argu);
		return leftType;
		
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
	public String visit(ArrayAssignmentStatement n, EntryInfo argu)
	{
		String IdentifierType = argu.v_get(n.f0.f0.tokenImage).get_type();
		if(!IdentifierType.equals("int[]"))
		{
			ErrorPrinter.add_error(n.f0.f0.beginLine, "you can only use int[] for array assignment.");
		}
		
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		
		String idxType = n.f2.accept(this, argu);
		if(!idxType.equals("int"))
		{
			ErrorPrinter.add_error(n.f0.f0.beginLine, "you can only use integers for array indexes.");
		}
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		
		String contentType = n.f5.accept(this, argu);
		if(!contentType.equals("int"))
		{
			ErrorPrinter.add_error(n.f0.f0.beginLine, "you can only use integers for array contents.");
		}
		
		n.f6.accept(this, argu);
		return "int[]";
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
	public  String visit(IfStatement n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		
		String type = n.f2.accept(this, argu);
		if(!type.equals("boolean"))
		{
			ErrorPrinter.add_error(n.f0.beginLine, "the type of condition expression for if statement must be boolean.");
		}
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		return null;
	}
	
	/**
	* f0 -> "while"
	* f1 -> "("
	* f2 -> Expression()
	* f3 -> ")"
	* f4 -> Statement()
	*/
	public String visit(WhileStatement n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String type = n.f2.accept(this, argu);
		if(!type.equals("boolean"))
		{
			ErrorPrinter.add_error(n.f0.beginLine, "the type of condition expression for while statement must be boolean.");
		}
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return null;
	}
	
	/**
	* f0 -> "System.out.println"
	* f1 -> "("
	* f2 -> Expression()
	* f3 -> ")"
	* f4 -> ";"
	*/
	public String visit(PrintStatement n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String type = n.f2.accept(this, argu);
		if(type != null && !type.equals("int"))//bug fix : in test16.java, the "op" is an undefined variable.  
		{
			ErrorPrinter.add_error(n.f0.beginLine, "the printed value must be of int type.");
		}
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		return null;
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
	public String visit(Expression n, EntryInfo argu)
	{
		String ret = n.f0.accept(this, argu);
		return ret;
	}

	/**
	* f0 -> PrimaryExpression()
	* f1 -> "&&"
	* f2 -> PrimaryExpression()
	*/
	public String visit(AndExpression n, EntryInfo argu)
	{
		String leftType = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String rightType = n.f2.accept(this, argu);
		if(!leftType.equals("boolean") || !rightType.equals("boolean"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "the expression beside \"&&\" must be of boolean type.");
		}
		return "boolean";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "<"
	* f2 -> PrimaryExpression()
	*/
	public String visit(CompareExpression n, EntryInfo argu)
	{
		String leftType = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String rightType = n.f2.accept(this, argu);
		if(!leftType.equals("int") || !rightType.equals("int"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "the expression beside \"<\" must be of int type.");
		}
		return "boolean";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "+"
	* f2 -> PrimaryExpression()
	*/
	public String visit(PlusExpression n, EntryInfo argu)
	{
		String leftType = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String rightType = n.f2.accept(this, argu);
		if(!leftType.equals("int") || !rightType.equals("int"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "the expression beside \"+\" must be of int type.");
		}
		return "int";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "-"
	* f2 -> PrimaryExpression()
	*/
	public String visit(MinusExpression n, EntryInfo argu)
	{
		String leftType = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String rightType = n.f2.accept(this, argu);
		if(!leftType.equals("int") || !rightType.equals("int"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "the expression beside \"-\" must be of int type.");
		}
		return "int";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "*"
	* f2 -> PrimaryExpression()
	*/
	public String visit(TimesExpression n, EntryInfo argu)
	{
		String leftType = n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		String rightType = n.f2.accept(this, argu);
		if(!leftType.equals("int") || !rightType.equals("int"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "the expression beside \"*\" must be of int type.");
		}
		return "int";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "["
	* f2 -> PrimaryExpression()
	* f3 -> "]"
	*/
	public String visit(ArrayLookup n, EntryInfo argu)
	{
		String indentifierType = n.f0.accept(this, argu);
		if(!indentifierType.equals("int[]"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "you can only use int[] for array lookup.");
		}
		
		n.f1.accept(this, argu);
		
		String idxType = n.f2.accept(this, argu);
		if(!idxType.equals("int"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "you can only use integers for array indexes.");
		}
		n.f3.accept(this, argu);
		
		return "int";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "."
	* f2 -> "length"
	*/
	public String visit(ArrayLength n, EntryInfo argu)
	{
		String varType = n.f0.accept(this, argu);
		if(!varType.equals("int[]"))
		{
			ErrorPrinter.add_error(n.f1.beginLine, "only variables of int[] type can use the .length.");
		}
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return "int";
	}
	
	/**
	* f0 -> PrimaryExpression()
	* f1 -> "."
	* f2 -> Identifier()
	* f3 -> "("
	* f4 -> ( ExpressionList() )?
	* f5 -> ")"
	*/
	public String visit(MessageSend n, EntryInfo argu)
	{
		String classType = n.f0.accept(this, argu);
		if(classType != null && !argu.is_class_type(classType))//bug fix : TreeVisitor-error.java line #340
		{
			ErrorPrinter.add_error(n.f1.beginLine, "the expression should be a instance of a class.");
			return null;
		}
		n.f1.accept(this, argu);
		EntryInfoMethod methodInfo = null;
		if(classType != null)//bug fix : TreeVisitor-error.java line #340
			methodInfo = SymbolTable.symbolTable.get(classType).m_get(n.f2.f0.tokenImage);//should find the method definition in the class PrimaryExpression identifier
		
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		
		if(methodInfo == null)
			return null;
		
		this.messageTable.push(methodInfo);
		methodInfo.init_para_idx();
		n.f4.accept(this, argu);
		if(!methodInfo.para_all_matched())
			ErrorPrinter.add_error(n.f1.beginLine, "fail to call \"" + n.f2.f0.tokenImage +"\" : the number of parameters unmatch.");
			//bug fix : test88, here can only test whether the number of parameters is right  
		this.messageTable.pop();
		
		n.f5.accept(this, argu);
		
		if(methodInfo == null) return null;
		
		return methodInfo.get_rtn_type();
		
	}
	
	/**
	* f0 -> Expression()
	* f1 -> ( ExpressionRest() )*
	*/
	public String visit(ExpressionList n, EntryInfo argu)
	{
		String type = n.f0.accept(this, argu);
		String properType = this.messageTable.lastElement().next_para();
		if(type != null && (properType == null || !this.is_match(this.messageTable.lastElement(), properType, type)))
		{
			int lineNumber;
			if(n.f0.f0.which <= 7)
			{
				AndExpression tempNode = (AndExpression)n.f0.f0.choice;
				lineNumber = tempNode.f1.beginLine;
			}
			else
			{
				PrimaryExpression tempNode = (PrimaryExpression)n.f0.f0.choice;
				if(tempNode.f0.which == 1 || tempNode.f0.which == 3)
				{
					Identifier tempNode2 = (Identifier)tempNode.f0.choice;
					lineNumber = tempNode2.f0.beginLine;
				}
				else
				{
					TrueLiteral tempNode3 = (TrueLiteral)tempNode.f0.choice;
					lineNumber = tempNode3.f0.beginLine;
				}
			}//bug fix : test88, change the line number to where the invoke happens, kind of troublesome 
			ErrorPrinter.add_error(lineNumber, "fail to call \"" +this.messageTable.lastElement().get_name() +"\" : parameter unmatch.");
		}
		n.f1.accept(this, argu);
		return null;
	}
	
	/**
	* f0 -> ","
	* f1 -> Expression()
	*/
	public String visit(ExpressionRest n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		String type = n.f1.accept(this, argu);
		String properType = this.messageTable.lastElement().next_para();
		if(type != null && (properType == null || !this.is_match(this.messageTable.lastElement(), properType, type)))
		{
			ErrorPrinter.add_error(n.f0.beginLine, "fail to call \"" +this.messageTable.lastElement().get_name() +"\" : parameter unmatch.");
		}
		return null;
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
	public String visit(PrimaryExpression n, EntryInfo argu)
	{
		String _ret = n.f0.accept(this, argu);
		return _ret;
	}
	
	/**
	* f0 -> <INTEGER_LITERAL>
	*/
	public String visit(IntegerLiteral n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		return "int";
	}
	
	/**
    * f0 -> "true"
    */
	public String visit(TrueLiteral n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		return "boolean";
	}
	
	/**
    * f0 -> "false"
    */
	public String visit(FalseLiteral n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		return "boolean";
	}
	
	/**
	* f0 -> <IDENTIFIER>
	*/
	public String visit(Identifier n, EntryInfo argu)
	{
		String _ret = null;
		n.f0.accept(this, argu);
		if(!(argu instanceof SymbolTable) && argu.v_get(n.f0.tokenImage) != null)
			_ret = argu.v_get(n.f0.tokenImage).get_type();//Identifier as a variable
		if(_ret == null)
		{
			if(SymbolTable.get_symbol_table().get(n.f0.tokenImage) != null)//Identifier as a class type
			_ret = n.f0.tokenImage;
		}
		return _ret;
	}
	
	/**
	* f0 -> "this"
	*/
	public String visit(ThisExpression n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		return argu.get_belong_class_name();
	}
	
	/**
	* f0 -> "new"
	* f1 -> "int"
	* f2 -> "["
	* f3 -> Expression()
	* f4 -> "]"
	*/
	public String visit(ArrayAllocationExpression n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		String idxType = n.f3.accept(this, argu);
		if(!idxType.equals("int"))
		{
			ErrorPrinter.add_error(n.f0.beginLine, "you can only use integers for array length.");
		}
		n.f4.accept(this, argu);
		
		return "int[]";
	}
	
	/**
	* f0 -> "new"
	* f1 -> Identifier()
	* f2 -> "("
	* f3 -> ")"
	*/
	public String visit(AllocationExpression n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		
		return (SymbolTable.symbolTable.get(n.f1.f0.tokenImage) == null ? null : n.f1.f0.tokenImage);
		//bug fix : test29, if a class is undefined, its constructor's return type should be null.
	}
	
	/**
	* f0 -> "!"
	* f1 -> Expression()
	*/
	public String visit(NotExpression n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		String type = n.f1.accept(this, argu);
		if(!type.equals("boolean"))
		{
			ErrorPrinter.add_error(n.f0.beginLine, "the expression after \"!\" must be of boolean type.");
		}
		return "boolean";
	}
	
	/**
	* f0 -> "("
	* f1 -> Expression()
	* f2 -> ")"
	*/
	public String visit(BracketExpression n, EntryInfo argu)
	{
		n.f0.accept(this, argu);
		String type = n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		return type;
	}
	
}
