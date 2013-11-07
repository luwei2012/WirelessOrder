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
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.interfaces.OnDetailChangedListener;

public class BrowseDetailCatalogFragment extends ListFragment {
	private int m_CurCheckPosition = 0;
	private OnDetailChangedListener listener;
	private ListView listView;
	private MyArrayAdapter myArrayAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.listview, null);
		listView = (ListView) view.findViewById(android.R.id.list);
		myArrayAdapter = new MyArrayAdapter(getActivity(),
				R.layout.simple_list_item);
		listView.setAdapter(myArrayAdapter);
		listener = (OnDetailChangedListener) getActivity(); 
		return view;
	}

	public void notifyDataSetChanged(int index) {
		m_CurCheckPosition = index;
		if (myArrayAdapter != null) {
			myArrayAdapter.notifyDataSetChanged();
		}
		listView.clearChoices();
		if (m_CurCheckPosition<myArrayAdapter.getCount()) {
			listView.setItemChecked(m_CurCheckPosition, true);
		}
		
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			m_CurCheckPosition = savedInstanceState.getInt("curChoice", 0);
		}
		getListView().setSelector(R.drawable.selector_list_item);
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
	}

}
