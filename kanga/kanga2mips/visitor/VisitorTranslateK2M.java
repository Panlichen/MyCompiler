package kanga.kanga2mips.visitor;

import kanga.kanga2mips.codesketch.*;
import kanga.syntaxtree.*;
import kanga.visitor.*;

import java.io.OutputStream;
import java.util.*;

import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;

public class VisitorTranslateK2M extends GJNoArguDepthFirst<MIPSCodeSet>{

	int curTotalKStack = 0;
	int curSpilledPara = 0;
	boolean endOfStmt = true;
	OutputStream out;
	
	public VisitorTranslateK2M(OutputStream out)
	{
		this.out = out;
	}
	/**
	 * Represents a sequence of nodes nested within a choice, list,
	 * optional list, or optional, e.g. ( A B )+ or [ C D E ]
	 */
	public MIPSCodeSet visit(NodeSequence n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
		{
			MIPSCodeSet temp = e.nextElement().accept(this);
			ret.merge_code_set(temp);
		}
		return ret;
	}
	
	/**
	 * Represents a grammar list, e.g. ( A )+
	 */
	public MIPSCodeSet visit(NodeList n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
		{
			MIPSCodeSet temp = e.nextElement().accept(this);
			ret.merge_code_set(temp);
		}
		return ret;
	}
	
	/**
	 * Represents an optional grammar list, e.g. ( A )*
	 */
	public MIPSCodeSet visit(NodeListOptional n)
	{
		if(n.present())
		{
			MIPSCodeSet ret = new MIPSCodeSet();
			for(Enumeration<Node> e = n.elements(); e.hasMoreElements();)
			{
				MIPSCodeSet temp = e.nextElement().accept(this);
				ret.merge_code_set(temp);
			}
			return ret;
		}
		else
			return null;
	}
	
	/**
	* f0 -> "MAIN"
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> "["
	* f5 -> IntegerLiteral()
	* f6 -> "]"
	* f7 -> "["
	* f8 -> IntegerLiteral()
	* f9 -> "]"
	* f10 -> StmtList()
	* f11 -> "END"
	* f12 -> ( Procedure() )*
	* f13 -> <EOF>
	*/
	public MIPSCodeSet visit(Goal n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		int totalKStack = Integer.parseInt(n.f5.f0.tokenImage);
		int maxCalledPara = Integer.parseInt(n.f8.f0.tokenImage);
		
		this.curTotalKStack = totalKStack;
		this.curSpilledPara = 0;
		
		ret.emit(".text");
		ret.emit("main:");
		ret.emit("move $fp $sp");
		ret.emit("subu $sp, $sp, " + 4 * (totalKStack + maxCalledPara + 2));
		ret.emit("sw $ra, -4($fp)");
		
		MIPSCodeSet sl = n.f10.accept(this);
		ret.merge_code_set(sl);
		
		ret.emit("lw $ra, -4($fp)");
		ret.emit("addu $sp, $sp, " + 4 * (totalKStack + maxCalledPara + 2));
		ret.emit("j _exit");
		ret.print_all(out);
		
		n.f12.accept(this);
		MIPSCodeSet rest = new MIPSCodeSet();
		
		rest.emit(".text");
		rest.emit("_halloc:");
		rest.emit("li $v0, 9");
		rest.emit("syscall");
		rest.emit("jr $ra\n");
		
		rest.emit(".text");
		rest.emit("_print:");
		rest.emit("li $v0, 1");
		rest.emit("syscall");
		rest.emit("la $a0, newline");
		rest.emit("li $v0, 4");
		rest.emit("syscall");
		rest.emit("jr $ra\n");
		
		rest.emit(".text");
		rest.emit("_error:");
		rest.emit("la $a0, err_info");
		rest.emit("li $v0, 4");
		rest.emit("syscall\n");
		rest.emit("_exit:");
		rest.emit("li $v0, 10");
		rest.emit("syscall\n");
		
		rest.emit(".data");
		rest.emit(".align 0");
		rest.emit("newline:\t.asciiz \"\\n\"\n");
		rest.emit(".data");
		rest.emit(".align 0");
		rest.emit("err_info:\t.asciiz \"Error: ARRAY INDEX OUT OF BOUNDS, EXITS.\"\n");
		
		rest.print_all(out);
		return null;
	}
	
	/**
	* f0 -> ( ( Label() )? Stmt() )*
	*/
	public MIPSCodeSet visit(StmtList n)
	{
		this.endOfStmt = true;
		MIPSCodeSet ret = n.f0.accept(this);
		return ret;
	}
	
	/**
	* f0 -> Label()
	* f1 -> "["
	* f2 -> IntegerLiteral()
	* f3 -> "]"
	* f4 -> "["
	* f5 -> IntegerLiteral()
	* f6 -> "]"
	* f7 -> "["
	* f8 -> IntegerLiteral()
	* f9 -> "]"
	* f10 -> StmtList()
	* f11 -> "END"
	*/
	public MIPSCodeSet visit(Procedure n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		ret.emit(".text");
		ret.emit(n.f0.f0.tokenImage + ":");
		
		int numPara = Integer.parseInt(n.f2.f0.tokenImage);
		int totalKStack = Integer.parseInt(n.f5.f0.tokenImage);
		int maxCalledPara = Integer.parseInt(n.f8.f0.tokenImage);
		
		numPara = numPara > 4 ? numPara - 4 : 0;
		maxCalledPara = maxCalledPara > 4 ? maxCalledPara - 4 : 0;
		
		this.curSpilledPara = numPara;
		this.curTotalKStack = totalKStack;
		
		ret.emit("sw $fp, -8($sp)");
		ret.emit("move $fp, $sp");
		ret.emit("subu $sp, $sp, " + (4 * (totalKStack - numPara + maxCalledPara + 2)));
		ret.emit("sw $ra, -4($fp)");
		
		MIPSCodeSet sl = n.f10.accept(this);
		ret.merge_code_set(sl);
		
		ret.emit("lw $ra, -4($fp)");
		ret.emit("lw $fp, -8($fp)");//!!!!!!!!!!!!!!!!!!!!!!!!!!SB
		ret.emit("addu $sp, $sp, " + (4 * (totalKStack - numPara + maxCalledPara + 2)));
		ret.emit("jr $ra");
		
		ret.print_all(out);
		return null;
	}
	
	/**
	* f0 -> NoOpStmt()
	*       | ErrorStmt()
	*       | CJumpStmt()
	*       | JumpStmt()
	*       | HStoreStmt()
	*       | HLoadStmt()
	*       | MoveStmt()
	*       | PrintStmt()
	*       | ALoadStmt()
	*       | AStoreStmt()
	*       | PassArgStmt()
	*       | CallStmt()
	*/
	public MIPSCodeSet visit(Stmt n)
	{
		this.endOfStmt = true;
		MIPSCodeSet ret = n.f0.accept(this);
		this.endOfStmt = true;
		return ret;
	}
	
	/**
	* f0 -> "NOOP"
	*/
	public MIPSCodeSet visit(NoOpStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		ret.emit("nop");
		return ret;
	}
	
	/**
	* f0 -> "ERROR"
	*/
	public MIPSCodeSet visit(ErrorStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		ret.emit("j _error");
		return ret;
	}
	
	/**
	* f0 -> "CJUMP"
	* f1 -> Reg()
	* f2 -> Label()
	*/
	public MIPSCodeSet visit(CJumpStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		String reg = n.f1.f0.choice.toString();
		String label = n.f2.f0.tokenImage;
		ret.emit("beqz $" + reg + ", " + label);
		return ret;
	}
	
	/**
	* f0 -> "JUMP"
	* f1 -> Label()
	*/
	public MIPSCodeSet visit(JumpStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		ret.emit("j " + n.f1.f0.tokenImage);
		return ret;
	}
	
	/**
	* f0 -> "HSTORE"
	* f1 -> Reg()
	* f2 -> IntegerLiteral()
	* f3 -> Reg()
	*/
	public MIPSCodeSet visit(HStoreStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		String regBase = n.f1.f0.choice.toString();
		String regData = n.f3.f0.choice.toString();
		int iDisp = Integer.parseInt( n.f2.f0.tokenImage);
		String disp = iDisp != 0 ? "" + iDisp : "";//the disp has been multiplied by 4
		
		ret.emit("sw $" + regData + ", " + disp + "($" + regBase + ")");
		return ret;
	}
	
	/**
	* f0 -> "HLOAD"
	* f1 -> Reg()
	* f2 -> Reg()
	* f3 -> IntegerLiteral()
	*/
	public MIPSCodeSet visit(HLoadStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		String regTgt = n.f1.f0.choice.toString();
		String regBase  = n.f2.f0.choice.toString();
		int iDisp = Integer.parseInt( n.f3.f0.tokenImage);
		String disp = iDisp != 0 ? "" + iDisp  : "";
		ret.emit("lw $" + regTgt + ", " + disp + "($" + regBase + ")");
		return ret;
	}
	
	/**
	* f0 -> "MOVE"
	* f1 -> Reg()
	* f2 -> Exp()
	*/
	public MIPSCodeSet visit(MoveStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		this.endOfStmt = false;
		MIPSCodeSet exp = n.f2.accept(this);
		this.endOfStmt = true;
		String regTgt = n.f1.f0.choice.toString();
		
		if(exp.get_expType().equals("label"))
		{
			ret.emit("la $" + regTgt + ", " + exp.get_exp_address());
		}
		else if(exp.get_expType().equals("int"))
		{
			ret.emit("li $" + regTgt + ", " + exp.get_exp_address());
		}
		else
		{
			ret.merge_code_set(exp);
			ret.emit("move $" + regTgt + ", " + exp.get_exp_address());
		}
		return ret;		
	}


	/**
	* f0 -> "PRINT"
	* f1 -> SimpleExp()
	*/
	public MIPSCodeSet visit(PrintStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		this.endOfStmt = false;
		MIPSCodeSet se = n.f1.accept(this);
		this.endOfStmt = true;
		if(se.get_expType().equals("int"))
		{
			ret.emit("li $a0, " + se.get_exp_address());
		}
		else if(se.get_expType().equals("label"))
		{
			ret.emit("la $a0, " + se.get_expType());
		}
		else
			ret.emit("move $a0, " + se.get_exp_address());
		
		ret.emit("jal _print");
		return ret;
	}
	
	/**
	* f0 -> "ALOAD"
	* f1 -> Reg()
	* f2 -> SpilledArg()
	*/
	public MIPSCodeSet visit(ALoadStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		String reg = n.f1.f0.choice.toString();
		MIPSCodeSet exp = n.f2.accept(this);
		ret.emit("lw $" + reg + ", " + exp.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> "ASTORE"
	* f1 -> SpilledArg()
	* f2 -> Reg()
	*/
	public MIPSCodeSet visit(AStoreStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		MIPSCodeSet exp = n.f1.accept(this);
		String reg = n.f2.f0.choice.toString();
		ret.emit("sw $" + reg + ", " + exp.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> "PASSARG"
	* f1 -> IntegerLiteral()
	* f2 -> Reg()
	*/
	public MIPSCodeSet visit(PassArgStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		int idx = Integer.parseInt(n.f1.f0.tokenImage);
		String reg = n.f2.f0.choice.toString();
		ret.emit("sw $" + reg + ", " + (4 * (idx - 1)) + "($sp)");
		return ret;		
	}
	
	/**
	* f0 -> "CALL"
	* f1 -> SimpleExp()
	*/
	public MIPSCodeSet visit(CallStmt n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		this.endOfStmt = false;
		MIPSCodeSet exp = n.f1.accept(this);
		this.endOfStmt = true;
		if(exp.get_expType().equals("label"))
		{
			ret.emit("jal " + exp.get_exp_address());
		}
		else
			ret.emit("jalr " + exp.get_exp_address());
		return ret;
	}
	
	/**
	* f0 -> HAllocate()
	*       | BinOp()
	*       | SimpleExp()
	*/
	public MIPSCodeSet visit(Exp n)
	{
		this.endOfStmt = false;
		MIPSCodeSet ret = n.f0.accept(this);
		this.endOfStmt = true;
		return ret;
	}
	
	/**
	* f0 -> "HALLOCATE"
	* f1 -> SimpleExp()
	*/
	public MIPSCodeSet visit(HAllocate n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		MIPSCodeSet se = n.f1.accept(this);
		if(se.get_expType().equals("int"))
		{
			ret.emit("li $a0, " + se.get_exp_address());
		}
		else
		{
			ret.emit("move $a0, " + se.get_exp_address());
		}
		ret.emit("jal _halloc");
		ret.set_exp_address("$v0");
		ret.set_expType("register");
		return ret;
	}
	
	/**
	* f0 -> Operator()
	* f1 -> Reg()
	* f2 -> SimpleExp()
	*/
	public MIPSCodeSet visit(BinOp n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		String op = n.f0.f0.choice.toString();
		String reg = n.f1.f0.choice.toString();
		MIPSCodeSet exp = n.f2.accept(this);
		
		switch(op)
		{
			case "LT" :
			{
				if(exp.get_expType().equals("int"))
				{
					ret.emit("slti $v0, $" + reg + ", " + exp.get_exp_address());
				}
				else
				{
					ret.emit("slt $v0, $" + reg + ", " + exp.get_exp_address());
				}
				break;
			}
			case "PLUS" :
			{
				if(exp.get_expType().equals("int"))
				{
					ret.emit("addi $v0, $" + reg + ", " + exp.get_exp_address());
				}
				else
				{
					ret.emit("add $v0, $" + reg + ", " + exp.get_exp_address());
				}
				break;
			}
			case "MINUS":
			{
				if(exp.get_expType().equals("int"))
				{
					ret.emit("li $v0, " + exp.get_exp_address());
					ret.emit("sub $v0, $" + reg + ", $v0");
				}
				else
				{
					ret.emit("sub $v0, $" + reg + ", " + exp.get_exp_address());
				}
				break;
			}
			case "TIMES":
			{
				if(exp.get_expType().equals("int"))
				{
					ret.emit("li $v0, " + exp.get_exp_address());
					ret.emit("mul $v0, $" + reg + ", $v0");
				}
				else
				{
					ret.emit("mul $v0, $" + reg + ", " + exp.get_exp_address());
				}
				break;
			}
		}
		ret.set_expType("register");
		ret.set_exp_address("$v0");
		return ret;
	}
	
	/**
	* f0 -> "SPILLEDARG"
	* f1 -> IntegerLiteral()
	*/
	public MIPSCodeSet visit(SpilledArg n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		int idx = Integer.parseInt(n.f1.f0.tokenImage);
		String offset;
		if(idx < this.curSpilledPara)//the parameter
		{
			offset = idx == 0 ? "" : "" + (4 * idx);
		}
		else
		{
			offset = "-" + (4 * (2 + this.curTotalKStack - idx));
		}
		ret.set_exp_address(offset + "($fp)");
		return ret;
	}

	/**
	* f0 -> Reg()
	*       | IntegerLiteral()
	*       | Label()
	*/
	public MIPSCodeSet visit(SimpleExp n)
	{
		MIPSCodeSet ret = n.f0.accept(this);
		return ret;
	}
	
	/**
	* f0 -> "a0"
	*       | "a1"
	*       | "a2"
	*       | "a3"
	*       | "t0"
	*       | "t1"
	*       | "t2"
	*       | "t3"
	*       | "t4"
	*       | "t5"
	*       | "t6"
	*       | "t7"
	*       | "s0"
	*       | "s1"
	*       | "s2"
	*       | "s3"
	*       | "s4"
	*       | "s5"
	*       | "s6"
	*       | "s7"
	*       | "t8"
	*       | "t9"
	*       | "v0"
	*       | "v1"
	*/
	public MIPSCodeSet visit(Reg n)//hava to write this not for the use of "Reg", but for "Simple Exp"
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		ret.set_exp_address("$" + n.f0.choice.toString());
		ret.set_expType("register");
		return ret;
	}
	
	/**
	* f0 -> <INTEGER_LITERAL>
	*/
	public MIPSCodeSet visit(IntegerLiteral n)
	{
		MIPSCodeSet ret = new MIPSCodeSet();
		ret.set_expType("int");
		ret.set_exp_address(n.f0.tokenImage);
		return ret;
	}
	
	/**
	* f0 -> <IDENTIFIER>
	*/
	public MIPSCodeSet visit(Label n)
	{
		if(!this.endOfStmt)
		{
			MIPSCodeSet ret = new MIPSCodeSet();
			ret.set_exp_address(n.f0.tokenImage);
			ret.set_expType("label"); 
			return ret;
		}
		else
		{
			MIPSCodeSet ret = new MIPSCodeSet();
			ret.emit(n.f0.tokenImage + ":");
			return ret;
		}
	}
}
