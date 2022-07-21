package com.sleepy.ownedsecurity.events;

import com.sleepy.ownedsecurity.OwnedSecurity;
import java.net.InetAddress;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerLoginEvent implements Listener {

    private final OwnedSecurity pl;
    public PlayerLoginEvent(OwnedSecurity plugin) {
        this.pl = plugin;
    }

    //Security Handler
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        InetAddress Ip = event.getAddress();
        UUID joinedUuid = event.getUniqueId();
        UUID targetUuid = UUID.fromString(pl.getConfig().getString("UUID"));
        //Checks for fake name joins
        if (joinedUuid.equals(targetUuid) && !event.getName().equalsIgnoreCase(pl.getConfig().getString("Player"))) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Malformed Username!");
            Bukkit.getLogger().log(Level.SEVERE, "[INVALID USERNAME] ACTIVITY NOT NORMAL FOR USERNAME {0}.", event.getName());
            return;
            //Checks for fake uuid joins
        } else if (!joinedUuid.equals(targetUuid) && event.getName().equalsIgnoreCase(pl.getConfig().getString("Player"))) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Malformed UUID!");
            Bukkit.getLogger().log(Level.SEVERE, "[INVALID UUID] ACTIVITY NOT NORMAL FOR UUID {0}.", event.getName());
            return;
        }
        //Checks for correct ip against uuid on login
        if (Ip.toString().equalsIgnoreCase("/" + pl.getConfig().getString("Ip"))
                && joinedUuid.equals(targetUuid)) {
            event.allow();
            Bukkit.getLogger().log(Level.INFO, "[VALID] Player Ip Authenticated Against UUID");
            return;
            //Checks for fake IP's against UUID's
        } else if (!Ip.toString().equalsIgnoreCase("/" + pl.getConfig().getString("Ip"))
                && joinedUuid.equals(targetUuid)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Malformed Ip Address");
            Bukkit.getLogger().log(Level.SEVERE, "!![INVALID IP] ACTIVITY NOT NORMAL. {0} WAS THE IP DETECTED!!", Ip);
            return;
            //Checks for correct ip against name on login
        }
        if (Ip.toString().equalsIgnoreCase("/" + pl.getConfig().getString("Ip"))
                && event.getName().equalsIgnoreCase(pl.getConfig().getString("Player"))) {
            event.allow();
            Bukkit.getLogger().log(Level.INFO, "[VALID] Player Ip Authenticated Against Username");
            //Checks for fake IP's against Name's
        } else if (!Ip.toString().equalsIgnoreCase("/" + pl.getConfig().getString("Ip"))
                && event.getName().equalsIgnoreCase(pl.getConfig().getString("Player"))) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Malformed Ip Address");
            Bukkit.getLogger().log(Level.SEVERE, "!![INVALID IP] ACTIVITY NOT NORMAL. {0} WAS THE IP DETECTED!!", Ip);
        }
    }
}