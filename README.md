# SkEco
*A simple yet effective vault economy Skript for your server.*

![Alt Text](https://i.imgur.com/KQAXWNe.png)

Looking to ditch Essentials, but still need an economy plugin? Well look no further... it's right here!

This simple yet effective economy plugin, will run all your economy needs. Unlike other Skript based economy plugins, SkEco will link in with the VaultAPI and any plugins that require a Vault based economy, such as JobsReborn, ChestShop, ShopChest, AuctionHouse, GriefPrevention and many more.

SkEco requires very little setup. It will run great out of the box. But I have added a few things you can configure yourself, such as the prefix for chat messages, your starting balance for new players, the name and symbol for your currency as well as the position of your currency symbol.
**_NOTE - By default players will have no permissions, make sure to add them to your permission file_**

**Dependencies:**
- [Skript](https://github.com/SkriptLang/Skript/releases) [Tested with Bensku 2.2 Dev36]
- [Reqn](https://forums.skunity.com/resources/reqn.95/) [For versions >=1.2]
- [SkVault](https://forums.skunity.com/resources/skvault-skript-economy-registerer.576/) + [Vault](https://www.spigotmc.org/resources/vault.34315/)
- *the next 2 can be removed for versions 1.2 and above*
- SkQuery [For versions <=1.1.2]
- TuSKe [Pikachu Patch] [For versions <=1.1.2]

**Tested Spigot Versions:**
- 1.12.2
- I haven't tried this on any older versions of Spigot. It most likely will run, but it will highly depend on whether or not the version can run SkVault.

**Player Commands:**
- /bal - Player's can check their balance
- /bal <player> - Check another player's balance
- /baltop - Shows the top balances of players
- /pay <player> <amount> - Pay money to another player from your account

**Admin Commands:**
- /eco - Shows the admin eco options
- /eco reset <player> - Resets a players balance to the preset starting balance
- /eco set <player> <amount> - Set's a players balance
- /eco add <player> <amount> - Add money to a player's balance
- /eco remove <player> <amount> - Remove money from a player's balance
- /eco convert <economy plugin> - Convert your player's balances from your old economy plugin to your new one. Currently only set up with essentials. I can add more upon request.
- /eco version - Show's the current version you are running, and if an update is available

**Permissions:**
- eco.bal - Player can check their own balance
- eco.bal.other - Player can check balance of another player
- eco.baltop - Player can see top 10 balance of players
- eco.pay - Player can pay money to another player
- eco.admin - Player can set/reset/add/remove to/from balance of other players
- eco.* - Player can use ALL SkEco commands

**Installation:**
1) Install Skript on your server if you haven't already.
2) Install all dependencies as per plugin instructions.
3) Place Skript into the scripts folder under plugins/Skript/scripts
4) Restart server or run /skript reload all
5) Give player's the appropriate permissions

**Economy Conversion:**
- If you are currently using essentials, simply run the command /eco convert essentials ... it will run on the console, and you can see the progress in the console.
- If you are using any other economy plugin, you will have to do the conversion using vaults command /vault-convert <your eco plugin here> skeco ... if it doesn't work, it will tell you the exact name of your economy plugin to put in.
