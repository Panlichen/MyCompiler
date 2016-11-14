package minijava.typecheck.visitor;

import minijava.visitor.*;
import minijava.typecheck.symboltable.*;
import minijava.syntaxtree.*;

public class VisitorCheckUndefinedRef extends GJVoidDepthFirst<EntryInfo>{
	
	/**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
	public void visit(ClassDeclaration n, EntryInfo argu)
	{
		String name = n.f1.f0.tokenImage;
		EntryInfoClass classInfo = argu.c_get(name);
		
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, classInfo);
        n.f4.accept(this, classInfo);
        n.f5.accept(this, argu);
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
	public void visit(ClassExtendsDeclaration n, EntryInfo argu)
	{
		String name = n.f1.f0.tokenImage;
		EntryInfoClass classInfo = argu.c_get(name);
		
		n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, classInfo);
        n.f6.accept(this, classInfo);
        n.f7.accept(this, argu);
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
    public void visit(MethodDeclaration n, EntryInfo argu)
    {
    	String name = n.f2.f0.tokenImage;
    	EntryInfoMethod methodInfo = argu.m_get(name);
    	
    	n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, methodInfo);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, methodInfo);
        n.f8.accept(this, methodInfo);
        n.f9.accept(this, argu);
        n.f10.accept(this, methodInfo);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
    }
		    
	/**
	* f0 -> Identifier()
	* f1 -> "="
	* f2 -> Expression()
	* f3 -> ";"
	*/
	public void visit(AssignmentStatement n, EntryInfo argu)//the argu should be an EntryInfoMethod
	{
		//undefined variables can show up here
		if(argu.v_get(n.f0.f0.tokenImage) == null)
		{
			ErrorPrinter.add_error(n.f0.f0.beginLine, "the variable \"" + n.f0.f0.tokenImage + "\" is undefined");
		}
			   
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		
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
	public void visit(ArrayAssignmentStatement n, EntryInfo argu)//the argu should be an EntryInfoMethod
	{
		if(argu.v_get(n.f0.f0.tokenImage) == null)
		{
			ErrorPrinter.add_error(n.f0.f0.beginLine, "the variable \"" + n.f0.f0.tokenImage + "\" is undefined");
		}
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
	public void visit(MessageSend n, EntryInfo argu)//the argu should be an EntryInfoMethod
	{
		String mthdName = n.f2.f0.tokenImage;
		if(n.f0.f0.which == 3)//invokes other class' method, both the class and method can be undefined
		{
			Identifier classInstanceIdentifier = (Identifier)n.f0.f0.choice;
				//bug fix : the identifier in PrimaryExpression is not the type name of a class, but the name of a this identifier of the class.
			
			/*bug fix begin : TreeVisitor-error line #340*/
			EntryInfoVariable tempVariableInfo = argu.v_get(classInstanceIdentifier.f0.tokenImage);
			String className;
			if(tempVariableInfo == null)
			{
				className = null;
			}
			else
			{
				className = tempVariableInfo.get_type();
			}
			/*bug fix end : TreeVisitor-error line #340*/
			if(className != null && argu.is_class_type(className) && SymbolTable.get_symbol_table().get(className) == null)
				//bug fix : test20
			{
				ErrorPrinter.add_error(n.f2.f0.beginLine, className + " is not a defined class.");
			}
			else if(className != null && SymbolTable.get_symbol_table().get(className).m_get(mthdName) == null)
			{
				ErrorPrinter.add_error(n.f2.f0.beginLine, "\"" + className + "\" does not have the method \"" + mthdName + "\".");
			}
		}
		else if(n.f0.f0.which == 4)//invokes this.f, the method can be undefined
		{
			if(SymbolTable.get_symbol_table().get(argu.get_belong_class_name()).m_get(mthdName) == null)
			{
				ErrorPrinter.add_error(n.f2.f0.beginLine, "\"" + argu.get_belong_class_name() + "\" does not have the method \"" + mthdName + "\".");
			}
		}
		
		 n.f0.accept(this, argu);
	     n.f1.accept(this, argu);
	     n.f2.accept(this, argu);
	     n.f3.accept(this, argu);
	     n.f4.accept(this, argu);
	     n.f5.accept(this, argu);
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
	public void visit(PrimaryExpression n, EntryInfo argu)//the argu should be an EntryInfoMethod
	{
		if(n.f0.which == 3)
		{
			Identifier varIdentifier = (Identifier)n.f0.choice;
			String varName = varIdentifier.f0.tokenImage;
			
			if(argu.v_get(varName) == null)
			{
				ErrorPrinter.add_error(varIdentifier.f0.beginLine, "the variable \"" + varName + "\" is undefined.");
			}
		}
		n.f0.accept(this, argu);
	}
	
	/**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
	public void visit(AllocationExpression n, EntryInfo argu)
	{
		if(SymbolTable.get_symbol_table().get(n.f1.f0.tokenImage) == null)
		{
			ErrorPrinter.add_error(n.f1.f0.beginLine, "the class \"" + n.f1.f0.tokenImage + "\" is undefined.");
		}
		n.f0.accept(this, argu);
	    n.f1.accept(this, argu);
	    n.f2.accept(this, argu);
	    n.f3.accept(this, argu);
	}
}

