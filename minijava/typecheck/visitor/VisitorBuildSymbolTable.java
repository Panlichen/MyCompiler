package minijava.typecheck.visitor;

import minijava.syntaxtree.*;
import minijava.typecheck.symboltable.*;
import minijava.visitor.*;

import java.util.*;
import minijava.typecheck.*;

public class VisitorBuildSymbolTable extends GJVoidDepthFirst<EntryInfo>{
	
	/**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public void visit(Goal n, EntryInfo argu) 
    {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
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
    public void visit(MainClass n, EntryInfo argu) // here argu should be the topSymbolTable
    {
    	String mainClassName = n.f1.f0.tokenImage;
    	argu.set_main_class(mainClassName);
    	
    	EntryInfoClass mainClassInfo = new EntryInfoClass();
    	mainClassInfo.set_id_info(n.f1);
    	
    	
    	//then we need to manually build all things for the main method...
    	EntryInfoMethod mainMethodInfo = new EntryInfoMethod();
    	mainMethodInfo.set_id_info(new Identifier(n.f6));
    	
    	mainClassInfo.m_put(mainMethodInfo.get_name(), mainMethodInfo);
    	
    	argu.c_put(mainClassInfo.get_name(), mainClassInfo);
    	
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
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
    	
    }
    
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    
    public void visit(ClassDeclaration n, EntryInfo argu)// here argu should be the topSymbolTable
    {
    	EntryInfoClass classInfo = new EntryInfoClass();
    	classInfo.set_id_info(n.f1);
    	
    	argu.c_put(classInfo.get_name(), classInfo);
    	
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
    public void visit(ClassExtendsDeclaration n, EntryInfo argu)// here argu should be the topSymbolTable
    {
    	EntryInfoClass classInfo = new EntryInfoClass();
    	classInfo.set_id_info(n.f1);
    	
    	argu.c_put(classInfo.get_name(), classInfo);
    	
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
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public void visit(VarDeclaration n, EntryInfo argu)//here the argu should be an EntryInfoClass or an EntryInfoMethod
    {
    	EntryInfoVariable variableInfo = new EntryInfoVariable();
    	variableInfo.set_id_info(n.f1);
    	variableInfo.set_type(n.f0);
    	
    	argu.v_put(variableInfo.get_name(), variableInfo);
    	
    	n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
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
    public void visit(MethodDeclaration n, EntryInfo argu)//here argu should be an EntryInfoClass
    {
    	EntryInfoMethod methodInfo = new EntryInfoMethod();
    	methodInfo.set_id_info(n.f2);
    	methodInfo.set_rtn_type(n.f1);
    	methodInfo.set_belong_class_name(argu.get_name());
    	
    	argu.m_put(methodInfo.get_name(), methodInfo);
    	
    	n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, methodInfo);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, methodInfo);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
    }
    

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public void visit(FormalParameter n, EntryInfo argu)//here argu should be an EntryInfoMethod
    {
    	EntryInfoVariable variableInfo = new EntryInfoVariable();
    	variableInfo.set_id_info(n.f1);
    	variableInfo.set_type(n.f0);
    	
    	argu.add_para(variableInfo.get_type());
    	argu.v_put(variableInfo.get_name(), variableInfo);
    	
    	n.f0.accept(this, argu);
        n.f1.accept(this, argu);
    }
}
