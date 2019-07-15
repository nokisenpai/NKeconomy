package be.noki_senpai.NKeconomy.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLConnect 
{
    private static HikariConfig jdbcConfig = new HikariConfig();
    private static HikariDataSource ds = null;    
	
	/*
	private SQLConnect()
	{
		
	}
	private static class DataSourceHolder 
	{
		private static final SQLConnect INSTANCE = new SQLConnect();
	}
	public static SQLConnect getInstance() 
	{
		return DataSourceHolder.INSTANCE;
	}*/

	public static HikariDataSource getHikariDS() 
	{
		return ds;
	}
	
	public static void setInfo(String host_, int port_, String dbName_, String user_, String password_)
	{
		jdbcConfig.setPoolName("NKeconomy");
	    jdbcConfig.setMaximumPoolSize(10);
	    jdbcConfig.setMinimumIdle(2);
	    jdbcConfig.setJdbcUrl("jdbc:mysql://" + host_ + ":" + port_ + "/" + dbName_ + "?useSSL=false");
	    jdbcConfig.setUsername(user_);
	    jdbcConfig.setPassword(password_);
	    ds = new HikariDataSource(jdbcConfig);
	}

}