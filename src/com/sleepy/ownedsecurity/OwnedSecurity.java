package com.sleepy.ownedsecurity;

import com.sleepy.ownedsecurity.events.PlayerLoginEvent;
import java.io.File;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class OwnedSecurity extends JavaPlugin implements Listener {
    PlayerLoginEvent playerloginEvent = new PlayerLoginEvent(this);
    public final File configFile = new File(getDataFolder().getPath() + "/config.yml");
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(playerloginEvent, this);
        if (!new File(getDataFolder().getPath() + "/config").exists()) {
            saveDefaultConfig();
            reloadConfig();
        }
    }
}
