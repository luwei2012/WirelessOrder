package com.wirelessorder.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ReflectionImageView extends ImageView {
	private PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	private Paint paint;
	private ColorMatrix colorMatrix;
	private float saturation;
	private float imageReflectionRatio;
	private int reflectionGap;
	private Bitmap wrappedViewBitmap;
	private float originalScaledownFactor;

	public ReflectionImageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public ReflectionImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ReflectionImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.colorMatrix = new ColorMatrix();
		this.setSaturation(1);
		// TODO Auto-generated constructor stub
	}

	public void setSaturation(float saturation) {
		if (saturation != this.saturation) {
			this.saturation = saturation;
			this.colorMatrix.setSaturation(saturation);
			this.paint.setColorFilter(new ColorMatrixColorFilter(
					this.colorMatrix));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int measuredWidth = this.getMeasuredWidth();
		int measuredHeight = this.getMeasuredHeight();

		if (this.wrappedViewBitmap == null
				|| this.wrappedViewBitmap.getWidth() != measuredWidth
				|| this.wrappedViewBitmap.getHeight() != measuredHeight) {
			this.wrappedViewBitmap = Bitmap.createBitmap(measuredWidth,
					measuredHeight, Bitmap.Config.ARGB_8888);
		}
		canvas.setBitmap(wrappedViewBitmap);
		canvas.setDrawFilter(filter);
		super.onDraw(canvas);
		createReflectedImages(canvas);
		canvas.drawBitmap(this.wrappedViewBitmap, 0, 0, paint);
	}

	/**
	 * Creates the reflected images.
	 * 
	 * @return true, if successful
	 */
	private void createReflectedImages(Canvas canvas) {

		final int width = this.wrappedViewBitmap.getWidth();
		final int height = this.wrappedViewBitmap.getHeight();

		final Matrix matrix = new Matrix();
		matrix.postScale(1, -1);

		final int scaledDownHeight = (int) (height * originalScaledownFactor);
		final int invertedHeight = height - scaledDownHeight - reflectionGap;
		final int invertedBitmapSourceTop = scaledDownHeight - invertedHeight;
		final Bitmap invertedBitmap = Bitmap.createBitmap(
				this.wrappedViewBitmap, 0, invertedBitmapSourceTop, width,
				invertedHeight, matrix, true);
		Paint defaultPaint = new Paint();
		canvas.drawBitmap(invertedBitmap, 0, scaledDownHeight + reflectionGap,
				defaultPaint);

		final Paint paint = new Paint();
		final LinearGradient shader = new LinearGradient(0, height
				* imageReflectionRatio + reflectionGap, 0, height, 0x70ffffff,
				0x00ffffff, Shader.TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawRect(0, height * (1 - imageReflectionRatio), width, height,
				paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		// When reflection is enabled calculate proportional scale down
		// factor.
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int originalChildHeight = this.getMeasuredHeight();
		this.originalScaledownFactor = (originalChildHeight
				* (1 - this.imageReflectionRatio) - reflectionGap)
				/ originalChildHeight;

		this.setMeasuredDimension(
				(int) (this.getMeasuredWidth() * this.originalScaledownFactor),
				this.getMeasuredHeight());
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}

}
