package ovh.lumen.NKeconomy.utils;

public class CheckType
{
	public static boolean isNumber(String str)
	{
		return str.matches("[-+]?\\d+(\\.\\d+)?");
	}
}
