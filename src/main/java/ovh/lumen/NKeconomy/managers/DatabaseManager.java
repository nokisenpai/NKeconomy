package ovh.lumen.NKeconomy.managers;

import ovh.lumen.NKeconomy.data.NKData;
import ovh.lumen.NKeconomy.enums.InternalMessages;
import ovh.lumen.NKeconomy.exceptions.SetupException;
import ovh.lumen.NKeconomy.utils.NKLogger;
import ovh.lumen.NKeconomy.utils.SQLConnect;

import java.sql.*;

public final class DatabaseManager
{
	private static Connection bdd = null;

	private DatabaseManager() {}

	public enum Tables
	{
		ACCOUNTS("accounts");

		private final String name;

		Tables(String name)
		{
			this.name = name;
		}

		public String toString()
		{
			return NKData.PREFIX + name;
		}
	}

	public static void load() throws SetupException
	{
		SQLConnect.setInfo(NKData.DBACCESS);

		try
		{
			connect();
		}
		catch(SQLException e)
		{
			throw new SetupException(InternalMessages.DATABASE_CANT_CONNECT.toString());
		}

		try
		{
			if(!checkTables())
			{
				createTable();
			}
		}
		catch(SQLException e)
		{
			throw new SetupException(InternalMessages.DATABASE_CANT_CREATE_TABLES.toString());
		}
	}

	public static void unload()
	{
		if(bdd != null)
		{
			try
			{
				bdd.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void connect() throws SQLException
	{
		bdd = SQLConnect.getHikariDS().getConnection();
	}

	private static void createTable() throws SQLException
	{
		try(Statement s = bdd.createStatement())
		{
			String req = "CREATE TABLE IF NOT EXISTS `" + Tables.ACCOUNTS + "` ("
					+ "`player_uuid` varchar(40) NOT NULL,"
					+ "`amount` double NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			s.execute(req);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		NKLogger.log(InternalMessages.DATABASE_CREATE_TABLES_SUCCESS.toString());
	}

	private static boolean checkTables()
	{
		String req = "SHOW TABLES FROM " + NKData.DBACCESS.getDbName() + " LIKE '" + NKData.PREFIX + "%'";

		try(PreparedStatement ps = bdd.prepareStatement(req); ResultSet result = ps.executeQuery())
		{
			int count = 0;

			while(result.next())
			{
				count++;
			}

			result.close();
			ps.close();

			if(count < Tables.values().length)
			{
				return false;
			}
		}
		catch(SQLException e)
		{
			NKLogger.error(InternalMessages.DATABASE_CANT_CHECK_TABLES.toString());

			return false;
		}

		return true;
	}

	public static Connection getConnection()
	{
		try
		{
			if(!bdd.isValid(1))
			{
				if(!bdd.isClosed())
				{
					bdd.close();
				}

				connect();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return bdd;
	}
}
