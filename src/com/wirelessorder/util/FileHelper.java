package com.wirelessorder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;

import com.wirelessorder.global.MyApplication;

public class FileHelper {

	/** SD卡是否存在 **/
	private boolean hasSD = false;
	/** 图片存放的根目录的路径 **/
	private String StockPATH;
	private long mTimeOut = 24 * 60 * 60 * 1000;// 定义过期时间,一天

	public long getmTimeOut() {
		return mTimeOut;
	}

	public void setmTimeOut(long mTimeOut) {
		this.mTimeOut = mTimeOut;
	}

	public FileHelper() {
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		StockPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + MyApplication.getInstance().getPackageName()
				+ File.separator;
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */

	public File createStockFile(String fileName) throws IOException {
		String catalogPath = StockPATH + fileName;
		File file = null;
		if (hasSD) {
			File catalogFile = new File(StockPATH);
			if (!catalogFile.exists()) {
				catalogFile.mkdirs();
				file = new File(catalogPath);
			} else {
				file = new File(catalogPath);
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		}
		return file;
	}

	public File createFile(String fileName) throws IOException {
		String catalogPath = StockPATH + fileName;
		File file = null;
		if (hasSD) {
			File catalogFile = new File(StockPATH);
			if (!catalogFile.exists()) {
				catalogFile.mkdirs();
				file = new File(catalogPath);
			} else {
				file = new File(catalogPath);
			}
		}
		return file;
	}

	public File getStockFile(String fileName) throws IOException {
		String catalogPath = StockPATH + fileName;
		File file = null;
		if (hasSD) {
			file = new File(catalogPath);
		}
		return file;
	}

	public File createStockDir(String fileName) throws IOException {
		File file = null;
		if (hasSD) {
			File catalogFile = new File(fileName);
			if (!catalogFile.exists()) {
				catalogFile.mkdirs();
				file = new File(fileName);
			} else {
				file = new File(fileName);
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		}
		return file;
	}

	public File createStockDir() throws IOException {
		return createStockDir(getStockPATH());
	}

	/**
	 * 删除SD卡上的文件
	 * 
	 * @param fileName
	 */
	public boolean deleteStockFile(String fileName) {
		String catalogPath = StockPATH + fileName;
		File file = new File(catalogPath);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		else {
			return file.delete();
		}
	}

	/**
	 * 删除SD卡上的文件夹
	 * 
	 * @param fileName
	 */
	public void deleteStockDir(String catalog) {
		String catalogPath = StockPATH + catalog;
		File file = new File(catalogPath);
		deleteStockDir(file);
	}

	/**
	 * 删除SD卡上的文件夹
	 * 
	 * @param file
	 */
	public void deleteStockDir(File catalog) {
		if (catalog.exists()) { // 判断文件是否存在
			if (catalog.isFile()) { // 判断是否是文件
				catalog.delete();
			} else if (catalog.isDirectory()) { // 否则如果它是一个目录
				File files[] = catalog.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteStockDir(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			catalog.delete();
		} else {
			// System.out.println("所删除的文件不存在！" + '\n');
		}
	}

	/**
	 * 读取SD卡中文本文件
	 * 
	 * @param fileName
	 * @return
	 */
	public String readStockFile(String fileName) {
		String catalogPath = StockPATH + fileName;
		StringBuffer sb = new StringBuffer();
		File file = new File(catalogPath);
		if (file.exists()) {
			try {
				BufferedReader fis = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				String data;
				while ((data = fis.readLine()) != null) {
					sb.append(data);
				}
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		} else {
			return null;
		}
	}

	public String getStockPATH() {
		return StockPATH.substring(0, StockPATH.lastIndexOf('/'));
	}

	public String getStockFilePATH(String fileName) {
		return StockPATH + fileName;
	}

	public boolean hasSD() {
		return hasSD;
	}

	public void removeExpiredFile(File file) {
		if (file.exists()) {
			if (file.isFile()) { // 判断是否是文件
				if (file.length() == 0) {
					file.delete();
				} else if (System.currentTimeMillis() - file.lastModified() > mTimeOut) {
					// 文件超多一天没有修改过，过期文件，删除过期
					file.delete();
				}
			}
		}
	}

	public void removeExpiredDirFile(File dirFile) {
		if (dirFile.exists()) { // 判断文件是否存在
			if (dirFile.isFile()) { // 判断是否是文件
				removeExpiredFile(dirFile); // 删除过期文件
			} else if (dirFile.isDirectory()) { // 否则如果它是一个目录
				File files[] = dirFile.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					removeExpiredDirFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
				dirFile.setLastModified(System.currentTimeMillis());// 更新文件夹的lastModify日期
			}
		}
	}

	public void removeExpiredDirFile(String dirName) {
		File dirFile = new File(dirName);
		removeExpiredDirFile(dirFile);
	}

	public void removeExpiredDirFile() {
		removeExpiredDirFile(getStockPATH());
	}
}
