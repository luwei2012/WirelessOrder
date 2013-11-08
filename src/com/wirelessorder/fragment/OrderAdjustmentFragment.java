package com.wirelessorder.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wirelessorder.LoginActivity;
import com.wirelessorder.R;
import com.wirelessorder.entity.Dish;
import com.wirelessorder.entity.DishMenu;
import com.wirelessorder.entity.Menu;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.interfaces.OnDetailChangedListener;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.SysUtil;
import com.wirelessorder.util.network.LRUAsyncImageLoader;

public class OrderAdjustmentFragment extends DialogFragment {

	public static OrderAdjustmentFragment newInstance(int v_dishId,
			int v_position) {
		OrderAdjustmentFragment fragment = new OrderAdjustmentFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("dish_id", v_dishId);
		bundle.putInt("num", DialogFragment.STYLE_NO_TITLE);
		bundle.putInt("orderIndex", v_position);
		fragment.setArguments(bundle);
		return fragment;
	}

	private Button m_okButton;
	private EditText m_remarkTextView, m_numEditText;
	private int m_orderIndex;
	public int mNum;
	public DishMenu dish_menu;
	private OnDetailChangedListener listener;
	public Dish dish;
	private ImageView dish_ico;
	public Callback<String> callback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mNum = getArguments().getInt("num");
		getArguments().getInt("dish_id");
		m_orderIndex = getArguments().getInt("orderIndex");
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
			theme = R.style.DialogFragment;
			break;
		case 2:
			theme = R.style.DialogFragment;
			break;
		case 3:
			theme = R.style.DialogFragment;
			break;
		case 0:
			theme = R.style.DialogFragment;
			break;
		}
		setStyle(style, theme);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View returnedView = inflater.inflate(
				R.layout.order_adjustment_fragment_layout, container, false);
		TextView dishNameTextView = (TextView) returnedView
				.findViewById(R.id.order_adjustment_fragment_dishname_textview);
		TextView dishPriceTextView = (TextView) returnedView
				.findViewById(R.id.order_adjustment_fragment_dishprice_textview);
		dish_ico = (ImageView) returnedView.findViewById(R.id.dish_ico);
		m_numEditText = (EditText) returnedView
				.findViewById(R.id.order_num_textview);
		dish_menu = MyApplication.getInstance().menu.getDish_menus().get(
				m_orderIndex);
		dish = dish_menu.getDish();
		setViewImage(dish_ico, dish.getImageUrl(), true);
		dishNameTextView.setText(dish.getName());
		dishPriceTextView.setText("￥"
				+ (int) (dish.getPrice() * dish.getSales()));
		m_numEditText.setText(dish_menu.getAmount() + "");
		m_remarkTextView = (EditText) returnedView
				.findViewById(R.id.order_adjustment_fragment_remark_edittext);
		listener = (OnDetailChangedListener) getActivity();
		m_remarkTextView.setText(dish_menu.getRemarks());

		m_okButton = (Button) returnedView
				.findViewById(R.id.order_adjustment_fragment_ok_button);

		Button cancelButton = (Button) returnedView
				.findViewById(R.id.order_adjustment_fragment_cancel_button);
		callback = new Callback<String>() {

			@Override
			public void excute(String t) {
				// TODO Auto-generated method stub
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						listener.onAddClicked();
					} else {
						// 桌号被占用，退出登陆，返回登陆界面
						if (jsonObject.has("flag")
								&& jsonObject.getBoolean("flag")) {
							// 表面申请该桌子失败
							SysUtil sysUtil = new SysUtil(getActivity());
							sysUtil.forward(LoginActivity.class);
						} else {
							// 只是一次偶然失败，或者菜品已卖完
							Toast.makeText(getActivity(),
									jsonObject.getString("message"),
									Toast.LENGTH_SHORT).show();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag(
						"orderadjustment");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.commit();
			}
		};
		m_okButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String remark = (String) (m_remarkTextView.getText().toString());
				int num = Integer.parseInt(m_numEditText.getText().toString());
				int oldNum = dish_menu.getAmount();

				if (num > oldNum) {
					// 添加
					Menu.addDish(dish.getId(), num - oldNum, remark, callback);
				} else if (num < oldNum) {
					// 减少
					Menu.removeDish(dish.getId(), oldNum - num, callback);
				} else {
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					Fragment prev = getFragmentManager().findFragmentByTag(
							"orderadjustment");
					if (prev != null) {
						ft.remove(prev);
					}
					ft.commit();
				}

			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int oldNum = dish_menu.getAmount();
				Menu.removeDish(dish.getId(), oldNum, callback);
			}
		});

		return returnedView;
	}

	public void setViewImage(final ImageView v, String url) {
		// Log.e("URL", url);
		try {
			setViewImage(v, url, false);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public void setViewImage(final ImageView v, String url,
			final Boolean needFillet) {
		try {
			Bitmap draw_pic = LRUAsyncImageLoader.getInstance(
					MyApplication.getInstance().getString()).loadBitmap(url,
					new ImageCallBack(url) {
						public void imageLoaded() {
							if (v != null) {
								if (this.bitmap.get() != null
										&& this.bitmap.get().getRowBytes() > 0) {
									v.setImageBitmap(this.bitmap.get());
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
}
