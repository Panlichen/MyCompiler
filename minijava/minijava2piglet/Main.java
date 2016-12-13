package minijava.minijava2piglet;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.visitor.GJDepthFirst;


import minijava.syntaxtree.*;
import minijava.typecheck.visitor.*;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import minijava.minijava2piglet.symboltablem2p.*;
import minijava.minijava2piglet.visitor.*;


public class Main { 
 
    public static void main(String[] args) {
    	try {
    		//Node root = new MiniJavaParser(System.in).Goal();
    		Node root = new MiniJavaParser(new FileInputStream("D:\\PKU\\compile_practice\\testCasesWOTE\\test98.java")).Goal();
    		
    		SymbolTableM2P topTable = new SymbolTableM2P();
    		VisitorBuildSymbolTableM2P vbstm = new VisitorBuildSymbolTableM2P();
    		ByteArrayOutputStream baops = new ByteArrayOutputStream();
    		VisitorTranslateM2P vtm = new VisitorTranslateM2P(baops);
    		
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