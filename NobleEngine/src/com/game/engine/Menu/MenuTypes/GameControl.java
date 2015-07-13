package com.game.engine.Menu.MenuTypes;

import me.libraryaddict.inventory.PageInventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Chat.Chat;
import com.game.engine.Game.GameManagement.Game;
import com.game.engine.Game.GameManagement.GameState;
import com.game.engine.Menu.Menu;
import com.game.engine.Util.ItemStackBuilder;

public class GameControl extends Menu
{

	public Material displayMaterial;
	public String displayName;
	public PageInventory inv;

	GameControl(Material displayItem, String displayName)
	{
		super("GameControl", 1, displayItem);

		this.displayMaterial = displayItem;
		this.displayName = displayName;
	}

	@Override
	public void InventoryConstruct(Player player, ItemStack item, Game game)
	{
		{
			addItemDisplay(new ItemStackBuilder(Material.DIAMOND_SWORD, 1, "Start Game", ChatColor.GREEN).buildItem());
			addItemDisplay(new ItemStackBuilder(Material.AIR, null, null).buildItem());
			addItemDisplay(new ItemStackBuilder(Material.TNT, 1, "End Game", ChatColor.RED).buildItem());
		}

	}

	@Override
	public void InventoryInteract(Player player, ItemStack item, InventoryAction action, Game game)
	{
		if (item.getItemMeta().getDisplayName().contains("Start Game") && item.getType().equals(Material.DIAMOND_SWORD))
		{
			if (game.getState() == GameState.WAITING)
			{
				game.prepare();
				Bukkit.broadcastMessage(Chat.format("SparkEngine", player.getName() + " has started the game!"));
			}
			else
			{
				player.sendMessage(Chat.format("SparkEngine", "The game has not ended yet!"));
			}
		}

		else if ((item.getItemMeta().getDisplayName().contains("End Game") && item.getType().equals(Material.TNT)))
		{
			if (game.getState() == GameState.STARTED)
			{
				game.stop();
				Bukkit.broadcastMessage(Chat.format("SparkEngine", player.getName() + " has stopped the game!"));
			}
			else
			{
				player.sendMessage(Chat.format("SparkEngine", "The game has not started yet!"));
			}
		}
	}

	public String getDisplayName()
	{
		// TODO Auto-generated method stub
		return displayName;
	}

	public Material getDisplayMaterial()
	{
		// TODO Auto-generated method stub
		return getDisplayMaterial();
	}

	public PageInventory getInventory()
	{
		return inv;
	}
}
