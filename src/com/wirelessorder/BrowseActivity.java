package com.wirelessorder;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wirelessorder.fragment.BrowseDetailDetailFragment;
import com.wirelessorder.fragment.BrowseDetailListViewFragment;
import com.wirelessorder.fragment.OrderDialogFragment;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.interfaces.OnDetailChangedListener;
import com.wirelessorder.util.Callback;

public class BrowseActivity extends Activity implements
		OnDetailChangedListener, ActionBar.TabListener, OnClickListener {
	private int m_currentCatalogPosition = 0;
	private String m_currentSelectedTab = new String();
	private int m_currentSelectedTabIndex = 0;
	private boolean flag = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_browse_detail);
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.addTab(bar.newTab()
				.setText(getResources().getString(R.string.start_browse))
				.setTabListener(this));
		bar.addTab(bar.newTab()
				.setText(getResources().getString(R.string.start_recommand))
				.setTabListener(this));
		bar.addTab(bar.newTab()
				.setText(getResources().getString(R.string.start_search))
				.setTabListener(this));
		bar.addTab(bar.newTab()
				.setText(getResources().getString(R.string.start_onsale))
				.setTabListener(this));

		m_currentSelectedTab = getIntent().getExtras().getString("function");
		m_currentSelectedTabIndex = parseStringCatalogtoIntIndex(m_currentSelectedTab);
		Button changeBrowseWayButton = (Button) findViewById(R.id.browse_detail_change_browse_way_list_button);
		changeBrowseWayButton.setOnClickListener(this);
		Button changeBrowseWayGalleryButton = (Button) findViewById(R.id.browse_detail_change_browse_way_gallery_button);
		changeBrowseWayGalleryButton.setOnClickListener(this);
		if (m_currentSelectedTabIndex != -1) {
			bar.setSelectedNavigationItem(m_currentSelectedTabIndex);
		}
		//

		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_USE_LOGO);
		bar.setDisplayShowHomeEnabled(true);
	}

	private int parseStringCatalogtoIntIndex(String v_catalog) {
		if (v_catalog.equals("browse")) {
			return 0;
		} else if (v_catalog.equals("recommand")) {
			return 1;
		} else if (v_catalog.equals("search")) {
			return 2;
		} else if (v_catalog.equals("onsale")) {
			return 3;
		}
		return -1;
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		String tabString = parseTabtoString(tab);
		m_currentSelectedTab = tabString;
		showDetails(0, tabString);
	}

	private String parseTabtoString(Tab v_tab) {
		switch (v_tab.getPosition()) {
		case 0:
			return "browse";
		case 1:
			return "recommand";
		case 2:
			return "search";
		case 3:
			return "onsale";
		default:
			return null;
		}
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onDetailChanged(int vIndex) {
		m_currentCatalogPosition = vIndex;
		showDetails(vIndex, m_currentSelectedTab);
	}

	private void showDetails(int vIndex, String v_Tag) {

		if (flag) {
			BrowseDetailDetailFragment details;
			if (vIndex < 0) {
				details = BrowseDetailDetailFragment.newInstance(0, v_Tag);
			} else {
				details = BrowseDetailDetailFragment.newInstance(vIndex, v_Tag);
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.Details, details);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		} else {
			BrowseDetailListViewFragment listfragment;
			if (vIndex < 0) {
				listfragment = BrowseDetailListViewFragment.newInstance(0,
						v_Tag);
			} else {
				listfragment = BrowseDetailListViewFragment.newInstance(vIndex,
						v_Tag);
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.Details, listfragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.browse_menu, menu);
		if (!(MyApplication.getInstance().menu.getDish_menus() == null)
				&& (MyApplication.getInstance().menu.getDish_menus().size() > 0)) {
			MenuItem menuCart = menu.findItem(R.id.menu_car);
			menuCart.setIcon(getResources().getDrawable(R.drawable.fullcart));
		} else {
			MenuItem menuCart = menu.findItem(R.id.menu_car);
			menuCart.setIcon(getResources().getDrawable(R.drawable.emptycart));
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_help:
			callServor();
			break;
		case R.id.menu_car:
			displayCart();
			break;
		case R.id.menu_setting:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private boolean callServor() {

		return true;
	}

	private boolean displayCart() {

		com.wirelessorder.entity.Menu.getMenu(new Callback<String>() {
			@Override
			public void excute(String t) {
				// TODO Auto-generated method stub
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag(
						"OrderDialogFragment");
				if (prev != null) {
					ft.remove(prev);
				}

				// Create and show the dialog.
				DialogFragment newFragment = OrderDialogFragment
						.newInstance(DialogFragment.STYLE_NO_TITLE);
				newFragment.show(ft, "OrderDialogFragment");
			}
		});
		return true;
	}

	public void onClick(View v) {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		if (v.getId() == R.id.browse_detail_change_browse_way_list_button) {
			BrowseDetailListViewFragment listfragment = BrowseDetailListViewFragment
					.newInstance(m_currentCatalogPosition, m_currentSelectedTab);
			transaction.replace(R.id.Details, listfragment);
			flag = false;
		} else {
			BrowseDetailDetailFragment galleryFragment = BrowseDetailDetailFragment
					.newInstance(m_currentCatalogPosition, m_currentSelectedTab);
			transaction.replace(R.id.Details, galleryFragment);
			flag = true;
		}
		transaction.commit();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (!(MyApplication.getInstance().menu.getDish_menus() == null)
				&& (MyApplication.getInstance().menu.getDish_menus().size() > 0)) {
			MenuItem menuCart = menu.findItem(R.id.menu_car);
			menuCart.setIcon(getResources().getDrawable(R.drawable.fullcart));
		} else {
			MenuItem menuCart = menu.findItem(R.id.menu_car);
			menuCart.setIcon(getResources().getDrawable(R.drawable.emptycart));
		}
		return true;
	}

	@Override
	public void onAddClicked() {
		// TODO Auto-generated method stub
		invalidateOptionsMenu();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		OrderDialogFragment prev = (OrderDialogFragment) getFragmentManager()
				.findFragmentByTag("OrderDialogFragment");
		if (prev != null) {
			prev.notifyDataSetChanged();
		}
	}

}
