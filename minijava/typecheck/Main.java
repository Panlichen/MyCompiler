package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.syntaxtree.Node;
import minijava.visitor.GJDepthFirst;

import minijava.syntaxtree.*;
import minijava.typecheck.symboltable.*;
import minijava.typecheck.visitor.*;


public class Main { 
 
    public static void main(String[] args) {
    	try {
    		Node root = new MiniJavaParser(System.in).Goal();
    		/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
    		SymbolTable topTable = new SymbolTable();
    		
    		VisitorBuildSymbolTable vbst = new VisitorBuildSymbolTable();
    		VisitorCheckUndefinedRef vcud = new VisitorCheckUndefinedRef();
    		VisitorCheckIncompatible vci = new VisitorCheckIncompatible();
    		VisitorCheckUnuse vcu = new VisitorCheckUnuse();
    		
    		root.accept(vbst, topTable);
    		topTable.check_undefined_class(topTable);
    		topTable.check_inheritance_loop();

    		root.accept(vcud, topTable);
    		root.accept(vci, topTable);
    		root.accept(vcu, topTable);
    		
    		ErrorPrinter.print_all_error();
    		ErrorPrinter.print_all_warning();
    		
    		if(ErrorPrinter.error_exists())
    		{
    			System.out.println("Type error");
    		}
    		else 
    			System.out.println("Program type checked successfully");
    		
    	}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch (ParseException e){
    		//Handle Grammar Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
}