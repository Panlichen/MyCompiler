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
    		SymbolTable topSymbolTable = new SymbolTable();
    		
    		VisitorBuildSymbolTable vbst = new VisitorBuildSymbolTable();
    		
    		root.accept(vbst, topSymbolTable);
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