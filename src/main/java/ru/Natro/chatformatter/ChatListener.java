package ru.Natro.chatformatter;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;

public class ChatListener implements Listener {

    private final ChatFormatter plugin;

    public ChatListener(ChatFormatter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getAdapter() == null) return;
        plugin.getServer().getScheduler().scheduleDelayedTask(plugin, () -> {
            plugin.updatePlayer(player);
        }, 20, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.invalidate(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(PlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (plugin.getAdapter() == null) return;

        Player player = event.getPlayer();
        if (player.hasPermission("chatformat.bypass")) return;

        if (player.getLevel() != null && !plugin.isWorldEnabled(player.getLevel().getName())) return;

        event.setFormat(TextFormat.colorize(plugin.getFormatTemplate()));
    }
}
