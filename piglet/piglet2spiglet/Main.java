package piglet.piglet2spiglet;


import java.io.ByteArrayOutputStream;

//import piglet.ParseException;
//import piglet.PigletParser;
//import piglet.TokenMgrError;
import piglet.piglet2spiglet.symboltablem2sp.*;
import piglet.piglet2spiglet.visitor.*;
//import piglet.syntaxtree.Node;
//import piglet.visitor.GJDepthFirst;
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
    		//Node root = new MiniJavaParser(System.in).Goal();
    		Node root = new MiniJavaParser(new FileInputStream("D:\\PKU\\compile_practice\\testCasesWOTE\\test73.java")).Goal();
    		
    		SymbolTableM2SP topTable = new SymbolTableM2SP();
    		VisitorBuildSymbolTableM2SP vbstm = new VisitorBuildSymbolTableM2SP();
    		ByteArrayOutputStream baops = new ByteArrayOutputStream();
    		VisitorTranslateM2SP vtm = new VisitorTranslateM2SP(baops);
    		
    		root.accept(vbstm, topTable);
    		topTable.inherit_from_ancestors();
    		
    		root.accept(vtm, topTable);
    		
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