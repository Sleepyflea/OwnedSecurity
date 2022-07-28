package com.sleepy.ownedsecurity.events;

import com.sleepy.ownedsecurity.OwnedSecurity;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerLoginEvent implements Listener {

    private final OwnedSecurity pl;
    public PlayerLoginEvent(OwnedSecurity plugin) {
        this.pl = plugin;
    }

    //Security Handler
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        String joinedName = event.getName();
        InetAddress Ip = event.getAddress();
        UUID joinedUuid = event.getUniqueId();


        ConfigurationSection players = pl.getConfig().getConfigurationSection("Players");

        Set<String> playerKeys = players.getKeys(false);

        playerKeys.forEach(key -> {

            String staffName = pl.getConfig().getString("Players." + key + ".Name");
            UUID staffUUID = UUID.fromString(pl.getConfig().getString("Players." + key + ".UUID"));
            String staffIp = pl.getConfig().getString("Players." + key + ".Ip");

            String response = checkIfSecure(joinedName, joinedUuid, Ip, staffName, staffUUID, staffIp);

            if(response.equalsIgnoreCase("[VALID] Player Ip Authenticated Against UUID") || response.equalsIgnoreCase("[VALID] Player Ip Authenticated Against Username")) {
                event.allow();
                Bukkit.getLogger().log(Level.INFO, response);
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, response);
            }

        });

    }

    private String checkIfSecure(String joinedName, UUID joinedUUID, InetAddress joinedIP, String staffName, UUID staffUUID, String staffIP) {

        //Checks for fake name joins
        if(!joinedName.equalsIgnoreCase(staffName) && joinedUUID.equals(staffUUID)) {

            return "[INVALID USERNAME] ACTIVITY NOT NORMAL FOR USERNAME " + joinedName + ".";
            //Checks for fake uuid joins

        } else if(!joinedUUID.equals(staffUUID) && joinedName.equalsIgnoreCase(staffName)) {

            return "[INVALID UUID] ACTIVITY NOT NORMAL FOR UUID " + staffUUID + ".";
        }

        //Checks for correct ip against uuid on login
        if (joinedIP.toString().equalsIgnoreCase("/" + staffIP) && joinedUUID.equals(staffUUID)) {

            return "[VALID] Player Ip Authenticated Against UUID";
            //Checks for fake IP's against UUID's

        } else if(!joinedIP.toString().equalsIgnoreCase("/" + staffIP) && joinedUUID.equals(staffUUID)) {

            return "!![INVALID IP] ACTIVITY NOT NORMAL. " + joinedIP.toString() + " WAS THE IP DETECTED!!";
            //Checks for correct ip against name on login

        }

        if (joinedIP.toString().equalsIgnoreCase("/" + staffIP) && joinedName.equalsIgnoreCase(staffName)) {

            return "[VALID] Player Ip Authenticated Against Username";
            //Checks for fake IP's against Name's

        } else if (!joinedIP.toString().equalsIgnoreCase("/" + staffIP) && joinedName.equalsIgnoreCase(staffName)) {

            return "!![INVALID IP] ACTIVITY NOT NORMAL. "  + joinedIP.toString() +  " WAS THE IP DETECTED!!";

        }

        return "ok";
    }
}