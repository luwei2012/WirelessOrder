package com.wirelessorder.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wirelessorder.R;
import com.wirelessorder.entity.Dish;
import com.wirelessorder.entity.DishMenu;
import com.wirelessorder.entity.Menu;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.network.LRUAsyncImageLoader;

public class OrderListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private int resID;
	private ListView listView;

	public OrderListAdapter(Context context,
			int browseDetailListfragmentItemview, ListView listView) {
		// TODO Auto-generated constructor stub
		this.listView = listView;
		inflater = ((Activity) context).getLayoutInflater();
		resID = browseDetailListfragmentItemview;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Menu menu = MyApplication.getInstance().menu;
		if (menu == null || menu.getDish_menus() == null
				|| menu.getDish_menus().size() == 0) {
			return 0;
		} else {
			return MyApplication.getInstance().menu.getDish_menus().size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return MyApplication.getInstance().menu.getDish_menus().get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return MyApplication.getInstance().menu.getDish_menus().get(position)
				.getDish().getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(resID, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.textView0 = (TextView) convertView
					.findViewById(R.id.order_dishname_textview);
			viewHolder.textView1 = (TextView) convertView
					.findViewById(R.id.order_num_textview);
			viewHolder.textView2 = (TextView) convertView
					.findViewById(R.id.order_price_textview);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.dish_ico);
			convertView.setTag(viewHolder);
		}
		DishMenu dish_menu = MyApplication.getInstance().menu.getDish_menus()
				.get(position);
		Dish dish = dish_menu.getDish();
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.textView0.setText(dish.getName());
		viewHolder.textView1.setText("" + dish_menu.getAmount() + "份");
		viewHolder.textView2.setText("￥" + dish.getPrice());

		String url = dish.getImageUrl();
		setViewImage(viewHolder.imageView, url, position,true);
		return convertView;
	}

	public void setViewImage(final ImageView v, String url, int position) {
		// Log.e("URL", url);
		try {
			setViewImage(v, url, position, false);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public void setViewImage(final ImageView v, String url, int position,
			final Boolean needFillet) {
		try {
			Bitmap draw_pic = LRUAsyncImageLoader.getInstance(
					MyApplication.getInstance().getString()).loadBitmap(url,
					new ImageCallBack(url + position) {
						public void imageLoaded() {
							ImageView imageViewByTag = (ImageView) listView
									.findViewWithTag(this.key);
							if (imageViewByTag != null) {

								if (this.bitmap.get() != null
										&& this.bitmap.get().getRowBytes() > 0) {

									imageViewByTag.setImageBitmap(this.bitmap
											.get());
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

	class ViewHolder {
		TextView textView0;
		TextView textView1;
		TextView textView2;
		ImageView imageView;
	}
}
