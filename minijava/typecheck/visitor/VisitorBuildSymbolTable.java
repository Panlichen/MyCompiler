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
    	mainMethodInfo.set_numParas(0);
    	
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
    	
    	n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, classInfo);
        n.f4.accept(this, classInfo);
        n.f5.accept(this, argu);
    }

}
