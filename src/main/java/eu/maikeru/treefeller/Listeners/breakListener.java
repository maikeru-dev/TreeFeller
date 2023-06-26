package eu.maikeru.treefeller.Listeners;

import eu.maikeru.treefeller.Utils.CollectTreeInformation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;


public class breakListener implements Listener {
    @EventHandler
    public void onBlockBreak (BlockBreakEvent e) {
        Block block = e.getBlock(); // block
        Player player = e.getPlayer();
        Location initialLocation = block.getLocation();

        if (!block.getType().name().contains("_LOG")) return;
        if (!player.getInventory().getItemInMainHand().getType().name().contains("_AXE")) return;
        // Axe implements damageable interface!

        Damageable itemInHandMeta = (Damageable) player.getInventory().getItemInMainHand().getItemMeta();
        int damageOnAxe = itemInHandMeta.getDamage();
        // We broke a block that might belong to a tree!

        // Definition of a tree: A bunch of logs stacked on a dirt block, surrounded by persistent=false leaves.

        // Find out if the surrounding area contains logs surrounded by persistent=false leaves or air.
        // Definition of surrounding area: 3x3x1 * n tree height. (x,y,z | Note: y is up/down).

        CollectTreeInformation treeManager = new CollectTreeInformation(initialLocation);

        if (treeManager.isValid()) {
            Material material = e.getBlock().getType();

            Bukkit.getLogger().info(treeManager.getLogCount() + " ");
            itemInHandMeta.setDamage(damageOnAxe - treeManager.getLogCount());
            treeManager.removeTree();

            initialLocation.getWorld().dropItem(initialLocation,
                    new ItemStack(material, treeManager.getLogCount()));
        }

    }

}
