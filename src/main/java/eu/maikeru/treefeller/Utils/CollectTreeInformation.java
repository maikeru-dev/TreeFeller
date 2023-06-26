package eu.maikeru.treefeller.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollectTreeInformation {
    ArrayList<Location> logLocations = new ArrayList<>();
    Location aboveLocation;
    Material initialBlockType;
    private short logCount = 0;
    private short leavesCount = 0;
    // Definition of a tree: A bunch of logs stacked on a dirt block, surrounded by persistent=false leaves.

    // Find out if the surrounding area contains logs surrounded by persistent=false leaves or air.
    // Definition of surrounding area: 3x3x1 * n tree height. (x,y,z | Note: y is up/down).

    private boolean branching; // TODO: Setup whether tree is branching or not for fall animation purpose

    public CollectTreeInformation(Location initialLocation) {
        if (isTreeOnDirt(initialLocation)) {
            logLocations.add(initialLocation);
            aboveLocation = GetLocationAbove(initialLocation);
            initialBlockType = initialLocation.getBlock().getType();

            if (aboveLocation.getBlock().getType() == initialBlockType || logCount < logLocations.size()) {
                processInformation();
            }
        }
    }
    private boolean isTreeOnDirt(Location initialLocation) {
        if (!GetLocationAbove(initialLocation, -1).getBlock().getType().equals(Material.DIRT)) {
            // We figured out that the block here is actually a log, but we don't know if the block below it is a dirt block.
            // Bukkit.getLogger().info("Hey you broke a log in the middle, weirdo!");
            short depth = -2;
            boolean belowUnequal = !GetLocationAbove(initialLocation, depth).getBlock().getType().equals(initialBlockType);
            while (!belowUnequal) {
                depth--;
                belowUnequal = !GetLocationAbove(initialLocation, depth).getBlock().getType().equals(initialBlockType);
            }
            if (!GetLocationAbove(initialLocation, depth).getBlock().getType().equals(Material.DIRT)) {
                return false;
            }
            // Bukkit.getLogger().info("Found dirt block at Depth: " + depth);
        }
        return true;
    }
    public boolean isValid() {
        return leavesCount > 8;
    }
    public int getLogCount() {
        return logCount;
    }
    public void removeTree() {
        for (Location log : logLocations) {
            // log.getBlock().setType(Material.CHERRY_FENCE); // Excellent debug choice.
            log.getBlock().setType(Material.AIR);
        }
    }
    private void processInformation() {
        // "This looks a bit convoluted."
        // This method is essentially a while loop disguised as a recursive function.
        /* Description: There are two variables for length, one is called logCount and one is the length field of logLocations.
        *               This is important to note, because the logCount is only incremented once per loop while the length of logLocations
        *               may increase multiple times per loop.
        *               Now, each time the loop runs, it runs a check on the surrounding blocks pivoted on the current logLocation using collectAt(x,y,z).
        *               collectAt(x,y,z) simply finds a log, adds it to the logLocations if it's new and increments leavesCount each time it finds a
        *               non-persistent leave block (we use that later to validate whether this block we found is a tree).
        *               Once collectAt(x,y,z) finishes looping, we check whether the block above the center is also a log, and add it if it hasn't been detected
        *               before, as it can be detected if we find that the tree branches out upwards.
        *               Finally, we check whether the loop conditions are finished, if not: continue looping the function.
        *               */
        for (byte x = -1; x <= 1; x++) {
            for (byte z = -1; z <= 1; z++){
                if (x == z & x == 0) continue;
                for (byte y = 0; y <= 1; y++) {
                    collectAt(x,y,z);
                }
            }
        }

        aboveLocation = GetLocationAbove(logLocations.get(logCount));
        logCount++;

        if (aboveLocation.getBlock().getType() == initialBlockType && !logLocations.contains(aboveLocation)) {
            logLocations.add(aboveLocation);
            // bug located (duplication) :: Fixed by appending check whether we are at the last log to conditions.
            // ^^ fix caused another bug (sometimes did not check above center log) :: Fixed by checking if the above location already is in the array
        }

        if (aboveLocation.getBlock().getType() == initialBlockType || logCount < logLocations.size()) {
            processInformation();
        }

    }
    private void collectAt(byte x, byte y, byte z) {
        Block newBlock = GetBlockAtNew(logLocations.get(logCount) , x, y, z);
        if (newBlock.getType().name().contains("_LEAVES")) {
            if (!((Leaves) newBlock.getBlockData()).isPersistent()) {
                leavesCount++;
            }
        }else if (newBlock.getType().equals(initialBlockType)) {
            if (!logLocations.contains(newBlock.getLocation())) {
                branching = true;
                logLocations.add(newBlock.getLocation());
            }

        }

    }
    private @NotNull Block GetBlockAtNew(Location location, byte x, byte y, byte z) {
        return new Location(location.getWorld(), location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z).getBlock();
    }
    @Contract("_ -> new")
    private @NotNull Location GetLocationAbove(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
    }
    @Contract("_, _ -> new")
    private @NotNull Location GetLocationAbove(Location location, int amount) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + amount, location.getBlockZ());
    }

}
