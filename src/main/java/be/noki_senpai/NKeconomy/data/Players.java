package be.noki_senpai.NKeconomy.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import be.noki_senpai.NKeconomy.NKeconomy;

public class Players 
{
	private UUID playerUUID;
	private String playerName;
	private int playerID; //ID in database
	private Account playerAccount;
	
	public Players(UUID tmp_UUID) //throws SQLException
	{
		setPlayerUUID(tmp_UUID);
		setPlayerName(Bukkit.getOfflinePlayer(playerUUID).getName());
		
		Connection bdd = null;
		ResultSet resultat = null;
		PreparedStatement ps = null;
		String req = null;
		
		
		try 
		{
			// Database connection
			bdd = NKeconomy.getInstance().getConnection();
			
			// Get 'id' and 'name' from database
			req = "SELECT id, name FROM " + NKeconomy.table.get("players") + " WHERE uuid = '" + playerUUID.toString() + "'";
			ps = bdd.prepareStatement(req);
	        resultat = ps.executeQuery();
	        
	        if(resultat.next()) 
	        {
	        	setPlayerID(resultat.getInt("id"));
	        	// If names are differents, update in database
	        	if(!resultat.getString("name").equals(playerName))
	        	{
	        		ps.close();
		        	resultat.close();
		        	
	        		req = "UPDATE " + NKeconomy.table.get("players") + " SET name = '" + playerName + "' WHERE id=" + playerID + "; ";  
			        ps = bdd.prepareStatement(req);
			        ps.executeUpdate();
	        	}
	        }
	        else
	        {
	        	//Add new player on database
	        	ps.close();
	        	resultat.close();
	        	
	        	req = "INSERT INTO " + NKeconomy.table.get("players") + " (uuid, name) VALUES ('" + playerUUID.toString() + "', '" + playerName + "')";		        
		        ps = bdd.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);  
		        ps.executeUpdate();  
		        resultat = ps.getGeneratedKeys();    
		        
		        resultat.next();  
		        setPlayerID(resultat.getInt(1));
		        
		        ps.close();
		        resultat.close();
		        
		        req = "INSERT INTO " + NKeconomy.table.get("accounts") + " (player_id, amount) VALUES ('" + playerID + "', '" + NKeconomy.startAmount + "')";		        
		        ps = bdd.prepareStatement(req);  
		        ps.executeUpdate();
		        
		        ps.close();
		        resultat.close();
	        }
	        
	        
		} 
		catch (SQLException e1) 
		{
			NKeconomy.getInstance().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " Error while setting a player. (Error#data.Players.000)");
			e1.printStackTrace();
		}
		
		try 
		{
			
			req = "SELECT id, amount FROM " + NKeconomy.table.get("accounts") + " WHERE player_id = " + playerID;
			ps = bdd.prepareStatement(req);
	        resultat = ps.executeQuery();
	        
	        if(resultat.next()) 
	        {
	        	playerAccount = new Account(resultat.getInt("id"),resultat.getDouble("amount"));
	        }
	        else
	        {
	        	ps.close();
		        resultat.close();
		        
	        	req = "INSERT INTO " + NKeconomy.table.get("accounts") + " (player_id, amount) VALUES  ('" + playerID + "', '" + NKeconomy.startAmount + "')";		        
		        ps = bdd.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);  
		        ps.executeUpdate();
		        resultat = ps.getGeneratedKeys();   
		        
		        if(resultat.next())
		        {
		        	playerAccount = new Account(resultat.getInt(1), NKeconomy.startAmount);
		        }
		        
	        }
	        
	        
	        ps.close();
	        resultat.close();
		} 
		catch (SQLException e1) 
		{
			NKeconomy.getInstance().getConsole().sendMessage(ChatColor.DARK_RED + NKeconomy.PName + " Error while setting a player. (Error#data.Players.001)");
			e1.printStackTrace();
		}
		
		//NKeconomy.getInstance().getConsole().sendMessage(ChatColor.BLUE + NKeconomy.PName + " Setting player " + playerName + " (uuid : " + playerUUID + ") | id : " + playerID + ".");		
	}
	
	
	//Getter & Setter 'playerUUID'
	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID)
	{
		this.playerUUID = playerUUID;
	}

	//Getter & Setter 'playerName'
	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	//Getter & Setter 'playerID'
	public int getPlayerID()
	{
		return playerID;
	}

	public void setPlayerID(int playerID)
	{
		this.playerID = playerID;
	}
	
	//Getter & Setter 'playerAccount'
	public Account getPlayerAccount()
	{
		return playerAccount;
	}

	public void setPlayerAccount(int accountID, double amount)
	{
		this.playerAccount = new Account(accountID, amount);
	}
	
	public void save()
	{
		Connection bdd = null;
		PreparedStatement ps = null;
		String req = null;
		
		req = "UPDATE " + NKeconomy.table.get("accounts") + " SET amount='" + getPlayerAccount().getAmount() + "' WHERE player_id='" + playerID + "'"; 

        try
		{
        	bdd = NKeconomy.getInstance().getConnection();
			ps = bdd.prepareStatement(req);
			ps.executeUpdate();
			ps.close();
		} 
        catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
	//For Vault
	
	public static double getBalance(String playerName)
	{
		if(NKeconomy.players.containsKey(playerName))
    	{
    		return NKeconomy.players.get(playerName).getPlayerAccount().getAmount();
    	}
		else
		{
			Connection bdd = null;
			PreparedStatement ps = null;
			ResultSet resultat = null;
			String req = null;
			
			req = "SELECT amount FROM " + NKeconomy.table.get("accounts") + " LEFT JOIN " + NKeconomy.table.get("players") + " ON " + NKeconomy.table.get("accounts") + ".player_id = " + NKeconomy.table.get("players") + ".id WHERE " + NKeconomy.table.get("players") + ".name ='" + playerName + "'";	
			
	        try
			{
	        	bdd = NKeconomy.getInstance().getConnection();
				ps = bdd.prepareStatement(req);
		        resultat = ps.executeQuery();
		        if(resultat.next())
		        {
		        	double am = resultat.getDouble("amount");
		        	ps.close();
		        	resultat.close();
		        	return am;
		        }
		        ps.close();
	        	resultat.close();
			} 
	        catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
		return 0;
	}
	
	
	
	public static boolean hasAccount(String playerName)
	{
		if(NKeconomy.players.containsKey(playerName))
    	{
    		return true;
    	}
		else
		{
			Connection bdd = null;
			PreparedStatement ps = null;
			ResultSet resultat = null;
			String req = null;
			
			req = "SELECT COUNT(name) FROM " + NKeconomy.table.get("accounts") + " LEFT JOIN " + NKeconomy.table.get("players") + " ON " + NKeconomy.table.get("accounts") + ".player_id = " + NKeconomy.table.get("players") + ".id WHERE " + NKeconomy.table.get("players") + ".name ='" + playerName + "'";	
			
	        try
			{
	        	bdd = NKeconomy.getInstance().getConnection();
				ps = bdd.prepareStatement(req);
		        resultat = ps.executeQuery();
		        resultat.next();
		        
		        if(resultat.getInt(1)==1)
		        {
		        	ps.close();
		        	resultat.close();
		        	return true;
		        }
		        ps.close();
		        resultat.close();
			} 
	        catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
		return false;
	}
	
	
}
