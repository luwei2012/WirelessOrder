package com.wirelessorder.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wirelessorder.R;
import com.wirelessorder.entity.DishType;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.interfaces.OnDetailChangedListener;
import com.wirelessorder.util.Callback;

public class BrowseDetailCatalogFragment extends ListFragment {
	private boolean isLoading = false;
	private int m_CurCheckPosition = 0;
	private OnDetailChangedListener listener;
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.listview, null);
		listView = (ListView) view.findViewById(android.R.id.list);
		listView.setAdapter(new MyArrayAdapter(getActivity(),
				R.layout.simple_list_item));
		listener = (OnDetailChangedListener) getActivity();
		if (!isLoading) {
			startLoadingThread();
		}
		return view;
	}

	private void startLoadingThread() {
		// TODO Auto-generated method stub
		isLoading = true;
		DishType.getDishTypes(new Callback<String>() {

			@Override
			public void excute(String t) {
				// TODO Auto-generated method stub
				isLoading = false;
				listener.onDetailChanged(m_CurCheckPosition);
			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			m_CurCheckPosition = savedInstanceState.getInt("curChoice", 0);
		}

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setItemChecked(m_CurCheckPosition, true);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try {
			if (m_CurCheckPosition != position) {
				m_CurCheckPosition = position;
				listener.onDetailChanged(m_CurCheckPosition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", m_CurCheckPosition);
	}

	public static class MyArrayAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private int resID;

		public MyArrayAdapter(Context context, int res) {
			super();
			inflater = ((Activity) context).getLayoutInflater();
			// TODO Auto-generated constructor stub
			resID = res;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return MyApplication.getInstance().dishTypes.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return MyApplication.getInstance().dishTypes.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return MyApplication.getInstance().dishTypes.get(arg0).getId();
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				arg1 = inflater.inflate(resID, null);
			}
			((TextView) arg1).setText(MyApplication.getInstance().dishTypes
					.get(arg0).getName());
			return arg1;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (MyApplication.getInstance().dishTypes.size() == 0) {
			startLoadingThread();
		}
	}

}
