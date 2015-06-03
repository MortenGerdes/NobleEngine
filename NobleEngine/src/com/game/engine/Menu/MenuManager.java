package com.game.engine.Menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.game.engine.GameEngine;
import com.game.engine.Util.ItemStackBuilder;

public class MenuManager
{
	private static MenuManager instance = null;

	// private HashMap<Material, String> MenuInfo = new HashMap<Material, String>();

	// Private constructor for the purpose of singleton pattern
	private MenuManager()
	{
		
	}
	
	//Again... singleton :)
	public static MenuManager getInstance()
	{
		if (instance == null)
		{
			instance = new MenuManager();
			if(!menuBag.isEmpty())
			{
				for(Menu menu: getMenuBag())
				{
					menu.unregisterMenu();
				}
				menuBag.clear();
			}
			
			// Adds the menues created with the "Menu" parent class so they work.
			AddMenu(new TeamPrefere(Material.REDSTONE, "Team Selector"));
			AddMenu(new KitSelector(Material.BUCKET, "Kit Selector"));

			addMenuItemsForHotToArray();

		}
		return instance;
	}

	private static List<Menu> menuBag = new ArrayList<>();
	private static ArrayList<ItemStack> menuItems = new ArrayList<>();

	public static void addMenuItemsForHotToArray()
	{
		for (Menu menues : getMenuBag())
		{
			ItemStack item = new ItemStackBuilder(menues.getMaterial(), 1, menues.getName(), ChatColor.GRAY).buildItem();
			if (!menuItems.contains(item))
			{
				menuItems.add(item);	
			}
			
		}
	}

	/**
	 * Adds a new page inventory to the list.
	 * 
	 * @param inventory The inventory to store.
	 */
	public static void AddMenu(Menu inventory)
	{
		if (!menuBag.contains(inventory))
		{
			menuBag.add(inventory);
			GameEngine.Debug("Adding " + inventory.getName() + ".");
		}
	}

	/**
	 * Removes a page inventory from the list.
	 * 
	 * @param inventory The inventory to remove.
	 */
	public static void RemoveMenu(Menu inventory)
	{
		if (menuBag.contains(inventory))
		{
			menuBag.remove(inventory);
			GameEngine.Debug("Removing " + inventory.getName() + ".");
		}
	}

	/**
	 * Retrieves an inventory page from the list.
	 * 
	 * @param name The name of the inventory.
	 * @return PageInventory
	 */
	public static Menu GetInventory(String name)
	{
		for (Menu inv : menuBag)
		{
			if (inv.getName().contains(name))
			{
				GameEngine.Debug("Retrieving results: " + inv.getName() + " from the page bag.");
				return inv;
			}
		}

		return null;
	}

	public static void ClearMenus()
	{
		menuBag.clear();
		GameEngine.Debug("Removing all inventory pages...");
	}

	/**
	 * Retrieves all the registered inventories from the bag.
	 * @return List<PageInventory>

	 */
	public static List<Menu> getMenuBag()
	{
		return menuBag;
	}

	public ArrayList<ItemStack> getMenuItems()
	{
		return menuItems;
	}
	
	public void destroyInstance()
	{
		menuItems.clear();
		instance = null;
	}
}
