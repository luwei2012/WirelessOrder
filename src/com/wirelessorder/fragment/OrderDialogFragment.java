package com.wirelessorder.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.wirelessorder.R;
import com.wirelessorder.adapter.OrderListAdapter;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;

public class OrderDialogFragment extends DialogFragment {

	private ImageView m_closeButton;
	private Button handinButton;
	OrderListAdapter listAdapter;
	private ListView listView;
	int mNum;

	public static OrderDialogFragment newInstance(int num) {
		OrderDialogFragment orderDialogFragment = new OrderDialogFragment();
		Bundle args = new Bundle();
		args.putInt("num", num);
		orderDialogFragment.setArguments(args);
		return orderDialogFragment;
	}

	public void notifyDataSetChanged() {
		listAdapter.notifyDataSetChanged();
		if (listAdapter.getCount() <= 0) {
			handinButton.setVisibility(View.GONE);
		} else {
			handinButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mNum = getArguments().getInt("num");
		int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
		switch ((mNum) % 4) {
		case 1:
			style = DialogFragment.STYLE_NO_TITLE;
			break;
		case 2:
			style = DialogFragment.STYLE_NO_FRAME;
			break;
		case 3:
			style = DialogFragment.STYLE_NO_INPUT;
			break;
		case 0:
			style = DialogFragment.STYLE_NORMAL;
			break;
		}
		switch ((mNum) % 4) {
		case 1:
			theme = R.style.MyDialog;
			break;
		case 2:
			theme = R.style.MyDialog;
			break;
		case 3:
			theme = R.style.MyDialog;
			break;
		case 0:
			theme = R.style.MyDialog;
			break;
		}
		setStyle(style, theme);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View listFragmentView = inflater.inflate(
				R.layout.order_browse_fragment, container, false);
		m_closeButton = (ImageView) listFragmentView
				.findViewById(R.id.order_close_button);
		m_closeButton.setOnClickListener(new OrderCloseButtonOnClickListener());
		listView = (ListView) listFragmentView.findViewById(android.R.id.list);
		listView.setSelector(R.drawable.selector_list_item);
		handinButton = (Button) listFragmentView
				.findViewById(R.id.menu_handin_menu_button);

		handinButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				com.wirelessorder.entity.Menu menu = MyApplication
						.getInstance().menu;
				if (menu == null || menu.getDish_menus() == null
						|| menu.getDish_menus().size() == 0) {
					Toast.makeText(getActivity(), "您尚未选中任何菜品！",
							Toast.LENGTH_SHORT).show();
				} else {
					// 网络请求，确认订单
					com.wirelessorder.entity.Menu
							.verify_menu(new Callback<String>() {

								@Override
								public void excute(String t) {
									// TODO Auto-generated method stub
									FragmentTransaction transaction = getActivity()
											.getFragmentManager()
											.beginTransaction();
									transaction
											.remove(OrderDialogFragment.this);
									transaction.commit();
								}
							});
				}
			}
		});

		listAdapter = new OrderListAdapter(getActivity(),
				R.layout.order_item_view, listView);
		if (listAdapter.getCount() <= 0) {
			handinButton.setVisibility(View.GONE);
		} else {
			handinButton.setVisibility(View.VISIBLE);
		}
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int dishID = (int) listAdapter.getItemId(position);
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag(
						"orderadjustment");
				if (prev != null) {
					ft.remove(prev);
				}

				// Create and show the dialog.
				DialogFragment newFragment = OrderAdjustmentFragment
						.newInstance(dishID, position);
				newFragment.show(ft, "orderadjustment");
			}
		});
		return listFragmentView;
	}

	public class OrderCloseButtonOnClickListener implements OnClickListener {

		public void onClick(View v) {
			com.wirelessorder.entity.Menu menu = MyApplication.getInstance().menu;
			if (menu != null && menu.getDish_menus() != null
					&& menu.getDish_menus().size() != 0
					&& menu.getStatus() == 0) {
				Toast.makeText(getActivity(), "提交订单后您的菜单才会生效！",
						Toast.LENGTH_SHORT).show();
			}
			FragmentTransaction transaction = getActivity()
					.getFragmentManager().beginTransaction();
			transaction.remove(OrderDialogFragment.this);
			transaction.commit();
		}
	}

}
