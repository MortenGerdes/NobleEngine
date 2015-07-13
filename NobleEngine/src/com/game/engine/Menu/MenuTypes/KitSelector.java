package com.game.engine.Menu.MenuTypes;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Chat.Announcer;
import com.game.engine.Game.Kit;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Menu.Menu;
import com.game.engine.Util.ItemStackBuilder;

public class KitSelector extends Menu
{

	// Constructor for the menu to set displayitem(what item to click)
	public KitSelector(Material displayItem, String displayName)
	{
		super(displayName, 1, displayItem);
	}

	// First abstact method from the Parent class that adds the items to the menu
	@Override
	public void InventoryConstruct(Player player, ItemStack item, Game game)
	{
		for (Kit kits : game.getKits())
		{
			addItemDisplay(kits.getDisplayItem());
			addItemDisplay(new ItemStackBuilder(Material.AIR, null, null).buildItem());
		}

	}

	// Second abstact method from the Parent class that handles what happens when an item is clicked
	@Override
	public void InventoryInteract(Player player, ItemStack item, InventoryAction action, Game game)
	{
		for (Kit kits : game.getKits())
		{
			if (item.equals(kits.getDisplayItem()))
			{
				game.setKit(player, kits);
				Announcer.describeKit(player, kits);
			}
		}
		// Plays a sound and closes the menu
		player.playSound(player.getLocation(), Sound.LAVA_POP, 1, 1);
		player.closeInventory();
	}
}
