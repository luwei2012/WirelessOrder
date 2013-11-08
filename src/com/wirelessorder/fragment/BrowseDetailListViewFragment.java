package com.wirelessorder.fragment;

import android.app.ListFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wirelessorder.R;
import com.wirelessorder.adapter.BrowDetailListFragmentAdapter;
import com.wirelessorder.entity.Dish;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.listener.AddOrderButtonOnClickListener;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.network.LRUAsyncImageLoader;

public class BrowseDetailListViewFragment extends ListFragment {

	private BrowDetailListFragmentAdapter adapter;
	private ListView listView;
	private View right_views;
	private int m_CurCheckPosition = 0;

	public static BrowseDetailListViewFragment newInstance(int index,
			String v_class) {
		BrowseDetailListViewFragment f = new BrowseDetailListViewFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		args.putString("class", v_class);
		f.setArguments(args);
		return f;
	}

	private AddOrderButtonOnClickListener m_addOrderButtonOnClickListener;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			m_CurCheckPosition = savedInstanceState.getInt("curChoice", 0);
		}
		getListView().setSelector(R.drawable.selector_list_item);
	}

	public void notifyDataSetChanged(int index) {

		if (adapter != null) {
			adapter.notifyDataSetChanged(index);
		}
		listView.clearChoices();
		right_views.setVisibility(View.INVISIBLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		m_addOrderButtonOnClickListener = new AddOrderButtonOnClickListener();
		Bundle temptBundle = getArguments();
		int catalog = 0;
		if (temptBundle != null) {
			catalog = temptBundle.getInt("index");
		}

		View returnedView = inflater.inflate(
				R.layout.browse_detail_listfragment, null);
		returnedView.findViewById(R.id.browse_detail_listfragment_add_button)
				.setOnClickListener(m_addOrderButtonOnClickListener);
		listView = (ListView) returnedView.findViewById(android.R.id.list);
		right_views = returnedView.findViewById(R.id.right_views);

		adapter = new BrowDetailListFragmentAdapter(getActivity(),
				R.layout.browse_detail_listfragment_itemview, catalog, listView);

		listView.setAdapter(adapter);
		return returnedView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", m_CurCheckPosition);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		right_views.setVisibility(View.VISIBLE);
		if (m_CurCheckPosition != position) {
			m_CurCheckPosition = position;
		}
		m_addOrderButtonOnClickListener.setCurrentSelectedDishId((int) adapter
				.getItemId(m_CurCheckPosition));
		ImageView bigImageView = (ImageView) getActivity().findViewById(
				R.id.browse_detail_listfragment_imageview);
		Dish dish = (Dish) adapter.getItem(position);
		setViewImage(bigImageView, dish.getImageUrl());
		TextView remark = (TextView) getActivity().findViewById(
				R.id.browse_detail_listfragment_remark_textview);
		remark.setText(dish.getRemarks());
		super.onListItemClick(l, v, position, id);
	}

	public void setViewImage(final ImageView target, String url,
			final Boolean needFillet) {
		try {

			Bitmap bitmap = LRUAsyncImageLoader.getInstance(
					MyApplication.getInstance().getString()).loadBitmap(url,
					new ImageCallBack(url) {
						public void imageLoaded() {

							if (this.bitmap.get() != null
									&& this.bitmap.get().getRowBytes() > 0) {

								target.setImageBitmap(this.bitmap.get());
							}
						}
					}, needFillet);
			if (bitmap != null) {

				target.setImageBitmap(bitmap);
			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void setViewImage(final ImageView v, String url) {
		try {
			setViewImage(v, url, false);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
