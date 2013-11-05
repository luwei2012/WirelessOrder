package com.wirelessorder.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wirelessorder.R;

/**
 * @author XZQ
 * @version
 */
public class MyProgressDialog extends Dialog {

	public Context context;// 上下文
	public TextView toastView;
	public ClickCallBack callBack = null;
	ImageView img_loading;
	RotateAnimation rotateAnimation;

	public interface ClickCallBack {
		public void onCloseLickListerner();
	}

	public MyProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public MyProgressDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		View view = LayoutInflater.from(context).inflate(R.layout.load, null); // 加载自己定义的布局
		toastView = (TextView) view.findViewById(R.id.tv_msg);
		img_loading = (ImageView) view.findViewById(R.id.img_load);
		RelativeLayout img_close = (RelativeLayout) view
				.findViewById(R.id.img_cancel);
		rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				context, R.anim.refresh); // 加载XML文件中定义的动画 
		setContentView(view);// 为Dialoge设置自己定义的布局
		// 为close的那个文件添加事件
		img_close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (callBack != null) {
					callBack.onCloseLickListerner();
				}
				dismiss();
			}
		});
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		img_loading.setAnimation(rotateAnimation);// 开始动画
		super.show();
		
	}

	public void setClickCallBack(ClickCallBack clickListener) {
		callBack = clickListener;
	}

	public void setText(String contentString) {
		if (toastView != null) {
			toastView.setText(contentString);
		}
	}
}
