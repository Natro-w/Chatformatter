package ru.Natro.chatformatter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.platform.PlayerAdapter;

public class ChatFormatter extends PluginBase {

    private static ChatFormatter instance;
    private Config config;
    private String formatTemplate;
    private List<String> enabledWorlds;
    private PlayerAdapter<Player> adapter;
    private EventSubscription lpSubscription;
    private final Map<UUID, PlayerCache> cache = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfigData();
        getLogger().info("ChatFormatter enabling, format=\"" + formatTemplate + "\"");

        try {
            LuckPerms lp = LuckPermsProvider.get();
            adapter = lp.getPlayerAdapter(Player.class);
            lpSubscription = lp.getEventBus().subscribe(UserDataRecalculateEvent.class, event -> {
                UUID uuid = event.getUser().getUniqueId();
                Optional<Player> optPlayer = getServer().getPlayer(uuid);
                if (optPlayer.isPresent() && optPlayer.get().isOnline()) {
                    getServer().getScheduler().scheduleDelayedTask(this, () -> {
                        updatePlayer(optPlayer.get());
                    }, 1, false);
                }
            });
            getLogger().info("ChatFormatter: LuckPerms hooked OK");
        } catch (Exception e) {
            getLogger().warning("ChatFormatter: LuckPerms check failed: " + e.getMessage());
        }

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getServer().getCommandMap().register("chatformatter", new ReloadCommand());

        getLogger().info("ChatFormatter enabled.");
    }

    @Override
    public void onDisable() {
        cache.clear();
        if (lpSubscription != null) {
            lpSubscription.close();
        }
    }

    public void reloadConfigData() {
        config = new Config(new File(getDataFolder(), "config.yml"));
        formatTemplate = config.getString("format", "%1$s§r: %2$s");
        enabledWorlds = config.getStringList("enabled-worlds");
        cache.clear();
        if (adapter != null) {
            for (Player player : getServer().getOnlinePlayers().values()) {
                updatePlayer(player);
            }
        }
    }

    public void updatePlayer(Player player) {
        if (adapter == null) return;
        cache.remove(player.getUniqueId());
        try {
            CachedMetaData meta = adapter.getMetaData(player);
            PlayerCache pc = new PlayerCache(player, meta);
            cache.put(player.getUniqueId(), pc);
            player.setDisplayName(pc.displayName);
            player.setNameTag(pc.displayName);
        } catch (Exception e) {
            getLogger().warning("ChatFormatter: failed to update " + player.getName() + ": " + e.getClass().getSimpleName());
        }
    }

    public PlayerCache getCached(Player player) {
        return cache.get(player.getUniqueId());
    }

    public void invalidate(Player player) {
        cache.remove(player.getUniqueId());
    }

    public static ChatFormatter get() {
        return instance;
    }

    public String getFormatTemplate() {
        return formatTemplate;
    }

    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }

    public boolean isWorldEnabled(String worldName) {
        if (enabledWorlds.isEmpty()) return true;
        return enabledWorlds.contains(worldName);
    }

    public PlayerAdapter<Player> getAdapter() {
        return adapter;
    }

    public static class PlayerCache {
        public final String prefix;
        public final String suffix;
        public final String displayName;

        PlayerCache(Player player, CachedMetaData meta) {
            String p = meta.getPrefix();
            if (p == null || p.isEmpty()) {
                var prefixes = meta.getPrefixes();
                p = prefixes.isEmpty() ? null : prefixes.get(prefixes.lastKey());
            }
            this.prefix = p;
            String s = meta.getSuffix();
            if (s == null || s.isEmpty()) {
                var suffixes = meta.getSuffixes();
                s = suffixes.isEmpty() ? null : suffixes.get(suffixes.lastKey());
            }
            this.suffix = s;

            StringBuilder sb = new StringBuilder();
            if (prefix != null && !prefix.isEmpty()) {
                sb.append(TextFormat.colorize(prefix)).append("§r");
            }
            sb.append(player.getName());
            if (suffix != null && !suffix.isEmpty()) {
                sb.append("§r").append(TextFormat.colorize(suffix));
            }
            this.displayName = sb.toString();
        }
    }
}
