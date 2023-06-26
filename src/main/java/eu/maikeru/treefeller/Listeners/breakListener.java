package eu.maikeru.treefeller.Listeners;

import eu.maikeru.treefeller.Utils.CollectTreeInformation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        // Axe implements Damageable interface!
        ItemStack itemStackInHand = player.getInventory().getItemInMainHand();
        Damageable itemStackInHandMeta = (Damageable) itemStackInHand.getItemMeta();

        // We broke a block that might belong to a tree!


        CollectTreeInformation treeManager = new CollectTreeInformation(initialLocation);

        if (treeManager.isValid()) {
            Material material = e.getBlock().getType();

            if (!player.getGameMode().equals(GameMode.CREATIVE)){
                int damageOnAxe = itemStackInHandMeta.getDamage();
                itemStackInHandMeta.setDamage(damageOnAxe + treeManager.getLogCount());
                itemStackInHand.setItemMeta(itemStackInHandMeta);
            }
            treeManager.removeTree();

            initialLocation.getWorld().dropItem(initialLocation,
                    new ItemStack(material, treeManager.getLogCount()));
        }

    }

}
