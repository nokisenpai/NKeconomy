package ovh.lumen.NKeconomy.data;

import ovh.lumen.NKcore.api.data.DBAccess;
import ovh.lumen.NKcore.api.data.NKServer;
import ovh.lumen.NKeconomy.enums.LogLevel;
import ovh.lumen.NKeconomy.interfaces.NKplugin;

import java.util.Map;
import java.util.TreeMap;

public class NKData
{
	public static DBAccess DBACCESS = new DBAccess();
	public static NKServer SERVER_INFO = null;
	public static String PREFIX = null;
	public static LogLevel LOGLEVEL = null;
	public static NKplugin PLUGIN = null;
	public static String PLUGIN_NAME = null;
	public static String PLUGIN_VERSION = null;
	public static String PLUGIN_AUTHOR = null;
	public static double START_AMOUNT = 100;
	public static String CURRENCY = null;
	public static Boolean ENABLE_CROSS_SERVER = true;
	public static Map<String, Account> ACCOUNTS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}
