package ovh.lumen.NKeconomy.managers;

import org.bukkit.configuration.file.FileConfiguration;
import ovh.lumen.NKcore.api.data.DBAccess;
import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.InternalMessages;
import ovh.lumen.NKeconomy.enums.LogLevel;
import ovh.lumen.NKeconomy.utils.NKLogger;

public final class ConfigManager
{
	private ConfigManager() {}

	private static FileConfiguration config = null;

	public static void init(FileConfiguration config)
	{
		ConfigManager.config = config;
	}

	public static void load()
	{
		boolean useNKcoreAccess = config.getBoolean("use-nkcore-access", true);

		if(useNKcoreAccess)
		{
			NKData.DBACCESS = NKcoreAPIManager.nKcoreAPI.getDBAccess();
		}
		else
		{
			DBAccess dbAccess = new DBAccess();
			dbAccess.setHost(config.getString("host"));
			dbAccess.setPort(config.getInt("port"));
			dbAccess.setDbName(config.getString("dbName"));
			dbAccess.setUser(config.getString("user"));
			dbAccess.setPassword(config.getString("password"));

			NKData.DBACCESS = dbAccess;
		}

		NKData.PREFIX = config.getString("table-prefix", NKData.PLUGIN_NAME + "_");

		NKData.SERVER_INFO = NKcoreAPIManager.nKcoreAPI.getNKServer();

		try
		{
			NKData.LOGLEVEL = LogLevel.valueOf(config.getString("log-level", "LOG").toUpperCase());
		}
		catch(IllegalArgumentException e)
		{
			NKLogger.error(InternalMessages.CONFIG_KEY_LOGLEVEL_BAD_VALUE.toString());
			NKData.LOGLEVEL = LogLevel.LOG;
		}

		NKLogger.setLogLevel(NKData.LOGLEVEL);

		NKData.START_AMOUNT = config.getDouble("start-amount", 100);
		NKData.CURRENCY = config.getString("currency", "€").replace("&", "§");
		NKData.ENABLE_CROSS_SERVER = config.getBoolean("enable-cross-server", true);

	}
}
