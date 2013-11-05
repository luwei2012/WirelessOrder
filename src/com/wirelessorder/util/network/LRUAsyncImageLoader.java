package com.wirelessorder.util.network;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.wirelessorder.R;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.ImageUtil;
import com.wirelessorder.util.ThreadPoolUtils;

public class LRUAsyncImageLoader {
	private static LRUAsyncImageLoader instance = null;
	private LruCache<String, Bitmap> sHardBitmapCache;
	private static String header = null;
	private static LRUAsynclmageHandler asynclmageHandler;
	private static Boolean isDownloadSuccess = true;
	private static final int SOFT_CACHE_CAPACITY = 40;
	private File mCacheDir = MyApplication.getInstance()
			.getApplicationContext().getCacheDir();

	// 获取bitmap
	private static BitmapFactory.Options sBitmapOptions;
	static {
		sBitmapOptions = new BitmapFactory.Options();
		sBitmapOptions.inPurgeable = true; // bitmap can be purged to disk
		sBitmapOptions.inSampleSize = 1;
	}
	private final static LinkedHashMap<String, WeakReference<Bitmap>> sSoftBitmapCache = new LinkedHashMap<String, WeakReference<Bitmap>>(
			SOFT_CACHE_CAPACITY, 0.75f, true) {

		private static final long serialVersionUID = -7422412983039393262L;

		@Override
		protected boolean removeEldestEntry(
				Entry<String, WeakReference<Bitmap>> eldest) {
			// TODO Auto-generated method stub
			if (size() > SOFT_CACHE_CAPACITY) {

				return true;
			}
			return false;
		}

		@Override
		public WeakReference<Bitmap> put(String key, WeakReference<Bitmap> value) {
			// TODO Auto-generated method stub
			return super.put(key, value);
		}

	};

	private LRUAsyncImageLoader(String host) {
		// Get memory class of this device, exceeding this amount will throw an
		// OutOfMemory exception.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		sHardBitmapCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getRowBytes() * value.getHeight();
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				// TODO Auto-generated method stub
				sSoftBitmapCache.put(key, new WeakReference<Bitmap>(oldValue));
			}

		};
		header = host;
		asynclmageHandler = new LRUAsynclmageHandler(this);
	}

	public static synchronized LRUAsyncImageLoader getInstance(String host) {
		if (instance == null) {
			instance = new LRUAsyncImageLoader(host);
		} else if (!header.equals(host)) {
			instance.sHardBitmapCache.evictAll();
			asynclmageHandler.removeMessages(0);
			sSoftBitmapCache.clear();
			instance = new LRUAsyncImageLoader(host);
		}
		return instance;
	}

	public Bitmap loadBitmap(final String imageUrl,
			final ImageCallBack imageCallback) {
		return loadBitmap(imageUrl, imageCallback, false);
	}

	public Bitmap loadBitmap(final String imageUrl,
			final ImageCallBack imageCallback, final Boolean fitScreen) {
		if (!isURLImage(imageUrl)) {
			return ImageUtil.getBitmapByID(R.drawable.timeline_image_loading);

		} else {
			// 查找内存缓存
			Bitmap bitmap = getBitmap(header + imageUrl);
			if (bitmap != null) {
				return bitmap;
			} else {// 查找sd缓存
				bitmap = getBitmapFromDisk(header + imageUrl);
				if (bitmap != null) {
					return bitmap;
				}
			}
		}

		// 两个缓存都没有则需要开启下载任务，开启之前将该url添加到内存缓存的映射表中，防止多次下载

		// 如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
		ThreadPoolUtils.execute(new Runnable() {

			@Override
			public void run() {
				Bitmap bitmap = loadImageFromUrl(header + imageUrl);
				if (bitmap != null) {
					if (fitScreen) {
						bitmap = ImageUtil.getRoundedCornerBitmap(bitmap,
								ImageUtil.roundPix);
					}
					imageCallback.setBitmap(bitmap);
					Message message = Message.obtain(null, 0, imageCallback);
					asynclmageHandler.sendMessage(message);

					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					if (width != 0 && height != 0) {

						if (LRUAsyncImageLoader.isDownloadSuccess) {
							sHardBitmapCache.put(header + imageUrl, bitmap);
							try {
								// 将下载的图片缓存到本地，防止JVM清空内存后需要重新下载
								putBitmap2Disk(header + imageUrl, bitmap);

							} catch (NullPointerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}

				}

			}
		});
		return null;

	}

	public Boolean isURLImage(String url) {

		return (url != null && (url.contains(".png") || url.contains(".jpg")
				|| url.contains(".jpeg") || url.contains(".bmp")
				|| url.contains(".PNG") || url.contains(".JPG")
				|| url.contains(".JPEG") || url.contains(".BMP")));
	}

	public Bitmap loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		Bitmap bitmap = null;
		try {
			if (isURLImage(url)) {
				m = new URL(url);
				URLConnection connection = m.openConnection();
				connection.setUseCaches(true);
				i = new BufferedInputStream(m.openStream());
				bitmap = BitmapFactory.decodeStream(i, null, sBitmapOptions);
				// 恢复原始大小
				sBitmapOptions.inSampleSize = 1;
				i.close();
			} else {
				LRUAsyncImageLoader.isDownloadSuccess = false;
				bitmap = ImageUtil
						.getBitmapByID(R.drawable.timeline_image_loading);
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			if (LRUAsyncImageLoader.isDownloadSuccess) {
				sHardBitmapCache.evictAll();
				LRUAsyncImageLoader.isDownloadSuccess = false;
				sBitmapOptions.inSampleSize *= 2;
				bitmap = loadImageFromUrl(url);
			} else {
				bitmap = ImageUtil.getBitmapByID(R.drawable.outofmemory);
			}

		} catch (MalformedURLException e) {

			e.printStackTrace();
			LRUAsyncImageLoader.isDownloadSuccess = false;
			bitmap = ImageUtil.getBitmapByID(R.drawable.timeline_image_loading);
		} catch (IOException e) {

			e.printStackTrace();
			LRUAsyncImageLoader.isDownloadSuccess = false;
			bitmap = ImageUtil.getBitmapByID(R.drawable.timeline_image_loading);
		}
		return bitmap;
	}

	private static class LRUAsynclmageHandler extends Handler {
		@SuppressWarnings("unused")
		WeakReference<LRUAsyncImageLoader> mImageLoader;

		LRUAsynclmageHandler(LRUAsyncImageLoader asyncImageLoader) {
			mImageLoader = new WeakReference<LRUAsyncImageLoader>(
					asyncImageLoader);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ImageCallBack imageCallBack = (ImageCallBack) msg.obj;
				imageCallBack.imageLoaded();

				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public Boolean putBitmap(String key, Bitmap Bitmap) {
		if (getBitmap(key) == null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(key, Bitmap);
			}
			return true;
		}
		return false;
	}

	private Bitmap getBitmap(String key) {
		// TODO Auto-generated method stub
		synchronized (sHardBitmapCache) {
			Bitmap bitmap = sHardBitmapCache.get(key);
			if (bitmap != null) {
				return bitmap;
			}
		}
		// 硬引用缓存区间中读取失败，从软引用缓存区间读取
		synchronized (sSoftBitmapCache) {
			WeakReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);
			if (bitmapReference != null) {
				Bitmap bitmap2 = bitmapReference.get();
				if (bitmap2 != null)
					return bitmap2;
				else {
					sSoftBitmapCache.remove(key);
				}
			}
		}
		return null;
	}

	private File getFile(String key) throws FileNotFoundException {
		String fileName = key.substring(key.lastIndexOf("/") + 1);
		File file = new File(mCacheDir, fileName);
		if (!file.exists() || !file.isFile())
			throw new FileNotFoundException("文件不存在或有同名文件夹");
		return file;
	}

	// 缓存bitmap到外部存储
	public boolean putBitmap2Disk(String key, Bitmap bitmap) {
		File file = null;
		try {
			file = getFile(key);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (file != null) {
			return true;
		}
		// 将下载的图片缓存到本地，防止JVM清空内存后需要重新下载
		FileOutputStream fos = getOutputStream(key);
		boolean saved = bitmap.compress(CompressFormat.JPEG, 100, fos);
		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return saved;
	}

	// 根据key获取OutputStream
	private FileOutputStream getOutputStream(String key) {
		String fileName = key.substring(key.lastIndexOf("/") + 1);
		if (mCacheDir == null)
			return null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mCacheDir.getAbsolutePath()
					+ File.separator + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fos;
	}

	public Bitmap getBitmapFromDisk(String key) {
		File bitmapFile = null;
		try {
			bitmapFile = getFile(key);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			bitmapFile = null;
			e.printStackTrace();
		}
		if (bitmapFile != null) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(
						bitmapFile), null, sBitmapOptions);
			} catch (OutOfMemoryError e) {
				sHardBitmapCache.evictAll();
				e.printStackTrace();
				bitmap = getBitmapFromDisk(bitmapFile, 2);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bitmap != null) {
				// 重新将其缓存至硬引用中
				putBitmap(key, bitmap);
				return bitmap;
			}
		}
		return null;
	}

	public Bitmap getBitmapFromDisk(File bitmapFile, int inSampleSize) {
		BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
		sBitmapOptions.inPurgeable = true; // bitmap can be purged to disk
		sBitmapOptions.inSampleSize = inSampleSize;
		if (bitmapFile != null) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(
						bitmapFile), null, sBitmapOptions);
			} catch (OutOfMemoryError e) {
				bitmap = getBitmapFromDisk(bitmapFile, inSampleSize * 2);
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bitmap != null) {
				return bitmap;
			}
		}
		return null;
	}
}
