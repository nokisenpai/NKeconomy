# NKeconomy
A simple spigot/bukkit economy plugin.

- Vault compatibility
- Cross server support (via Bungee)
- SQL storage (only)

###### Prerequisites

- Vault
- SQL serveur


###### Commands

- /eco (List accessible commands)
- /eco money (Display your amount) - (NKeco.money)
- /eco money [player] (Display player amount) - (NKeco.money.other)
- /eco pay <player> <amount> (Pay a player) - (NKeco.pay)
- /eco top [page] (Display top amount by page of 10 players) - (NKeco.top)
- /eco give <player> <amount> (Give amount to a player) - (NKeco.give)
- /eco take <player> <amount> (Take amount from a player) - (NKeco.take)
- /eco set <player> <amount> (Set player amount) - (NKeco.set)


###### Other nodes

- NKeco.user (Group NKeco.money, NKeco.pay and NKeco.top)
- NKeco.admin (Group all nodes)
