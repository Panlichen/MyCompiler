package minijava.typecheck;

import java.io.FileInputStream;
import java.util.Scanner;

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
    		//Scanner input = new Scanner(new File("D:\\PKU\\compile_practice\\testCases\\trivial1.java"));
    		//Node root = new MiniJavaParser(new InputStream(input)).Goal();
    		Node root = new MiniJavaParser(new FileInputStream("D:\\PKU\\compile_practice\\testCases\\test56.java")).Goal();
    		//System.out.println("hello world.");
    		/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
    		SymbolTable topTable = new SymbolTable();
    		
    		VisitorBuildSymbolTable vbst = new VisitorBuildSymbolTable();
    		VisitorCheckUndefinedRef vcudr = new VisitorCheckUndefinedRef();
    		VisitorCheckIncompatible vci = new VisitorCheckIncompatible();
    		VisitorCheckUnuse vcu = new VisitorCheckUnuse();
    		
    		root.accept(vbst, topTable);
    		topTable.check_undefined_class(topTable);
    		topTable.check_inheritance_loop();

    		root.accept(vcudr, topTable);
    		root.accept(vci, topTable);
    		//root.accept(vcu, topTable);
    		
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