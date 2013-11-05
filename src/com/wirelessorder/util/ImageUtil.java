package com.wirelessorder.util;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.wirelessorder.global.MyApplication;

public class ImageUtil {

	public static final float roundPix = 6;
	public static final int rate = 2;

	public static final int reflectionGap = 2;
	private static final int SOFT_CACHE_CAPACITY = 40;

	private final static LinkedHashMap<Integer, WeakReference<Bitmap>> sSoftBitmapCache = new LinkedHashMap<Integer, WeakReference<Bitmap>>(
			SOFT_CACHE_CAPACITY, 0.75f, true) {

		private static final long serialVersionUID = -7422412983039393262L;

		@Override
		protected boolean removeEldestEntry(
				Entry<Integer, WeakReference<Bitmap>> eldest) {
			// TODO Auto-generated method stub
			if (size() > SOFT_CACHE_CAPACITY) {

				return true;
			}
			return false;
		}

		@Override
		public WeakReference<Bitmap> put(Integer key,
				WeakReference<Bitmap> value) {
			// TODO Auto-generated method stub
			return super.put(key, value);
		}

	};

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 255, 255, 255);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap originalBitmap) {

		int width = originalBitmap.getWidth();

		int height = originalBitmap.getHeight();

		Matrix matrix = new Matrix();
		// 指定一个角度以0,0为坐标进行旋转
		// matrix.setRotate(30);
		// 指定矩阵(x轴不变，y轴相反)
		matrix.preScale(1, -1);
		// 将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图
		Bitmap reflectionBitmap = Bitmap
				.createBitmap(originalBitmap, 0, height / ImageUtil.rate,
						width, height / ImageUtil.rate, matrix, false);
		// 创建一个宽度不变，高度为原图+倒影图高度的位图
		Bitmap withReflectionBitmap = Bitmap.createBitmap(width, (height
				+ height / ImageUtil.rate + reflectionGap), Config.ARGB_8888);
		// 将上面创建的位图初始化到画布
		Canvas canvas = new Canvas(withReflectionBitmap);
		canvas.drawBitmap(originalBitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		/**
		 * 参数一:为渐变起初点坐标x位置， 参数二:为y轴位置， 参数三和四:分辨对应渐变终点， 最后参数为平铺方式，
		 * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
		 */
		LinearGradient shader = new LinearGradient(0,
				originalBitmap.getHeight(), 0,
				withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff,
				TileMode.MIRROR);
		// 设置阴影
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// 用已经定义好的画笔构建一个矩形阴影渐变效果
		canvas.drawRect(0, height, width, withReflectionBitmap.getHeight(),
				paint);

		return withReflectionBitmap;
	}

	public static Bitmap getBitmapByID(int sourceID) {
		if (sSoftBitmapCache.containsKey(sourceID)) {
			return sSoftBitmapCache.get(sourceID).get();
		} else {
			Bitmap bm = BitmapFactory.decodeResource(MyApplication
					.getInstance().getResources(), sourceID);
			sSoftBitmapCache.put(sourceID, new WeakReference<Bitmap>(bm));
			return bm;
		}

	}

	public static Drawable getRoundedCornerDrawable(Drawable drawable,
			float roundPx) {
		Bitmap bitmap = drawableToBitmap(drawable);
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 255, 255, 255);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return new BitmapDrawable(MyApplication.getInstance().getResources(),
				output);
	}

}