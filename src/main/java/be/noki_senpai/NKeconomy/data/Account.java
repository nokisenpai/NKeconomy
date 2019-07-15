package be.noki_senpai.NKeconomy.data;

public class Account 
{
	private int id = -1;
	private Double amount = 0.0;
	
	public Account(int id_, Double amount_)
	{
		setId(id_);
		setAmount(amount_);
	}
	
	//Getter & Setter 'id'
	public int getId()
	{
		return id;
	}
	
	public void setId(int id_)
	{
		id = id_;
	}
	
	//Getter & Setter 'amount'
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
}

