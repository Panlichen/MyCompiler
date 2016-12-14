package spiglet.spiglet2kanga.flowgraph;

public class TempInfo {
	private int tempNum;
	private boolean typeDU;//false: define(die); true: use(live); use boolean to use BitSet
	
	public TempInfo(int tempNum)
	{
		this.tempNum = tempNum;
		this.typeDU = true;
	}
	
	public void change_typeDU()
	{
		this.typeDU = !this.typeDU;
	}
	
	public int get_temp_num()
	{
		return this.tempNum;
	}
	
	public boolean is_use()
	{
		return this.typeDU;
	}
	
	public String toString()
	{
		return "TEMP " + this.tempNum;
	}
}
