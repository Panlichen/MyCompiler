package spiglet.spiglet2kanga.flowgraph;

import java.util.*;

public class InterferenceGraph {
	private static int MAX_NUM_BB = 10000;
	private static int MAX_COLOR = 18;
	private int N = 0;
	
	private Vector<Vector<Integer>> vecAdjacentTable;
	private Stack<Integer> stack;
	private int[] arrayDegreePerTemp;
	private int[] arrayColorPerTemp;
	private int[] arrayLocationPerTemp;
	private boolean[] arrayUsedPerTemp;
	
	private int maxCalledNumPara;
	private int numSpilled;
	private MyBitSet sReg;
	private MyBitSet tReg;
	private int numMorePara;
	
	public InterferenceGraph(FlowGraph flowGraph)
	{
		this.stack = new Stack<Integer>();
		this.vecAdjacentTable = new Vector<Vector<Integer>>();
		for(int i = 0; i < MAX_NUM_BB; i++)
		{
			this.vecAdjacentTable.addElement(new Vector<Integer>());
		}
		this.arrayDegreePerTemp = new int[MAX_NUM_BB];
		this.arrayColorPerTemp = new int[MAX_NUM_BB];
		this.arrayLocationPerTemp = new int[MAX_NUM_BB];
		this.arrayUsedPerTemp = new boolean[MAX_NUM_BB];
		this.sReg = new MyBitSet();
		this.tReg = new MyBitSet();
		
		this.maxCalledNumPara = flowGraph.get_maxCalledNumPara();
		
		Vector<BasicBlock> tempVecBB = flowGraph.get_vecBB();
		for(int i = 0; i < tempVecBB.size(); i++)
		{
			BasicBlock tempBB = tempVecBB.elementAt(i);
			for(int j = tempBB.vecLivenessPerStmt.size() - 1; j >= 0 ; j--)
			{
				this.add_interference(tempBB.vecLivenessPerStmt.elementAt(j), tempBB.vecDefInfoPerStmt.elementAt(j));
			}
		}
	}
	
	public void add_interference(MyBitSet livenessInfo, MyBitSet defInfo)
	{
		int defIdx = livenessInfo.nextSetBit(0);
		for(int i = livenessInfo.nextSetBit(0); i >= 0; i = livenessInfo.nextSetBit(i + 1))
		{
			for (int j = livenessInfo.nextSetBit(i + 1); j >= 0; j = livenessInfo.nextSetBit(j + 1))
			{
				if(i != j)
					this.add_edge(i, j);
			}
			if(defIdx >= 0)
				this.add_edge(defIdx, i);
		}
	}
	
	public void add_edge(int a, int b)
	{
		if(this.vecAdjacentTable.elementAt(a).contains(b))
			return;
		if(a > this.N)
		{
			this.N = a;
		}
		if(b > this.N)
		{
			this.N = b;
		}
		this.vecAdjacentTable.elementAt(a).addElement(b);
		this.vecAdjacentTable.elementAt(b).addElement(a);
		this.arrayDegreePerTemp[a]++;
		this.arrayDegreePerTemp[b]++;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
