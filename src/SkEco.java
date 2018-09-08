import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SkEco
extends JavaPlugin {
    private boolean legacyIsLoaded = false;

    public void onEnable() {
        block5 : {
            block6 : {
                block4 : {
                    for (String string : this.getDescription().getSoftDepend()) {
                        if (this.getServer().getPluginManager().isPluginEnabled(string)) continue;
                        for (String string2 : "[&bSkEco] &c&l*** Warning ***\n[&bSkEco] &cYou do not have all required plugins!".split("\n")) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)string2));
                        }
                        this.getPluginLoader().disablePlugin((Plugin)this);
                        return;
                    }
                    if (!Skript.methodExists(ScriptLoader.class, (String)"getLoadedFiles", (Class[])new Class[0])) break block4;
                    if (ScriptLoader.getLoadedFiles().stream().map(File::getName).collect(Collectors.toList()).contains("SkEco")) break block5;
                    break block6;
                }
                if (this.legacyIsLoaded) break block5;
            }
            this.loadSkVault();
            this.loadReqn();
            Bukkit.getScheduler().runTaskLater((Plugin)this, this::initialize, 1L);
        }
    }

    private void initialize() {
        this.legacyIsLoaded = true;
        try {
            InputStream inputStream = IOUtils.toInputStream((String)String.join((CharSequence)"", "#PERMISSIONS:\n\n#eco.bal - Player can check their own balance\n#eco.bal.other - Player can check balance of another player\n#eco.baltop - Player can see top balances of players\n#eco.pay - Player can pay money to another player\n\n#eco.admin - Player can set/reset/add/remove to/from balance of other players, also sees updates on login\n#eco.* - Player can use ALL eco commands\n\n#SERVER OPTIONS - CHANGE ANYTHING HERE YOU WOULD LIKE\noptions:\n    #Set the amount a player will receive when they first join your server\n    firstjoin: 500\n\n    #The logo/name for your currency, and position (use either before or after)\n    symbol: $ #some other options \u20ac \u00a5 \n    currencyname: dollar\n    position: before #Currency symbol position, either before or after currency\n    \n    #You can change the prefix of the economy messages\n    prefix: &7[&a&lECO&7]\n    #You can change the color of the economy messages\n    msgcolor: &6\n    #Whether or not players get message when admin/console gives/takes money using /eco add/remove\n    consolemsg: true\n\n    #Permission message\n    permmessage: &cYou do not have the required permssion for this command\n\n    version: 1.3 #Do not change\n    \n#CODE - PLEASE DO NOT EDIT BELOW THIS LINE OR YOU RISK LOSING SUPPORT FOR THIS SKRIPT\n\n# -- VERSION CHECK -- \non load:\n    send a \"GET\" request to \"https://raw.githubusercontent.com/ShaneBeee/skripts/master/SkecoVersion\"\n    set {_t} to last http response\n    set {_j} to {_t}'s body\n    set {_v::*} to {_j} split at \"|\"\n    send \"&7[&a&lSkEco&7] - &2Loaded successfully\" to console\n    if \"{@version}\" is not equal to \"%{_v::2}%\":\n        send \"&7[&a&lSkEco&7] - &2Running version: {@version}\" to console\n        send \"&7[&a&lSkEco&7] - &cUpdate available, version: %{_v::2}%\" to console\n    else:\n        send \"&7[&a&lSkEco&7] - &2Running current version: {@version}\" to console\n    delete {eco::indexes::*}\n    delete {eco::saved::balance::*}\n\non join:\n    wait 1 second\n    if player has permission \"eco.admin\":\n        send a \"GET\" request to \"https://raw.githubusercontent.com/ShaneBeee/skripts/master/SkecoVersion\"\n        set {_t} to last http response\n        set {_j} to {_t}'s body\n        set {_v::*} to {_j} split at \"|\"\n        if \"{@version}\" is not equal to \"%{_v::2}%\":\n            send \"&7[&a&lSkEco&7] - &2Running version: {@version}\" to player\n            send \"&7[&a&lSkEco&7] - &cUpdate available, version: %{_v::2}%\" to player\n\n\n# -- VAULT API HOOK-- \n\non vault player balance request:\n    return {eco::balance::%uuid of event-offline player%}\non vault currency decimals request:\n    return 2\non vault deposit request:\n    add event-number to {eco::balance::%uuid of event-offline player%}\n    return economy response with amount modified event-number, new balance {eco::balance::%uuid of event-offline player%}, response type success, and error message \"none\"\non vault withdraw request:\n    remove event-number from {eco::balance::%uuid of event-offline player%}\n    return economy response with amount modified event-number, new balance {eco::balance::%uuid of event-offline player%}, response type success, and error message \"none\"\non vault check player balance request:\n    if {eco::balance::%uuid of event-offline player%} is greater than event-number:\n        return true\n    else:\n        return false\non vault enabled status request:\n    return true\non vault economy name request:\n    return \"SkEco\"\non vault format currency request:\n    if \"{@position}\" is \"before\":\n        return \"{@symbol}%event-number%\"\n    if \"{@position}\" is \"after\":\n        return \"%event-number%{@symbol}\"\non vault plural currency name request:\n    return \"{@currencyname}s\"\non vault singular currency name request:\n    return \"{@currencyname}\"\non player has account request:\n    if {eco::balance::%uuid of event-offline player%} is set:\n        return true\n    else:\n        return false\non create account request:\n    set {eco::balance::%uuid of event-offline player%} to 0\n    set {eco::player::%uuid of event-offline player%} to event-offline player\n    return true\n\n\n# -- FUNCTIONS -- \nfunction formatMoney(money: number) :: string:\n    set {_money} to \"%{_money}%\"\n    if {_money} contains \".\":\n        if length of {_money} - first index of \".\" in {_money} is 2:\n            return {_money}\n        return join {_money} and \"0\"\n    else:\n        return {_money}\n\non first join:\n    set {eco::balance::%uuid of player%} to {@firstjoin}\n    set {eco::player::%uuid of player%} to player\n\non join:\n    set {eco::player::%uuid of player%} to player\n\ncommand /balance [<offline player>]:\n    usage: Type /bal(ance) to see your balance\n    aliases: bal, money\n    executable by: players and console\n    trigger:\n        if arg-1 is not set:\n            if sender is a player:\n                if player has permission \"eco.bal\":\n                    if \"{@position}\" = \"before\":\n                        send \"{@prefix} {@msgcolor}Balance: &a{@symbol}%formatMoney({eco::balance::%uuid of player%})%\"\n                    if \"{@position}\" = \"after\":\n                        send \"{@prefix} {@msgcolor}Balance: &a%formatMoney({eco::balance::%uuid of player%})%{@symbol}\"\n                else:\n                    send \"{@prefix} {@permmessage}\"\n            if sender is console:\n                send \"{@prefix} - {@msgcolor}The console has no balance\"\n        if arg-1 is set:\n            if sender has permission \"eco.bal.other\":\n                if {eco::balance::%uuid of arg-1%} is set:\n                    if \"{@position}\" = \"before\":\n                        send \"{@prefix} &3%arg-1%{@msgcolor}'s Balance: &a{@symbol}%formatMoney({eco::balance::%uuid of arg-1%})%\"\n                    if \"{@position}\" = \"after\":\n                        send \"{@prefix} &3%arg-1%{@msgcolor}'s Balance: &a%formatMoney({eco::balance::%uuid of arg-1%})%{@symbol}\"\n                if {eco::balance::%uuid of arg-1%} is not set:\n                    send \"{@prefix} - &3%arg-1% &chas not logged into this server\"\n            else:\n                send \"{@prefix} {@permmessage}\"\n\ncommand /eco [<text>] [<text>] [<number>]:\n    executable by: players and console\n    aliases: economy\n    trigger:\n        if sender has permission \"eco.admin\":\n            if arg-1 is set:\n                if arg-1 is \"reset\":\n                    if arg-2 is set:\n                        set {_uuid} to uuid of arg-2 parsed as offline player\n                        set {eco::balance::%{_uuid}%} to {@firstjoin}\n                        if \"{@position}\" = \"before\":\n                            send \"{@prefix} - &3%arg-2%{@msgcolor}'s balance has been reset to &3{@symbol}{@firstjoin}\"\n                            if {@consolemsg} is true:\n                                send \"{@prefix} - &3%sender% {@msgcolor}has reset the balance of your account back to &3{@symbol}{@firstjoin}\" to arg-2 parsed as player\n                        if \"{@position}\" = \"after\":\n                            send \"{@prefix} - &3%arg-2%{@msgcolor}'s balance has been reset to &3{@firstjoin}{@symbol}\"\n                            if {@consolemsg} is true:\n                                send \"{@prefix} - &3%sender% {@msgcolor}has reset the balance of your account back to &3{@firstjoin}{@symbol}\" to arg-2 parsed as player\n                    else:\n                        send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco reset <player>\" to sender\n                if arg-1 is \"set\":\n                    if arg-2 is set:\n                        if arg-3 is set:\n                            set {_uuid} to uuid of arg-2 parsed as offline player\n                            set {eco::balance::%{_uuid}%} to arg-3\n                            if \"{@position}\" = \"before\":\n                                send \"{@prefix} - &3%arg-2%{@msgcolor}'s balance has been set to &3{@symbol}%formatMoney(arg-3)%\"\n                                if {@consolemsg} is true:\n                                    send \"{@prefix} - &3%sender% {@msgcolor}has set the balance of your account to &3{@symbol}%formatMoney(arg-3)%\" to arg-2 parsed as player\n                            if \"{@position}\" = \"after\":\n                                send \"{@prefix} - &3%arg-2%{@msgcolor}'s balance has been set to &3%formatMoney(arg-3)%{@symbol}\"\n                                if {@consolemsg} is true:\n                                    send \"{@prefix} - &3%sender% {@msgcolor}has set the balance of your account to &3%formatMoney(arg-3)%{@symbol}\" to arg-2 parsed as player\n                        else:\n                            send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco set <player> <amount>\" to sender\n                    else:\n                        send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco set <player> <amount>\" to sender\n                if arg-1 is \"add\" or \"give\":\n                    if arg-2 is set:\n                        if arg-3 is set:\n                            set {_uuid} to uuid of arg-2 parsed as offline player\n                            add arg-3 to {eco::balance::%{_uuid}%}\n                            if \"{@position}\" = \"before\":\n                                send \"{@prefix} - {@msgcolor}Added &3{@symbol}%formatMoney(arg-3)% {@msgcolor}to account of &3%arg-2%\"\n                                if {@consolemsg} is true:\n                                    send \"{@prefix} - &3%sender% {@msgcolor}added &3{@symbol}%formatMoney(arg-3)% {@msgcolor}to your account\" to arg-2 parsed as player\n                            if \"{@position}\" = \"after\":\n                                send \"{@prefix} - {@msgcolor}Added &3%formatMoney(arg-3)%{@symbol} {@msgcolor}to account of &3%arg-2%\"\n                                if {@consolemsg} is true:\n                                    send \"{@prefix} - &3%sender% {@msgcolor}added &3%formatMoney(arg-3)%{@symbol} {@msgcolor}to your account\" to arg-2 parsed as player\n                        else:\n                            send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco add/give <player> <amount>\" to sender\n     ", "               else:\n                        send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco add/give <player> <amount>\" to sender\n                if arg-1 is \"remove\" or \"take\":\n                    if arg-2 is set:\n                        if arg-3 is set:\n                            set {_uuid} to uuid of arg-2 parsed as offline player\n                            remove arg-3 from {eco::balance::%{_uuid}%}\n                            if \"{@position}\" = \"before\":\n                                send \"{@prefix} - {@msgcolor}Removed &3{@symbol}%formatMoney(arg-3)% {@msgcolor}from account of &3%arg-2%\"\n                                if {@consolemsg} is true:\n                                    send \"{@prefix} - &3%sender% {@msgcolor}removed &3{@symbol}%formatMoney(arg-3)% {@msgcolor}from your account\" to arg-2 parsed as player\n                            if \"{@position}\" = \"after\":\n                                send \"{@prefix} - {@msgcolor}Removed &3%formatMoney(arg-3)%{@symbol}{@msgcolor}from account of &3%arg-2%\"\n                                if {@consolemsg} is true:\n                                    send \"{@prefix} - &3%sender% {@msgcolor}removed &3%formatMoney(arg-3)%{@symbol} {@msgcolor}from your account\" to arg-2 parsed as player\n                        else:\n                            send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco remove/take <player> <amount>\" to sender\n                    else:\n                        send \"{@prefix} - {@msgcolor}Correct Usage: &3/eco remove/take <player> <amount>\" to sender\n                if arg-1 is \"convert\":\n                    if arg-2 is set:\n                        if arg-2 is \"essentials\":\n                            execute console command \"vault-convert essentialseconomy skeco\"\n                            send \"{@prefix} &7- {@msgcolor}Conversion from Essentials to SkEco has started, check console for progress\"\n                        else:\n                            send \"{@prefix} &7- {@msgcolor}Currently only essentials is set up for conversion.\"\n                            send \"{@msgcolor}If you are using another economy plugin, use &b/vault-convert <the name of your eco plugin> skeco\"\n                    if arg-2 is not set:\n                        send \"{@prefix} &7- {@msgcolor}Correct usage: &b/eco convert <economy plugin> {@msgcolor}Currently only &bessentials {@msgcolor}is supported\"\n                if arg-1 is \"version\":\n                    send a \"GET\" request to \"https://raw.githubusercontent.com/ShaneBeee/skripts/master/SkecoVersion\"\n                    set {_t} to last http response\n                    set {_j} to {_t}'s body\n                    set {_v::*} to {_j} split at \"|\"\n                    if \"{@version}\" is not equal to \"%{_v::2}%\":\n                        send \"&7[&a&lSkEco&7] - &2Running version: {@version}\" to player\n                        send \"&7[&a&lSkEco&7] - &cUpdate available, version: %{_v::2}%\" to sender\n                    else:\n                        send \"&7[&a&lSkEco&7] - &2Running current version: {@version}\" to sender\n                    delete {eco::indexes::*}\n            else:\n                send \"&7<=====>{@prefix}&7<=====>\" to player\n                send \"&3/eco set <player> <number> &7- {@msgcolor}Set the balance of player\"\n                send \"&3/eco add/give <player> <amount> &7- {@msgcolor}Add an amount to balance of player\"\n                send \"&3/eco remove/take <player> <amount> &7- {@msgcolor}Remove an amount from balance of player\"\n                send \"&3/eco reset <player> &7- {@msgcolor}Resets the players balance to the starting balace\"\n                send \"&3/eco convert <economy plugin> &7- {@msgcolor}Converts player balances from your previous economy plugin to SkEco - Currently only &bessentials{@msgcolor} is supported\"\n                send \"&3/eco version &7- {@msgcolor}Shows the version of SkEco you are running as well as current version available\"\n        else:\n            send \"{@prefix} {@permmessage}\"\n\n\ncommand /pay [<offline player>] [<number>]:\n    executable by: players\n    trigger:\n        if player has permission \"eco.pay\":\n            if arg-1 is set:\n                if arg-2 is set:\n                    if arg-1 is not player:\n                        if arg-2 <= {eco::balance::%uuid of player%}:\n                            remove arg-2 from {eco::balance::%uuid of player%}\n                            add arg-2 to {eco::balance::%uuid of arg-1%}\n                            if \"{@position}\" = \"before\":\n                                send \"{@prefix} - {@msgcolor}You sent &3{@symbol}%formatMoney(arg-2)% {@msgcolor}to &3%arg-1%\" to player\n                                send \"{@prefix} - &3%player% {@msgcolor}sent you &3{@symbol}%formatMoney(arg-2)%\" to arg-1\n                            if \"{@position}\" = \"after\":\n                                send \"{@prefix} - {@msgcolor}You sent &3%formatMoney(arg-2)%{@symbol} {@msgcolor}to &3%arg-1%\" to player\n                                send \"{@prefix} - &3%player% {@msgcolor}sent you &3%formatMoney(arg-2)%@symbol}\" to arg-1\n                        else:\n                            send \"{@prefix} - &cYou do not have enough money to send\"\n                    else:\n                        send \"{@prefix} &cYou can not send money to yourself\" to player\n                else:\n                    send \"{@prefix} &cCorrect usage /pay <player> <amount>\" to player\n            else:\n                send \"{@prefix} &cCorrect usage /pay <player> <amount>\" to player\n        else:\n            send \"{@prefix} {@permmessage}\" to player\n\n# -- BAL TOP & BAL TOP FUNCTIONS -- \n\nfunction formatMoney2(money: string) :: string:\n    set {_money} to \"%{_money}%\"\n    if {_money} contains \".\":\n        if length of {_money} - first index of \".\" in {_money} is 2:\n            return {_money}\n        return join {_money} and \"0\"\n    else:\n        return {_money}\nfunction regex(n: object) :: text:\n    if \"%{_n}%\" contains \".\":\n        set {_s::*} to split \"%{_n}%\" at \".\"\n        set {_n} to \"%a({_s::1})%.%last 2 characters of {_s::2}%\"\n        return \"%{_n}%\"\n    else:\n        set {_n} to a(\"%{_n}%\")\n        return \"%{_n} ? 0%\"\nfunction a(b: text) :: text:\n    if length of {_b} > 3:\n        return \"%a(first length of {_b} - 3 characters of {_b})%,%last 3 characters of {_b}%\"\n    return {_b}\n\nfunction sortHighestToLowest(indexes: strings, values: objects) :: strings:\n    set {_size} to size of {_values::*}\n    loop {_size} times:\n        loop {_size} - 1 times:\n            set {_value1} to {_values::%loop-number-2%}\n            set {_value2} to {_values::%loop-number-2 + 1%}\n            {_value1} is less than {_value2}\n            set {_index1} to {_indexes::%loop-number-2%}\n            set {_indexes::%loop-number-2%} to {_indexes::%loop-number-2 + 1%}\n            set {_values::%loop-number-2%} to {_value2}\n            set {_indexes::%loop-number-2 + 1%} to {_index1}\n            set {_values::%loop-number-2 + 1%} to {_value1}\n        remove 1 from {_size}\n    set {eco::indexes::*} to {_indexes::*}\n    return {eco::indexes::*}\n    wait 5 minute\n    delete {eco::indexes::*}\n    delete {eco::saved::balance::*}\n\nfunction baltop(p: sender, page: Number):\n    loop {eco::balance::*}:\n        set {_loop::%loop-index%} to loop-index\n    if {eco::indexes::*} is not set:\n        loop {eco::balance::*}:\n            set {eco::saved::balance::%loop-index%} to {eco::balance::%loop-index%}\n            set {_balance::%loop-index%} to {eco::balance::%loop-index%}\n        set {_playersSorted::*} to sortHighestToLowest({_loop::*}, {eco::balance::*})\n    else:\n        set {_playersSorted::*} to {eco::indexes::*}\n        loop {eco::balance::*}:\n            set {_balance::%loop-index%} to {eco::saved::balance::%loop-index%}\n    set {_maxPage} to 1 if size of {_playersSorted::*} is 0 else rounded up (size of {_playersSorted::*}/10)\n    if {_page} is greater than {_maxPage}:\n        send \"{@prefix} &cThat is not a valid page!\" to {_p}\n    else if {_page} is less than 1:\n        send \"{@prefix} &cThat is not a valid page!\" to {_p}\n    else:\n        send \"\" to {_p}\n        send \"{@prefix} - &l&nTop Balances\" to {_p}\n        send \"\" to {_p}\n \n        set {_calcPage} to {_page} - 1\n        set {_i} to 0\n        set {_pos} to 1\n        loop {_playersSorted::*}:\n            if {_i} is between {_calcPage} * 10 and (({_calcPage} + 1) * 10) - 1:\n                set {_bal} to regex({_balance::%loop-value%})\n                set {_bal2} to formatMoney2({_bal})\n                send \"&b##%{_pos}% &b%{eco::player::%loop-value%}% &8&l\u00bb &f$%{_bal2}%\" to {_p}\n                add 1 to {_pos}\n            add 1 to {_i}\n        send \"&7&o(( You are on page %{_page}% / %{_maxPage}% ))\" to {_p}\n\ncommand /baltop [<number=1>]:\n    aliases: balancetop, topbal, topbalance\n    trigger:\n        if sender has permission \"eco.baltop\":        \n            baltop(sender, arg-1)\n\n\n\n"), (Charset)StandardCharsets.UTF_8);
            if (Skript.methodExists(ScriptLoader.class, (String)"loadStructure", (Class[])new Class[]{InputStream.class, String.class})) {
                ScriptLoader.loadScripts((Config[])new Config[]{ScriptLoader.loadStructure((InputStream)inputStream, (String)"SkEco")});
            } else {
                File file = new File(System.getProperty("java.io.tmpdir"), "SkEco.sk");
                FileUtils.copyInputStreamToFile((InputStream)inputStream, (File)file);
                ScriptLoader.loadScripts((File[])new File[]{file});
                file.delete();
            }
            inputStream.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private void loadSkVault() {
        if (Bukkit.getPluginManager().getPlugin("SkVault") != null) {
            return;
        }
        try {
            File file = new File(System.getProperty("java.io.tmpdir"), "SkEco-SkVault.jar");
            FileUtils.copyInputStreamToFile((InputStream)this.getClass().getClassLoader().getResourceAsStream("SkEco-SkVault.jar"), (File)file);
            file.deleteOnExit();
            Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
            Field field = PluginDescriptionFile.class.getDeclaredField("name");
            field.setAccessible(true);
            field.set((Object)plugin.getDescription(), "SkEco-SkVault");
            Field field2 = PluginLogger.class.getDeclaredField("pluginName");
            field2.setAccessible(true);
            field2.set(plugin.getLogger(), "[SkEco] ");
            Bukkit.getPluginManager().enablePlugin(plugin);
            Field field3 = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
            field3.setAccessible(true);
            Field field4 = Field.class.getDeclaredField("modifiers");
            field4.setAccessible(true);
            field4.setInt(field3, field3.getModifiers() & -17);
            field3.set((Object)Bukkit.getPluginManager(), Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(plugin2 -> !plugin2.equals((Object)plugin)).collect(Collectors.toList()));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    private void loadReqn() {
        if (Bukkit.getPluginManager().getPlugin("Reqn") != null) {
            return;
        }
        try {
            File file = new File(System.getProperty("java.io.tmpdir"), "SkEco-Reqn.jar");
            FileUtils.copyInputStreamToFile((InputStream)this.getClass().getClassLoader().getResourceAsStream("SkEco-Reqn.jar"), (File)file);
            file.deleteOnExit();
            Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
            Field field = PluginDescriptionFile.class.getDeclaredField("name");
            field.setAccessible(true);
            field.set((Object)plugin.getDescription(), "SkEco-Reqn");
            Field field2 = PluginLogger.class.getDeclaredField("pluginName");
            field2.setAccessible(true);
            field2.set(plugin.getLogger(), "[SkEco] ");
            Bukkit.getPluginManager().enablePlugin(plugin);
            Field field3 = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
            field3.setAccessible(true);
            Field field4 = Field.class.getDeclaredField("modifiers");
            field4.setAccessible(true);
            field4.setInt(field3, field3.getModifiers() & -17);
            field3.set((Object)Bukkit.getPluginManager(), Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(plugin2 -> !plugin2.equals((Object)plugin)).collect(Collectors.toList()));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
