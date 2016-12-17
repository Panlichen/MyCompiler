package spiglet.spiglet2kanga.codesketch;



public class KangaCode{
	private String code;
	public KangaCode(String str)
	{
		this.code = str;
	}
	
	public String toString()
	{
		return this.code;
	}
	
	public boolean isLabel()
	{
		return this.code.contains("_") && !this.code.contains(" ");
	}
}
