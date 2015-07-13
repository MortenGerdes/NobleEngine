package com.game.engine.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;

import com.game.engine.GameEngine;

/*
 * This util class made by Lew_ and edited/tuned by Morten
 */
public class FileUtils
{
	public static void DownloadFile(String game, File destination)
	{
		ProcessBuilder pb;
		Process p;
		try
		{
			if (destination == null)
			{
				destination = new File(GameEngine.GetPlugin().getDataFolder() + File.separator + game + File.separator + "maps");
				destination.mkdirs();
			}
			String[] cmd = new String[]
			{ "scp", "-P", "52679", "-r", "dev" + "@" + "167.114.36.26" + ":" + "/home/dev/game/" + game + "/*", destination.getAbsolutePath() };
			
			pb = new ProcessBuilder(cmd);
			p = pb.start();
			
			if (p.waitFor() != 0)
			{
				throw new Exception("Exception during RSync; return code = " + p.waitFor());
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			pb = null;
			p = null;
		}
	}

	public static boolean DownloadMaps(String Game)
	{
		File destination = new File(GameEngine.GetPlugin().getDataFolder() + File.separator + Game + File.separator + "maps");
		DownloadFile(Game, destination);
		return true;
	}
}