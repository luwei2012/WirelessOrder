package com.wirelessorder.util;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

public abstract class ImageCallBack {
	public String key;
	public WeakReference<Bitmap> bitmap;

	public ImageCallBack(String key) {
		super();
		this.key = key;
	}

	public ImageCallBack() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ImageCallBack(String key, Bitmap bitmap) {
		super();
		this.key = key;
		this.bitmap = new WeakReference<Bitmap>(bitmap);
	}

	public String getUrl() {
		return key;
	}

	public void setUrl(String key) {
		this.key = key;
	}

	public Bitmap getBitmap() {
		return bitmap.get();
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = new WeakReference<Bitmap>(bitmap);
	}

	public abstract void imageLoaded();
}
