package com.game.engine.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.game.engine.GameEngine;
import com.game.engine.Chat.Chat;
import com.game.engine.Util.ItemStackBuilder;
import com.game.engine.Util.UtilPlayer;

public class Kit
{
	private boolean isAllLeather = false;
	private String name;
	private String[] description;
	private Material displayItem;
	private ItemStack helmet;
	private ItemStack chest;
	private ItemStack leggins;
	private ItemStack boots;
	private List<ItemStack> contents = new ArrayList<>();
	private HashMap<ItemStack, Integer> specificContent = new HashMap<ItemStack, Integer>();

	public Kit(String kitName, String[] kitDescription, Material displayItem)
	{
		name = kitName;
		description = kitDescription;
		this.displayItem = displayItem;
		GameEngine.Debug("Creating " + name + " kit.");
	}

	public void setArmor(ItemStack helmet, ItemStack chest, ItemStack leggins, ItemStack boots, boolean isAllLeather)
	{
		this.helmet = helmet;
		this.chest = chest;
		this.leggins = leggins;
		this.boots = boots;
		this.isAllLeather = isAllLeather;
	}

	public void AddItem(ItemStack itemStack)
	{
		contents.add(itemStack);
	}

	public void addSpecialItem(ItemStack item, int slot)
	{
		specificContent.put(item, slot);
	}

	public void RemoveItem(ItemStack itemStack)
	{
		if (contents.contains(itemStack)) contents.remove(itemStack);
	}

	public void Equip(Player player)
	{
		UtilPlayer.reset(player);
		player.sendMessage(Chat.format("Kit", "You have equipped the &a" + name + "&f."));
		for (ItemStack item : specificContent.keySet())
		{
			player.getInventory().setItem(specificContent.get(item), item);
		}
		for (ItemStack itemStack : contents)
		{
			player.getInventory().addItem(itemStack);
		}
		if (isAllLeather = true)
		{
			try
			{
			LeatherArmorMeta im1 = (LeatherArmorMeta) helmet.getItemMeta();
			im1.setColor(GameEngine.getCurrentGame().GetTeam(player).GetLeatherColor());
			helmet.setItemMeta(im1);
			LeatherArmorMeta im2 = (LeatherArmorMeta) chest.getItemMeta();
			im2.setColor(GameEngine.getCurrentGame().GetTeam(player).GetLeatherColor());
			chest.setItemMeta(im2);
			LeatherArmorMeta im3 = (LeatherArmorMeta) leggins.getItemMeta();
			im3.setColor(GameEngine.getCurrentGame().GetTeam(player).GetLeatherColor());
			leggins.setItemMeta(im3);
			LeatherArmorMeta im4 = (LeatherArmorMeta) boots.getItemMeta();
			im4.setColor(GameEngine.getCurrentGame().GetTeam(player).GetLeatherColor());
			boots.setItemMeta(im4);
			}
			catch(NullPointerException e)
			{
				GameEngine.Debug("Caught kit item meta exception. Are you sure you added armor for " + GetName() + "?");
			}
		}
		try
		{
			player.getInventory().setHelmet(helmet);
			player.getInventory().setChestplate(chest);
			player.getInventory().setLeggings(leggins);
			player.getInventory().setBoots(boots);
		}
		catch (NullPointerException e)
		{
			GameEngine.Debug("Caught kit set armor exception");
		}
	}

	public String GetName()
	{
		return name;
	}

	public String[] GetDescription()
	{
		return description;
	}

	public List<ItemStack> GetContents()
	{
		return contents;
	}

	public ItemStack getDisplayItem()
	{
		return new ItemStackBuilder(displayItem, 1, name, description, ChatColor.GREEN).buildItem();
	}
}
