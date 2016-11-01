package minijava.typecheck.visitor;

import java.util.Vector;
import minijava.syntaxtree.*;
import minijava.typecheck.symboltable.*;
import minijava.visitor.*;

public class VisitorCheckUnuse extends GJVoidDepthFirst<EntryInfo>{
	
	private Vector<String> usedVars;
	private Vector<String> initializedVars;
	
	public VisitorCheckUnuse()
	{
		super();
		this.usedVars = new Vector<String>();
		this.initializedVars = new Vector<String>();
	}
	
	public void init_vectors()
	{
		this.usedVars.removeAllElements();
		this.initializedVars.removeAllElements();
	}
	
	public boolean used(String key)
	{
		for(int i = 0; i < this.usedVars.size(); i++)
		{
			if(this.usedVars.elementAt(i).equals(key))
				return true;
		}
		return false;
	}
	
	public boolean initialized(String key)
	{
		for(int i = 0; i < this.initializedVars.size(); i++)
		{
			if(this.initializedVars.elementAt(i).equals(key))
				return true;
		}
		return false;
	}
	
	public void check_var_flow(EntryInfo argu)//here the argu should be an EntryInfoMethod
	{
		for(String key : argu.get_var_table().keySet())
		{
			if(!used(key))
			{
				ErrorPrinter.add_warning(argu.v_get(key).get_line_number(), "the variable \"" + key + "\" is unused.");
			}
			if(!initialized(key))
			{
				ErrorPrinter.add_warning(argu.v_get(key).get_line_number(), "the variable \"" + key + "\" is uninitialized.");
			}
		}
	}
	
	/**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
	public void visit(ClassDeclaration n, EntryInfo argu)//here the argu should be the topTable
	{
		String className = n.f1.f0.tokenImage;
		EntryInfoClass classInfo = argu.c_get(className);
		
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
		String className = n.f1.f0.tokenImage;
		EntryInfoClass classInfo = argu.c_get(className);
		
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
	public void visit(MethodDeclaration n, EntryInfo argu)//here the argu should be an EntryInfoClass
	{
		EntryInfoMethod methodInfo = argu.m_get(n.f2.f0.tokenImage);
		
		this.init_vectors();
		
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
        
        this.check_var_flow(methodInfo);
	}
	
	/**
	* f0 -> Identifier()
	* f1 -> "="
	* f2 -> Expression()
	* f3 -> ";"
	*/	
	public void visit(AssignmentStatement n, EntryInfo argu)
	{
		this.initializedVars.addElement(n.f0.f0.tokenImage);
		
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
	//but it is too hard to locate which slot of the array is initialized, so i quit.
	
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
	public void visit(PrimaryExpression n, EntryInfo argu)
	{
		if(n.f0.which == 3)
		{
			Identifier idNode = (Identifier)n.f0.choice;
			this.usedVars.addElement(idNode.f0.tokenImage);
		}
		n.f0.accept(this, argu);
	}
}
