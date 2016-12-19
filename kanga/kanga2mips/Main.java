package kanga.kanga2mips;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import kanga.KangaParser;
import kanga.ParseException;
import kanga.TokenMgrError;
import kanga.kanga2mips.visitor.VisitorTranslateK2M;
import kanga.syntaxtree.Node;
import kanga.visitor.GJDepthFirst;

public class Main {

	public static void main(String[] args) {
		try {
			//Node root = new KangaParser(System.in).Goal();
			Node root = new KangaParser(new FileInputStream("D:\\PKU\\compile_practice\\testCasesKanga\\test01.kg")).Goal();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			VisitorTranslateK2M vtkm = new VisitorTranslateK2M(out);
			root.accept(vtkm);
			//System.out.println("here");
			
		} catch (TokenMgrError e) {
			// Handle Lexical Errors
			e.printStackTrace();
		} catch (ParseException e) {
			// Handle Grammar Errors
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}