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
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.network.LRUAsyncImageLoader;

public class BrowDetailListFragmentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private int type;
	private int resID;
	private ListView listView;

	public BrowDetailListFragmentAdapter(Context context,
			int browseDetailListfragmentItemview, int catalog, ListView listView) {
		// TODO Auto-generated constructor stub
		this.listView = listView;
		inflater = ((Activity) context).getLayoutInflater();
		resID = browseDetailListfragmentItemview;
		type = catalog;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (MyApplication.getInstance().dishTypes.isEmpty()
				|| MyApplication.getInstance().dishTypes.size() == 0) {
			return 0;
		} else {
			return MyApplication.getInstance().dishTypes.get(type).getDishes()
					.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return MyApplication.getInstance().dishTypes.get(type).getDishes()
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return MyApplication.getInstance().dishTypes.get(type).getDishes()
				.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(resID, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.textView0 = (TextView) convertView
					.findViewById(R.id.browse_detail_listfragment_itemview_name_textview);
			viewHolder.textView1 = (TextView) convertView
					.findViewById(R.id.browse_detail_listfragment_itemview_price_textview);
			viewHolder.textView2 = (TextView) convertView
					.findViewById(R.id.flow_sales);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.browse_detail_listfragment_itemview_imageview);
			convertView.setTag(viewHolder);
		}
		Dish dish = MyApplication.getInstance().dishTypes.get(type).getDishes()
				.get(position);
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.textView0.setText(dish.getName());
		if (dish.getSales() < 1.0) {
			viewHolder.textView1.setTextColor(MyApplication.getInstance()
					.getResources().getColor(android.R.color.darker_gray));
			viewHolder.textView1.setBackgroundResource(R.drawable.line);
			viewHolder.textView2.setVisibility(View.VISIBLE);
			viewHolder.textView2.setText("￥"
					+ (int) (dish.getPrice() * dish.getSales()));
		} else {
			viewHolder.textView1.setTextColor(MyApplication.getInstance()
					.getResources().getColor(android.R.color.white));
			viewHolder.textView1.setBackgroundResource(0);
			viewHolder.textView2.setVisibility(View.INVISIBLE);
		}
		viewHolder.textView1.setText("￥" + dish.getPrice());
		setViewImage(viewHolder.imageView, dish.getImageUrl(), position);
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
							if (listView != null) {

								ImageView imageViewByTag = (ImageView) listView
										.findViewWithTag(this.key);
								if (imageViewByTag != null) {

									if (this.bitmap.get() != null
											&& this.bitmap.get().getRowBytes() > 0) {

										imageViewByTag
												.setImageBitmap(this.bitmap
														.get());
									}
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
		TextView textView0, textView1, textView2;
		ImageView imageView;
	}

	public void notifyDataSetChanged(int index) {
		// TODO Auto-generated method stub
		type = index;
		super.notifyDataSetChanged();
	}
}
