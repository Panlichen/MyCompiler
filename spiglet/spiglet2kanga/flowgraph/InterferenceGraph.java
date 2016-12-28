package spiglet.spiglet2kanga.flowgraph;

import java.util.*;

public class InterferenceGraph {
	private static int MAX_NUM_BB = 10000;
	private static int MAX_COLOR = 18;
	private Vector<Vector<Integer>> vecAdjacentTable;
	private Stack<Integer> stack;
	private int[] arrayDegreePerTemp;
	private int[] arrayColorPerTemp;
	private int[] arrayLocationPerTemp;
	private boolean[] arrayUsedPerTemp;
	
	private int maxCalledNumPara;
	private int numSpilled;
	private MyBitSet tReg;
	private int numTReg;
	private MyBitSet sReg;
	private int numSReg;
	private int numMorePara;
	
	private MyBitSet tempIdxUsed;
	
	public MyBitSet get_tempIdxUsed()
	{
		return this.tempIdxUsed;
	}
	
	public InterferenceGraph(FlowGraph flowGraph)
	{
		this.tReg = new MyBitSet();
		this.sReg = new MyBitSet();
		this.tempIdxUsed = new MyBitSet();
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
		this.numTReg = 0;
		this.numSReg = 0;
		
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
		
		this.do_color();
		this.do_reg_distribution();
		//System.out.println("total spilled: " + numSpilled);
	}
	
	public void add_interference(MyBitSet livenessInfo, MyBitSet defInfo)
	{
		int defIdx = defInfo.nextSetBit(0);
		if(defIdx >= 0)
		{
			this.tempIdxUsed.set(defIdx);
		}
		for(int i = livenessInfo.nextSetBit(0); i >= 0; i = livenessInfo.nextSetBit(i + 1))
		{
			this.tempIdxUsed.set(i);
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

		this.vecAdjacentTable.elementAt(a).addElement(b);
		this.vecAdjacentTable.elementAt(b).addElement(a);
		this.arrayDegreePerTemp[a]++;
		this.arrayDegreePerTemp[b]++;
	}
	
	private void do_color()
	{
		for(int i = this.tempIdxUsed.nextSetBit(0); i >= 0; i = this.tempIdxUsed.nextSetBit(i + 1))
		{
			this.arrayUsedPerTemp[i] = false;
		}
		for(int i = this.tempIdxUsed.nextSetBit(0); i >= 0; i = this.tempIdxUsed.nextSetBit(i + 1))
		{
			int degree = -1, idx = -1, realMax = -1, realIdx = -1;
			for(int j = this.tempIdxUsed.nextSetBit(0); j >= 0; j = this.tempIdxUsed.nextSetBit(j + 1))
			{
				if(!this.arrayUsedPerTemp[j])
				{
					if(this.arrayDegreePerTemp[j] > realMax)
					{
						realMax = this.arrayDegreePerTemp[j];
						realIdx = j;
					}
					if(this.arrayDegreePerTemp[j] < MAX_COLOR && this.arrayDegreePerTemp[j] > degree)
					{
						degree = this.arrayDegreePerTemp[j];
						idx = j;
					}
				}
			}
			if(degree == -1)
			{
				this.arrayUsedPerTemp[realIdx] = true;
				this.arrayColorPerTemp[realIdx] = -1;
				this.delete_node(realIdx);
				this.numSpilled++;
			}
			else
			{
				this.arrayUsedPerTemp[idx] = true;
				this.stack.push(idx);
				this.delete_node(idx);
			}
		}
	}
	
	private void delete_node(int idx)
	{
		Vector<Integer> tempVec = this.vecAdjacentTable.elementAt(idx);
		for(int i = 0; i < tempVec.size(); i++)
		{
			if(!this.arrayUsedPerTemp[tempVec.elementAt(i)])
			{
				this.delete_edge(idx, tempVec.elementAt(i));
			}
		}
	}
	
	private void delete_edge(int i, int j)
	{
		this.arrayDegreePerTemp[i]--;
		this.arrayDegreePerTemp[j]--;
	}
	
	private void do_reg_distribution()
	{
		while(!stack.empty())
		{
			int tempNum = this.stack.pop();
			MyBitSet tempMBS = new MyBitSet();
			Vector<Integer> vecAdj = this.vecAdjacentTable.elementAt(tempNum);
			for(int i = 0; i < vecAdj.size(); i++)
			{
				int adj = vecAdj.elementAt(i);
				if(this.arrayColorPerTemp[adj] > 0)
				{
					tempMBS.set(this.arrayColorPerTemp[adj]);
				}
			}
			this.arrayColorPerTemp[tempNum] = tempMBS.nextClearBit(1);
			if(this.arrayColorPerTemp[tempNum] <= 10)
			{
				//this.numTReg++;
				this.tReg.set(this.arrayColorPerTemp[tempNum]);
			}
			else
			{
				//this.numSReg++;
				this.sReg.set(this.arrayColorPerTemp[tempNum]);
			}
		}
		this.numTReg = this.tReg.cardinality();
		this.numSReg = this.sReg.cardinality();
		int cnt = 0;
		for(int i = this.tempIdxUsed.nextSetBit(0); i >= 0; i = this.tempIdxUsed.nextSetBit(i + 1))
		{
			if(this.arrayColorPerTemp[i] == -1)
			{
				this.arrayLocationPerTemp[i] = cnt++;
			}
		}
		//
	}
	
	public void set_numMorePara(int i)
	{
		this.numMorePara = i;
	}
	public int get_numMorePara()
	{
		return this.numMorePara;
	}
	
	public int get_maxCalledNumPara()
	{
		return this.maxCalledNumPara;
	}
	
	public int get_numSpilled()
	{
		return this.numSpilled;
	}
	
	public int get_numTReg()
	{
		return this.numTReg;
	}
	
	public int get_numSReg()
	{
		return this.numSReg;
	}
	
	public boolean isSpilled(int tempNum)
	{
		return (this.arrayColorPerTemp[tempNum] == -1);
	}
	
	public int getColor(int tempNum)
	{
		if(this.arrayColorPerTemp[tempNum] == -1)
		{
			return this.numMorePara + this.numTReg + this.numSReg + this.arrayLocationPerTemp[tempNum];
		}
		else
		{
			return this.arrayColorPerTemp[tempNum];
		}
	}
}
