package com.game.engine.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import com.game.engine.GameEngine;

public class WorldUtil
{
	public static File getWorldFile(String worldName)
	{
		return new File(Bukkit.getWorldContainer(), worldName);
	}

	public static boolean isWorldLoaded(String worldName)
	{
		return Bukkit.getServer().getWorld(worldName) != null;
	}

	public static World getWorld(String worldName)
	{
		return Bukkit.getServer().getWorld(worldName);
	}

	public static boolean hasWorldFile(String mapName)
	{
		return new File(Bukkit.getWorldContainer(), mapName).exists();
	}

	public static void deleteWorld(final String mapName)
	{
		deleteWorld(mapName, null);
	}

	public static void deleteWorld(final String mapName, final Runnable runAfter)
	{
		// final Plugin plugin = ExGame.getPlugin();
		final File file;
		World world = Bukkit.getServer().getWorld(mapName);
		if (world == null)
		{
			System.out.print("The world " + mapName + " is not loaded!");
			file = new File(Bukkit.getServer().getWorldContainer(), mapName);
			if (!file.exists())
			{
				System.out.print("Could not find file at: " + file.getPath() + "  Something can't be deleted if its not there.");
				runAfter.run();
				return;
			}
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					try
					{
						deleteFile(file);
						System.out.print("Deleted the files found at: " + file.getPath() + "  though.");
						if (runAfter != null) new BukkitRunnable()
						{
							@Override
							public void run()
							{
								runAfter.run();
							}
						}.runTask(GameEngine.GetPlugin());
					}
					catch (IOException e)
					{
						e.printStackTrace();
						return;
					}
				}
			}.runTaskAsynchronously(GameEngine.GetPlugin());
			return;
		}
		else
		{
			file = world.getWorldFolder();
		}
		if (Bukkit.getServer().unloadWorld(world, true))
		{
			System.out.print("Successfully unloaded " + world.getName());
		}
		else
		{
			System.out.print("COULD NOT UNLOAD " + world.getName());
			return;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				try
				{
					deleteFile(file);
					System.out.print("Successfully deleted " + mapName);
					if (runAfter != null) new BukkitRunnable()
					{
						@Override
						public void run()
						{
							runAfter.run();
						}
					}.runTask(GameEngine.GetPlugin());
					return;
				}
				catch (Exception e)
				{
					System.out.print("COULD NOT DELETE " + mapName + "!");
					e.printStackTrace();
				}
			}
		}.runTaskLaterAsynchronously(GameEngine.GetPlugin(), 20);
		return;
	}

	public static World createWorld(File scrFile, String worldName)
	{
		return createWorld(scrFile, worldName, false);
	}

	public static World createWorld(File scrFile, String worldName, boolean quiet)
	{
		if (!scrFile.exists())
		{
			if (!quiet) System.out.print("There is no file at: " + scrFile.getPath() + " So it can not be loaded into the server");
			return null;
		}
		else if (!new File(scrFile, "level.dat").exists())
		{
			if (!quiet) System.out.print("The File at " + scrFile.getPath() + " is not a world file!");
			return null;
		}
		File destFile = new File(Bukkit.getServer().getWorldContainer(), worldName);
		if (destFile.exists())
		{
			World currentWorld = WorldUtil.getWorld(worldName);
			if (currentWorld != null)
			{
				if (!quiet) System.out.print("The world " + worldName + " already exists!");
				return null;
			}
			if (!quiet) System.out.print("We found a file at: " + destFile.getPath() + ". So we sill try to delete it!");
			try
			{
				deleteFile(destFile);
			}
			catch (IOException e)
			{
				if (!quiet) System.out.print("Could not delete the file at: " + destFile.getPath() + ". Please do so yourself!");
				e.printStackTrace();
			}
		}
		if (copyWorld(scrFile, destFile))
		{
			World w = Bukkit.getServer().createWorld(new WorldCreator(worldName));
			return w;
		}
		return null;
	}

	public static boolean copyWorld(File source, File target)
	{
		try
		{
			ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
			if (!ignore.contains(source.getName()))
			{
				if (source.isDirectory())
				{
					if (!target.exists()) target.mkdirs();
					String files[] = source.list();
					for (String file : files)
					{
						File srcFile = new File(source, file);
						File destFile = new File(target, file);
						copyWorld(srcFile, destFile);
					}
				}
				else
				{
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0)
						out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		}
		catch (IOException e)
		{
			Bukkit.broadcastMessage("I failed to copy a world");
			return false;
		}
		return true;
	}

	public static void deleteFile(File file) throws IOException
	{
		if (!file.exists()) return;
		if (file.isDirectory())
		{
			for (File c : file.listFiles())
				deleteFile(c);
		}
		if (!file.delete()) throw new FileNotFoundException("Failed to delete file: " + file);
	}
}
