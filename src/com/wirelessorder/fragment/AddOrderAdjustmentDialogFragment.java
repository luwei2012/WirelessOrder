package com.wirelessorder.fragment;

import java.util.List;

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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wirelessorder.LoginActivity;
import com.wirelessorder.R;
import com.wirelessorder.entity.Dish;
import com.wirelessorder.entity.Menu;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.interfaces.OnDetailChangedListener;
import com.wirelessorder.listener.AddOrderButtonOnClickListener;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.ImageCallBack;
import com.wirelessorder.util.SysUtil;
import com.wirelessorder.util.network.LRUAsyncImageLoader;

public class AddOrderAdjustmentDialogFragment extends DialogFragment {
	private AddOrderButtonOnClickListener m_listener = null;
	private OnDetailChangedListener listener;
	private EditText m_numberTextView = null;
	private EditText m_remarkTextView = null;
	private Button m_okButton = null;
	private int dish_id;
	private Dish dish;
	public int mNum;
	private ImageView dish_ico;

	public static AddOrderAdjustmentDialogFragment newInstance(
			AddOrderButtonOnClickListener listener, int v_dishId) {
		AddOrderAdjustmentDialogFragment fragment = new AddOrderAdjustmentDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("dish_id", v_dishId);
		bundle.putInt("num", DialogFragment.STYLE_NO_TITLE);
		fragment.setArguments(bundle);
		fragment.setListener(listener);
		return fragment;
	}

	public AddOrderButtonOnClickListener getListener() {
		return m_listener;
	}

	public void setListener(AddOrderButtonOnClickListener m_listener) {
		this.m_listener = m_listener;
	}

	@Override
	public void onStart() {
		super.onStart();
		m_okButton.requestFocus();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mNum = getArguments().getInt("num");
		getArguments().getInt("dish_id");
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dish_id = getArguments().getInt("dish_id");
		View returnedView = inflater.inflate(
				R.layout.add_order_adjustment_dialog_fragment_layout,
				container, false);
		TextView dishNameTextView = (TextView) returnedView
				.findViewById(R.id.add_adjustment_fragment_dishname_textview);
		TextView dishPriceTextView = (TextView) returnedView
				.findViewById(R.id.add_adjustment_fragment_dishprice_textview);
		m_okButton = (Button) returnedView
				.findViewById(R.id.add_adjustment_fragment_ok_button);
		m_numberTextView = (EditText) returnedView
				.findViewById(R.id.add_num_textview);
		m_remarkTextView = (EditText) returnedView
				.findViewById(R.id.add_adjustment_fragment_remark_edittext);
		m_okButton = (Button) returnedView
				.findViewById(R.id.add_adjustment_fragment_ok_button);
		dish_ico = (ImageView) returnedView.findViewById(R.id.dish_ico);
		Button cancelButton = (Button) returnedView
				.findViewById(R.id.add_adjustment_fragment_cancel_button);
		listener = (OnDetailChangedListener) getActivity();
		List<Dish> dishes = MyApplication.getInstance().db.findAllByWhere(
				Dish.class, "id=" + dish_id);
		if (dishes != null && dishes.size() > 0) {
			dish = dishes.get(0);
		} else {
			dish = null;
		}
		if (dish != null) {
			setViewImage(dish_ico, dish.getImageUrl(), true);
			dishNameTextView.setText(dish.getName());
			dishPriceTextView.setText("￥" + (int) (dish.getPrice() * dish.getSales()));
			m_numberTextView.setText("1");
			m_remarkTextView
					.setOnFocusChangeListener(new OnFocusChangeListener() {

						public void onFocusChange(View v, boolean hasFocus) {
							EditText temptEditText = (EditText) v;
							if (hasFocus) {
								if (temptEditText
										.getText()
										.toString()
										.equals(getResources().getString(
												R.string.add_edit_text_default))) {
									temptEditText.setText("");
								}
							} else {
								if (temptEditText.getText().toString()
										.equals("")) {
									temptEditText
											.setText(getResources()
													.getString(
															R.string.add_edit_text_default));
								}
							}
						}
					});

			m_okButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					listener.onAddClicked();
					int number = Integer.parseInt((String) (m_numberTextView
							.getText().toString()));
					String remark = (String) (m_remarkTextView.getText()
							.toString());
					// 网络请求，添加菜品，成功后再添加到菜单里
					Menu.addDish(dish_id, number, remark,
							new Callback<String>() {

								@Override
								public void excute(String t) {
									// TODO Auto-generated method stub
									try {
										JSONObject jsonObject = new JSONObject(
												t);
										if (jsonObject.getInt("result") == 1) {
											// 添加成功
											listener.onAddClicked();
											FragmentTransaction ft = getFragmentManager()
													.beginTransaction();
											Fragment prev = getFragmentManager()
													.findFragmentByTag(
															"adjustment");
											if (prev != null) {
												ft.remove(prev);

											}
											ft.commit();
										} else {
											// 桌号被占用，退出登陆，返回登陆界面
											if (jsonObject.has("flag")
													&& jsonObject
															.getBoolean("flag")) {
												// 表面申请该桌子失败
												SysUtil sysUtil = new SysUtil(
														getActivity());
												sysUtil.forward(LoginActivity.class);
											} else {
												// 只是一次偶然失败，或者菜品已卖完
												Toast.makeText(
														getActivity(),
														jsonObject
																.getString("message"),
														Toast.LENGTH_SHORT)
														.show();
											}
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
				}
			});

		} else {
			Toast.makeText(getActivity(), "未找到菜品，请呼服务员解决问题，谢谢！",
					Toast.LENGTH_SHORT).show();
		}

		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				m_listener.doCancel();
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag(
						"adjustment");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.commit();
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
