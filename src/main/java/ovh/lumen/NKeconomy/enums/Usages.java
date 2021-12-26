package ovh.lumen.NKeconomy.enums;

import ovh.lumen.NKeconomy.data.NKData;

public enum Usages
{
	ROOT_CMD("/" + NKData.PLUGIN_NAME.toLowerCase() + " [reload]"),
	ECO_CMD("/eco <money|pay|top|give|take|set>"),
	ECO_GIVE_CMD("/eco give <joueur> <montant>"),
	ECO_PAY_CMD("/eco pay <joueur> <montant>"),
	ECO_SET_CMD("/eco set <joueur> <montant>");

	private final String value;

	Usages(String value)
	{
		this.value = value;
	}

	public String toString()
	{
		return InternalMessages.PREFIX_USAGE + this.value;
	}
}
