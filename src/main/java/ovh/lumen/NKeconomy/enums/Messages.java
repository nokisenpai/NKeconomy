package ovh.lumen.NKeconomy.enums;

import org.bukkit.ChatColor;
import ovh.lumen.NKeconomy.managers.AccountManager;
import ovh.lumen.NKeconomy.managers.ConfigManager;

public enum Messages
{
	PERMISSION_MISSING(ChatColor.RED + "Vous n'avez pas la permission."),
	ROOT_PLUGIN_INFO_MSG(ChatColor.GREEN + "%0% v%1%" + ChatColor.ITALIC + " by %2%"),
	ROOT_RELOAD_MSG(ChatColor.GREEN + "%0% a été rechargé."),
	ARG_MUST_BE_NUMBER_MSG(ChatColor.RED + "Le montant doit être un nombre"),
	ARG_MUST_BE_POSITIVE_NUMBER_MSG(ChatColor.RED + "Le montant doit être plus grand que 0 !"),
	UNKNOWN_PLAYER(ChatColor.RED + "Joueur introuvable"),
	NO_CONSOLE_USE(ChatColor.RED + "Vous ne pouvez pas utiliser cette commande dans la console."),
	ECO_GIVE_SENDER_NOTIFY(ChatColor.DARK_GREEN + "Tous les joueurs connectés" + ChatColor.GREEN + " ont reçu %0% %1%"),
	ECO_PAY_SENDER_NOTIFY(ChatColor.GREEN + "Vous avez donné %0% %1% à " + ChatColor.DARK_GREEN + "%2%"),
	ECO_SET_SENDER_NOTIFY(ChatColor.DARK_GREEN + "%0%" + ChatColor.GREEN + " a maintenant %1% %2%"),
	ECO_MONEY_SELF_MSG(ChatColor.GREEN + "Vous avez %0% %1%"),
	ECO_MONEY_OTHER_MSG(ChatColor.DARK_GREEN + "%0%" + ChatColor.GREEN + " a %1% %2%"),
	ECO_MONEY_CONSOLE_NO_ARG_MSG(ChatColor.RED + "Veuillez spécifier un joueur."),
	ECO_GIVE_TARGET_NOTIFY(ChatColor.GREEN + " Vous avez reçu %0% %1%"),
	ECO_PAY_TARGET_NOTIFY(ChatColor.DARK_GREEN + "%0%" + ChatColor.GREEN + " vous a donné %1% %2%"),
	ECO_TAKE_TARGET_NOTIFY(ChatColor.GREEN + "Vous avez perdu %0% %1%"),
	ECO_TAKE_SENDER_NOTIFY(ChatColor.DARK_GREEN + "%0%" + ChatColor.GREEN + " a perdu %1% %2%"),
	ECO_SET_TARGET_NOTIFY(ChatColor.GREEN + "Vous avez maintenant %0% %1%"),
	ECO_NOT_ENOUGH_MONEY(ChatColor.RED + "Vous n'avez pas assez de %0%"),
	ECO_TARGET_NOT_ENOUGH_MONEY(ChatColor.RED + "Le joueur n'a pas assez de %0%"),
	ECO_TOP_NOBODY(ChatColor.RED + "Il n'y a personne à cette page du classement"),
	ECO_TOP_ITS_ME(ChatColor.GOLD + "" + ChatColor.BOLD + "> " + ChatColor.RESET),
	ECO_TOP_1("\n" + ChatColor.GOLD + "%0%. %1%" + ChatColor.GOLD + ChatColor.BOLD + "%2%   %3% %4%"),
	ECO_TOP_LINE("\n" + ChatColor.GREEN + "%0%. %1%" + ChatColor.GREEN + ChatColor.DARK_GREEN + "%2%   %3% %4%"),
	ECO_TOP_TITLE(ChatColor.GREEN + "---- Top %0%" + ChatColor.GREEN + " ---- " + ChatColor.DARK_GREEN + "%1%" + ChatColor.GREEN + " à " + ChatColor.DARK_GREEN + "%2%" + ChatColor.GREEN
			+ " -----------------------------");

	private final String value;

	Messages(String value)
	{
		this.value = value;
	}

	public String toString()
	{
		return this.value;
	}
}
