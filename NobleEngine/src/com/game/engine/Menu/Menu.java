package com.game.engine.Menu;

import java.util.ArrayList;

import me.libraryaddict.inventory.PageInventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.game.engine.GameEngine;
import com.game.engine.Game.Game;

public abstract class Menu implements Listener
{
	//Variables that we will be working with
	private PageInventory inventory;
	private String name;
	private Material material;
	private int pages;
	private ArrayList<ItemStack> displays = new ArrayList<>();
	private Game game = GameEngine.getCurrentGame();
	private boolean enabled = true;

	/**
	 * This is the super constructor for ever class that extends this menu.
	 * 
	 * @param name
	 *            (Name of the item in hand)
	 * @param pages
	 *            (Amount of pages in that particular menu)
	 * @param InteractableMaterial
	 *            (What item to interact with the open the menu)
	 */
	public Menu(String name, int pages, Material InteractableMaterial)
	{
		this.name = name;
		this.pages = pages;
		this.material = InteractableMaterial;
		GameEngine.Register(this);
	}

	// Abstract method that takes a player, item and game as parameter to construct the menu
	public abstract void InventoryConstruct(Player player, ItemStack item, Game game);

	// When the menu has been constructed, you can interact with the items within. This methods
	// takes care of this.
	public abstract void InventoryInteract(Player player, ItemStack item, InventoryAction action, Game game);

	/*
	 * This is an event that will be triggered when a player interacts with anything
	 * in other words it will be fired when a player left-click or right-click
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (enabled)
		{
			Player player = event.getPlayer();
			ItemStack item = event.getItem();
			Action action = event.getAction();
			
			// Here I limits the actions to a right click. So we will only continue if right click
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				// Some null checks to prevent nullpointers
				if (item == null) return;
				if (player.getItemInHand() == null) return;
				if (!item.hasItemMeta()) return;
				if (item.getItemMeta().getDisplayName().contains(name))
				{
					displays.clear();
					InventoryConstruct(player, item, game);
					inventory = new PageInventory(player); // Creates the framework
					inventory.setTitle(this.name);
					inventory.setPage(pages);
					inventory.setPages(this.displays); // Sets the items in the menu
					inventory.openInventory(); // Opens the menu
					event.setCancelled(true);
				}
			}
		}
	}

	/*
	 * This is an event that fires when a player closes a menu
	 * This will clear and destroy the menu so it's ready to be opened again
	 */
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (enabled)
		{
			Inventory currentInventory = event.getInventory(); // Gets the open menu
			if (currentInventory.getName().contains(name)) // Check if the menu is a menu of mine
			{
				displays.clear(); // Clears items
				currentInventory.clear(); // Clear currentInventory.
			}
		}
	}

	/*
	 * This event is fired when a player clicks an item in a menu.
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (enabled)
		{
			try
			{
				Player player = (Player) event.getWhoClicked(); // Get the player that clicked
				Inventory currentInventory = event.getInventory(); // Get the inventory that's clicked
				ItemStack clicked = event.getCurrentItem(); // Get item that's clicked
				
				//Null checks to prevent nullpointers
				if (currentInventory == null)
				{
					return;
				}
				if (clicked == null)
				{
					return;
				}
				if (clicked.getType() == Material.AIR)
				{
					return;
				}
				// Check if the item clicked is equal to an item in a menu of mine.
				if (currentInventory.getName().equals(name))
				{
					for (ItemStack item : inventory.getItems())
					{
						if (clicked.getType() == item.getType() && clicked.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
						{
							InventoryInteract(player, clicked, event.getAction(), game); // execute the abstract method
						}
					}
				}
			}
			catch (Exception e) // catch any exceptions... But I don't count on any to happen
			{
				//e.printStackTrace();
			}
		}
	}

	// Adds in item to the menu
	public void addItemDisplay(ItemStack item)
	{
		displays.add(item);
	}

	// Adds multiple items to the menu
	public void addItemviaArray(ItemStack[] itemtoAdd)
	{
		for (ItemStack items : itemtoAdd)
		{
			displays.add(items);
		}
	}

	/*
	 * The rest below is just some getters and setters to the menues.
	 */
	public String getName()
	{
		return name;
	}

	public Material getMaterial()
	{
		return material;
	}
	
	// Unregisters the events for the menu if needed.
	public void unregisterMenu()
	{
		HandlerList.unregisterAll(this);
	}
}
