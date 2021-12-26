package ovh.lumen.NKeconomy.data;

import org.bukkit.Bukkit;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Account
{
	private final UUID uuid;
	private Double amount;

	public Account(UUID uuid)
	{
		this.uuid = uuid;
		String playerName = Bukkit.getOfflinePlayer(this.uuid).getName();
		this.amount = AccountManager.getOfflineBalance(playerName);

		try
		{
			if(this.amount < 0)
			{
				Connection bdd = DatabaseManager.getConnection();
				String req = "INSERT INTO " + DatabaseManager.Tables.ACCOUNTS + " ( player_uuid, amount ) VALUES ( ? , ? )";
				PreparedStatement ps = bdd.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, this.uuid.toString());
				ps.setDouble(2, NKData.START_AMOUNT);

				ps.executeUpdate();

				this.amount = NKData.START_AMOUNT;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public double getAmount()
	{
		return amount;
	}

	public void setAmount(Double amount)
	{
		this.amount = amount;
	}

	public void addAmount(Double amount)
	{
		this.amount += amount;
	}

	public void removeAmount(Double amount)
	{
		this.amount -= amount;
	}

	public void save()
	{
		try
		{
			Connection bdd = DatabaseManager.getConnection();
			String req = "UPDATE " + DatabaseManager.Tables.ACCOUNTS + " SET amount = ? WHERE player_uuid = ?";
			PreparedStatement ps = bdd.prepareStatement(req);
			ps.setDouble(1, getAmount());
			ps.setString(2, uuid.toString());

			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}
