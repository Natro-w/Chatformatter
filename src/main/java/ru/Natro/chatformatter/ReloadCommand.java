package ru.Natro.chatformatter;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("chatformatter", "Reload ChatFormatter configuration");
        setAliases(new String[]{"cfreload"});
        setPermission("chatformatter.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("chatformatter.reload")) {
            sender.sendMessage("§cYou don't have permission to reload ChatFormatter");
            return false;
        }
        ChatFormatter plugin = ChatFormatter.get();
        plugin.reloadConfigData();
        sender.sendMessage("§aChatFormatter configuration reloaded");
        plugin.getLogger().info("Configuration reloaded by " + sender.getName());
        return true;
    }
}
