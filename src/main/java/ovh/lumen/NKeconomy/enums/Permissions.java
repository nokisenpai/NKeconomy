package ovh.lumen.NKeconomy.enums;

import ovh.lumen.NKeconomy.data.NKData;

public enum Permissions
{
	USER("user"),
	ADMIN("admin"),

	ROOT_CMD(""),
	ROOT_RELOAD_CMD(".reload"),
	ECO_GIVE_CMD(".give"),
	ECO_PAY_CMD(".pay"),
	ECO_MONEY_CMD(".money"),
	ECO_MONEY_OTHER_CMD(".money.other"),
	ECO_SET_CMD(".set"),
	ECO_TAKE_CMD(".take"),
	ECO_TOP_CMD(".top");

	private final String value;

	Permissions(String value)
	{
		this.value = value;
	}

	public String toString()
	{
		return NKData.PLUGIN_NAME + this.value;
	}
}
