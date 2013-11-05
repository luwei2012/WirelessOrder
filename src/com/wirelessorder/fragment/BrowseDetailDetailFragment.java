package com.wirelessorder.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;

import com.wirelessorder.R;
import com.wirelessorder.adapter.Gallery3DAdapter;
import com.wirelessorder.entity.Dish;
import com.wirelessorder.listener.AddOrderButtonOnClickListener;

public class BrowseDetailDetailFragment extends Fragment implements
		OnItemClickListener, OnItemSelectedListener {

	private View mContentView;
	private TextView m_DetailTextView, m_nameTextView, m_priceTextView;
	private FancyCoverFlow m_gallery;
	private Button m_HandinButton;
	private AddOrderButtonOnClickListener m_addOrderButtonOnClickListener;

	public static BrowseDetailDetailFragment newInstance(int index,
			String v_class) {
		BrowseDetailDetailFragment f = new BrowseDetailDetailFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		args.putString("class", v_class);
		f.setArguments(args);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.browse_details_fragment, null);
		mContentView = inflater.inflate(R.layout.browse_details_fragment, null);
		m_addOrderButtonOnClickListener = new AddOrderButtonOnClickListener();
		Bundle temptBundle = getArguments();
		int catalog = 0;
		String classString = null;
		if (temptBundle != null) {
			catalog = temptBundle.getInt("index");
			classString = temptBundle.getString("class");
		}

		initialGallery(catalog, classString);
		m_nameTextView = (TextView) mContentView.findViewById(R.id.flow_name);
		m_priceTextView = (TextView) mContentView.findViewById(R.id.flow_price);
		m_DetailTextView = (TextView) mContentView
				.findViewById(R.id.browse_fragment_description);
		m_HandinButton = (Button) mContentView
				.findViewById(R.id.browse_fragment_add_button);
		m_HandinButton.setOnClickListener(m_addOrderButtonOnClickListener);

		return mContentView;
	}

	private void initialGallery(int catalog) {

		m_gallery = (FancyCoverFlow) mContentView
				.findViewById(R.id.browse_fragment_imagedetails);
		m_gallery.setAdapter(new Gallery3DAdapter(catalog, m_gallery));
		m_gallery.setOnItemClickListener((OnItemClickListener) this);
		m_gallery.setOnItemSelectedListener((OnItemSelectedListener) this);
	}

	private void initialGallery(int catalog, String v_class) {
		if (v_class.equals("browse")) {
			initialGallery(catalog);
			return;
		}
		m_gallery = (FancyCoverFlow) mContentView
				.findViewById(R.id.browse_fragment_imagedetails);
		m_gallery.setAdapter(new Gallery3DAdapter(catalog, m_gallery));
		m_gallery.setOnItemClickListener((OnItemClickListener) this);
		m_gallery.setOnItemSelectedListener((OnItemSelectedListener) this);
	}

	public void changeContent(int vIndex) {
		// mTextView.setText(Integer.toString(vIndex));
	}

	public void onItemClick(AdapterView<?> arg0, View v_OnClickedItemView,
			int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	public void onItemSelected(AdapterView<?> arg0, View v_OnSelectedView,
			int arg2, long arg3) {
		Dish dish = (Dish) ((Gallery3DAdapter) (arg0.getAdapter()))
				.getItem(arg2);
		m_addOrderButtonOnClickListener.setCurrentSelectedDishId(dish.getId());
		m_DetailTextView.setText(dish.getRemarks());
		m_nameTextView.setText(dish.getName());
		m_priceTextView.setText("￥" + dish.getPrice());
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
