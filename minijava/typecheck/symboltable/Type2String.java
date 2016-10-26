package minijava.typecheck.symboltable;
import minijava.syntaxtree.*;

public class Type2String {
	static public String type_to_string(Type theType)
	{
		switch (theType.f0.which)
		{
		case 0:
			return "int[]";
		case 1:
			return "boolean";
		case 2:
			return "int";
		case 3:
			return get_identifer_image((Identifier)theType.f0.choice);
		default:
			return null;
		}
	}
	static String get_identifer_image(Identifier nID)
	{
		return nID.f0.tokenImage;
	}
}
