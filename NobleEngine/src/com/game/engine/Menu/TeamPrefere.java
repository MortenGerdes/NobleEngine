package com.game.engine.Menu;

import me.libraryaddict.inventory.PageInventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import com.game.engine.Game.Game;
import com.game.engine.Game.Team;
import com.game.engine.Util.ItemStackBuilder;

public class TeamPrefere extends Menu
{
	public Material displayMaterial;
	public String displayName;
	PageInventory inv;

	// Constructor for the menu to set displayitem(what item to click)
	TeamPrefere(Material displayItem, String name)
	{
		super("TeamPrefere", 1, displayItem);

		this.displayMaterial = displayItem;
		this.displayName = name;
	}

	// First abstact method from the Parent class that adds the items to the menu
	@Override
	public void InventoryConstruct(Player player, ItemStack item, Game game)
	{
		for (Team teams : game.GetTeams())
		{
			addItemDisplay(teams.numberedTeamColorWool());
		}
	}

	// Second abstact method from the Parent class that handles what happens when an item is clicked
	@Override
	public void InventoryInteract(Player player, ItemStack item, InventoryAction action, Game game)
	{
		game.AdvancedSelectTeam(player, item.getItemMeta().getDisplayName());
	}

	public String getDisplayName()
	{
		// TODO Auto-generated method stub
		return displayName;
	}

	public Material getDisplayMaterial()
	{
		// TODO Auto-generated method stub
		return displayMaterial;
	}

	public PageInventory getInventory()
	{
		// TODO Auto-generated method stub
		return inv;
	}

}
