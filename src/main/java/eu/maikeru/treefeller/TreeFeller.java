package eu.maikeru.treefeller;

import eu.maikeru.treefeller.Listeners.breakListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TreeFeller extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Starting TreeFeller v1.0 SNAPSHOT");
        getServer().getPluginManager().registerEvents(new breakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
