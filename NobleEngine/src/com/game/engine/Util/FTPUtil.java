package com.game.engine.Util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//
//import com.game.engine.GameEngine;
//
//
public class FTPUtil
{
//	private static FTPUtil instance = null;
//	
//	String server;
//	int port;
//	String user;
//	String pass;
//	
//	private FTPUtil()
//	{
//		
	}
//	
//	public static FTPUtil getInstance()
//	{
//		if(instance == null)
//		{
//			instance = new FTPUtil();
//		}
//		return instance;
//	}
//	
//	public void connectFTP(String server, int port, String user, String pass)
//	{
//		this.server = server;
//		this.port = port;
//		this.user = user;
//		this.pass = pass;
//	}
//	
//	private void createConnection(FTPClient ftp)
//	{
//		try
//		{
//			ftp.connect(server, port);
//			ftp.login(user, pass);
//			ftp.enterLocalPassiveMode();
//			ftp.setFileType(FTP.BINARY_FILE_TYPE);
//
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	public void downloadFile(String pathFTP, String pathLocal)
//	{
//		FTPClient ftp = new FTPClient();
//		
//		createConnection(ftp);
//		
//		File file = new File(pathLocal);
//		if(file.exists())
//		{
//			GameEngine.Debug("The file: " + file.getName() + " already exists! Delete it first");
//		}
//		else
//		{
//			try
//			{
//				FileOutputStream FOS = new FileOutputStream(file);
//				ftp.retrieveFile(pathFTP, FOS);
//				ftp.disconnect();
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//	}
//	
//	public void uploadFile(String pathToFTP, String pathToLocal, String nameOfFile)
//	{
//		FTPClient ftp = new FTPClient();
//		
//		createConnection(ftp);
//		
//		File localFile = new File(pathToLocal);
//		try
//		{
//			InputStream is = new FileInputStream(localFile);
//			boolean isDone = ftp.storeFile(nameOfFile, is);
//			is.close();
//			
//			if(isDone)
//			{
//				GameEngine.Debug("File &6" + nameOfFile + "&f has been uploaded to FTP");
//			}
//			else
//			{
//				GameEngine.Debug("File &6" + nameOfFile + "&f was not uploaded!");
//			}
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public void deleteFileFTP(String path)
//	{
//		FTPClient ftp = new FTPClient();
//		
//		createConnection(ftp);
//		
//		try
//		{
//			ftp.deleteFile(path);
//			ftp.disconnect();
//			GameEngine.Debug("the file with the path: &6" + path + "&f has been deleted");
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public void deleteFileLocal(String path)
//	{
//		File file = new File(path);
//		try
//		{
//			FileUtils.deleteDirectory(file);
//			GameEngine.Debug("The file: &6" + file.getName() + "&f has been deleted");
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//}
