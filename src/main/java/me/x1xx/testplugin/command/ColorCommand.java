package me.x1xx.testplugin.command;


import com.meteoritepvp.api.command.CommandClass;
import com.meteoritepvp.api.command.DefaultCommand;
import me.x1xx.testplugin.Pair;
import me.x1xx.testplugin.TestPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * The type Color command.
 */
@DefaultCommand
public class ColorCommand implements CommandClass {
    private final TestPlugin plugin;

    // In a real implementation, this would be stored somewhere else, and lazily loaded, and would have a manager class
    private final Map<UUID, Set<ItemStack>> playerColors = new HashMap<>();

    /**
     * Instantiates a new Color command.
     *
     * @param plugin the plugin
     */
    public ColorCommand(TestPlugin plugin) {
        this.plugin = plugin;

    }


    /**
     * Add a color command
     *
     * @param sender the sender
     * @param player the player
     * @param params the params
     */
    @Command(args = {"add"}, description = "add a color to your list", params = "@colors")
    public void add(CommandSender sender, Player player, String[] params) {
        // ensure that the sender is a player
        if (player == null) {
            // if it's not a player, send a basic message to the sender
            sender.sendMessage(c("&cYou must be a player to use this command"));
            return;
        }

        // get the color from the params and strip trailing and leading whitespace
        String color = params[0].trim();

        // get the material from the color

        Pair<ItemStack, String> colorItem = getColor(color);
        if (colorItem == null) {
            sender.sendMessage(c("&cInvalid color, options are: Black, White, Red, Orange, Yellow, \n Green, Blue, Purple, Pink, Brown, Gray, Light Gray, Cyan, Lime, Magenta, Light Blue"));
            return;
        }
        playerColors.computeIfAbsent(player.getUniqueId(), (k) -> new HashSet<>()).add(colorItem.getKey());

        sender.sendMessage(c("&aAdded color " + colorItem.getValue() + color + "&a to your list"));

    }

    /**
     * Gets a color from a string, utility method
     * @param color the color
     * @return the color
     */
    private Pair<ItemStack, String> getColor(String color) {
        color = color.toUpperCase().trim();

        switch (color) {
            case "BLACK":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), "&0");
            case "WHITE":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0), "&f");
            case "RED":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), "&c");
            case "ORANGE":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1), "&6");
            case "YELLOW":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4), "&e");
            case "GREEN":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13), "&2");
            case "BLUE":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11), "&9");
            case "PURPLE":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10), "&5");
            case "PINK":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6), "&d");
            case "BROWN":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 12), "&6");
            case "GRAY":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), "&8");
            case "LIGHT_GRAY":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8), "&7");
            case "CYAN":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9), "&3");
            case "LIME":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5), "&a");
            case "MAGENTA":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2), "&d");
            case "LIGHT_BLUE":
                return new Pair<>(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3), "&b");
            default:
                return null;
        }
    }

    /**
     * Remove color command
     *
     * @param sender the sender
     * @param player the player
     * @param params the params
     */
    @Command(args = {"remove"}, description = "remove a color from your list", params = "@colors")
    public void remove(CommandSender sender, Player player, String[] params) {
        if (player == null) {
            // if it's not a player, send a basic message to the sender
            sender.sendMessage(c("&cYou must be a player to use this command"));
            return;
        }

        // get the color from the params and strip trailing and leading whitespace
        String color = params[0].trim();

        // get the material from the color

        Pair<ItemStack, String> colorItem = getColor(color);
        if (colorItem == null) {
            sender.sendMessage(c("&cInvalid color, options are: Black, White, Red, Orange, Yellow, \n Green, Blue, Purple, Pink, Brown, Gray, Light Gray, Cyan, Lime, Magenta, Light Blue"));
            return;
        }
        Set<ItemStack> itemStacks = playerColors.get(player.getUniqueId());

        if (itemStacks == null || itemStacks.isEmpty()) {
            sender.sendMessage(c("&cYou don't have any colors to remove"));
            return;
        }

        itemStacks.remove(colorItem.getKey());

        sender.sendMessage(c("&aRemoved color " + colorItem.getValue() + color + "&a from your list"));
    }

    /**
     * Main command.
     *
     * @param sender the sender
     * @param player the player
     */
    @Command(description = "view your colors list")
    public void mainCommand(CommandSender sender, Player player) {
        // ensure that the sender is a player
        if (player == null) {
            // if it's not a player, send a basic message to the sender
            sender.sendMessage(c("&cYou must be a player to use this command"));
            // end command execution
            return;
        }

        // create a new instance inventory

        MeteoriteInventory inventory = new MeteoriteInventory(plugin, "Colors", 9, 6, true);

        // create a new instance of the BasicInventoryPage class, could cache it, but performance tradeoff is not worth it

        BasicInventoryPage page = new BasicInventory();

        // get the colors for the player

        Set<ItemStack> colors = getColors(player);

        // iterate through the colors, and add them to the inventory, up to 54 items

        for (int i = 0; i < colors.size() && i < 54; i++) {
            page.setItem(i, colors.get(i));
        }

        // update the page, and apply it to the inventory

        page.update();

        inventory.applyPage(page);

        page.setOnSlotClickListener(e -> e.getEvent().setCancelled(true));


        inventory.show(player);

    }


    /**
     * Gets colors from the map or provides an empty set. Utility method
     * @param player the player
     * @return the colors
     */

    private Set<ItemStack> getColors(Player player) {
        return playerColors.getOrDefault(player.getUniqueId(), new HashSet<>());
    }


    /**
     * Utility method to translate color codes
     * @param s the string
     * @return the translated string
     */
    private static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }


}
