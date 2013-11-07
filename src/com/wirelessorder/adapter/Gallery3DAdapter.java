package com.wirelessorder.adapter;

import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.network.LRUAsyncImageLoader;

public class Gallery3DAdapter extends FancyCoverFlowAdapter {
	int mGalleryItemBackground;
	private int type;
	private Gallery gallery;

	public Gallery3DAdapter(int typeIndex, Gallery gallery) {
		this.gallery = gallery;
		type = typeIndex;
	}

	public void notifyDataSetChanged(int index) {
		// TODO Auto-generated method stub
		type = index;
		super.notifyDataSetChanged();
	}

	public int getCount() {
		if (MyApplication.getInstance().dishTypes.isEmpty()
				|| MyApplication.getInstance().dishTypes.size() == 0) {
			return 0;
		} else {
			return MyApplication.getInstance().dishTypes.get(type).getDishes()
					.size();
		}
	}

	public Object getItem(int position) {
		return MyApplication.getInstance().dishTypes.get(type).getDishes()
				.get(position);
	}

	public long getItemId(int position) {
		return MyApplication.getInstance().dishTypes.get(type).getDishes()
				.get(position).getId();
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

	public void setViewImage(final ImageView v, String url, int position) {
		// Log.e("URL", url);
		try {
			setViewImage(v, url, position, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public void setViewImage(final ImageView v, String url, final int position,
			final Boolean needFillet) {
		try {
			Bitmap draw_pic = LRUAsyncImageLoader.getInstance(
					MyApplication.getInstance().getString()).loadBitmap(url,
					new ImageCallBack(url + position) {
						public void imageLoaded() {
							ImageView imageViewByTag = (ImageView) gallery
									.findViewWithTag(this.key);

							if (imageViewByTag != null) {
								if (this.bitmap.get() != null
										&& this.bitmap.get().getRowBytes() > 0) {
									imageViewByTag.setImageBitmap(this.bitmap
											.get());
									ViewGroup viewGroup = (ViewGroup) imageViewByTag
											.getParent();
									viewGroup.requestLayout();
									viewGroup.invalidate();
								}
							}
						}
					}, needFillet);
			if (draw_pic != null) {
				try {
					v.setImageBitmap(draw_pic);
				} catch (OutOfMemoryError e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public View getCoverFlowItem(int position, View reusableView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		if (reusableView == null) {
			// 创建一个ImageView用来显示已经画好的bitmapWithReflection
			reusableView = new ImageView(parent.getContext());
			((ImageView) reusableView)
					.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// 设置imageView大小 ，也就是最终显示的图片大小
			reusableView.setLayoutParams(new FancyCoverFlow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

		String urlString = MyApplication.getInstance().dishTypes.get(type)
				.getDishes().get(position).getImageUrl();
		reusableView.setTag(urlString + position);
		setViewImage((ImageView) reusableView, urlString, position);
		return reusableView;
	}
}
