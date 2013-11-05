package com.wirelessorder.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

import com.wirelessorder.R;

/** 继承 TabHost 组件，带有切入切出的滑动动画效果。 */
public class AnimationTabHost extends TabHost {

	private Animation slideLeftIn;// 从屏幕左边进来
	private Animation slideLeftOut;// 从屏幕左边出去
	private Animation slideRightIn;// 从屏幕右边进来
	private Animation slideRightOut;// 从屏幕右边出去

	/** 记录是否打开动画效果 */
	private boolean isOpenAnimation;
	/** 记录当前标签页的总数 */
	private int mTabCount;

	public AnimationTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		/** 初始化默认动画 */
		slideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_out);
		isOpenAnimation = false;// 动画默认关闭

	}

	/**
	 * 设置是否打开动画效果
	 * 
	 * @param isOpenAnimation
	 *            true：打开
	 */
	public void setOpenAnimation(boolean isOpenAnimation) {
		this.isOpenAnimation = isOpenAnimation;
	}

	/**
	 * 
	 * @return 返回当前标签页的总数
	 */

	public int getTabCount() {
		return mTabCount;
	}

	@Override
	public void addTab(TabSpec tabSpec) {
		mTabCount++;
		super.addTab(tabSpec);
	}

	// 重写setCurrentTab(int index) 方法，这里加入动画！关键点就在这。
	@Override
	public void setCurrentTab(int index) {
		// 切换前所在页的页面
		int mCurrentTabID = getCurrentTab();
		if (null != getCurrentView()) {
			// 第一次设置 Tab 时，该值为 null。
			if (isOpenAnimation) {
				// 离开的页面
				// 循环时，末页到第一页
				if (mCurrentTabID == (mTabCount - 1) && index == 0) {
					getCurrentView().startAnimation(slideLeftOut);
				}
				// 循环时，首页到末页
				else if (mCurrentTabID == 0 && index == (mTabCount - 1)) {
					getCurrentView().startAnimation(slideRightOut);
				}
				// 切换到右边的界面，从左边离开
				else if (index > mCurrentTabID) {
					getCurrentView().startAnimation(slideLeftOut);
				}
				// 切换到左边的界面，从右边离开
				else if (index < mCurrentTabID) {
					getCurrentView().startAnimation(slideRightOut);
				}
			}
		}
		// 设置当前页
		super.setCurrentTab(index);

		if (isOpenAnimation) {
			// 当前页进来是动画
			// 循环时，末页到第一页
			if (mCurrentTabID == (mTabCount - 1) && index == 0) {
				getCurrentView().startAnimation(slideRightIn);
			}
			// 循环时，首页到末页
			else if (mCurrentTabID == 0 && index == (mTabCount - 1)) {
				getCurrentView().startAnimation(slideLeftIn);
			}
			// 切换到右边的界面，从右边进来
			else if (index > mCurrentTabID) {
				getCurrentView().startAnimation(slideRightIn);
			}
			// 切换到左边的界面，从左边进来
			else if (index < mCurrentTabID) {
				getCurrentView().startAnimation(slideLeftIn);
			}
		}
	}
}
