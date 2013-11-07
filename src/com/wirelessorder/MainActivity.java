package com.wirelessorder;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wirelessorder.entity.DishType;
import com.wirelessorder.entity.Menu;
import com.wirelessorder.fragment.OrderDialogFragment;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.interfaces.OnDetailChangedListener;
import com.wirelessorder.util.Callback;

public class MainActivity extends Activity implements OnDetailChangedListener {

	private Intent m_startActivityIntent = new Intent();;

	private Button m_startBrowseButton;
	private Button m_startOnsaleButton;
	private Button m_startReconmmendButton;

	private Button m_startHelpButton;
	private Button m_startEntertainmentButton;
	private Button m_startAccountButton;
	private RelativeLayout m_cartParent;

	private ArrayList<Button> m_buttonCollection;

	private Animation m_startButtonAnimation;
	private Animation m_startCartAnimation;
	private Animation m_startServeAnimation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		MyApplication.getInstance().activityList.add(this);
		setContentView(R.layout.main_interface_start_layout);
		m_startButtonAnimation = AnimationUtils.loadAnimation(this,
				R.anim.start_button_animationset);
		m_startCartAnimation = AnimationUtils.loadAnimation(this,
				R.anim.start_cart_selected_animation);
		m_startServeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.start_button_animationset);

		m_buttonCollection = new ArrayList<Button>();
		m_startBrowseButton = (Button) findViewById(R.id.start_brower_button);
		m_startOnsaleButton = (Button) findViewById(R.id.start_onsale_button);
		m_startReconmmendButton = (Button) findViewById(R.id.start_recomand_button);

		m_startHelpButton = (Button) findViewById(R.id.start_help_button);
		m_startEntertainmentButton = (Button) findViewById(R.id.start_entertainment_button);
		m_startAccountButton = (Button) findViewById(R.id.start_account_button);
		m_cartParent = (RelativeLayout) findViewById(R.id.start_cart_parent_relativeLayout);

		m_buttonCollection.add(m_startBrowseButton);
		m_buttonCollection.add(m_startOnsaleButton);
		m_buttonCollection.add(m_startReconmmendButton);
		m_buttonCollection.add(m_startHelpButton);
		m_buttonCollection.add(m_startEntertainmentButton);
		m_buttonCollection.add(m_startAccountButton);

		m_startBrowseButton.setOnClickListener(new StartBrowseButtonListener());
		m_startOnsaleButton.setOnClickListener(new StartOnsaleButtonListener());
		m_startReconmmendButton
				.setOnClickListener(new StartRecommandButtonListener());

		m_startHelpButton.setOnClickListener(new StartHelpButtonListener());
		m_startEntertainmentButton
				.setOnClickListener(new StartEntertainmentButtonListener());
		m_startAccountButton
				.setOnClickListener(new StartAccountButtonListener());

		m_cartParent.setOnClickListener(new StartCartLayoutListener());
	}

	/*
	 * this is the lefttop button's listener
	 */

	@Override
	public void onResume() {
		super.onResume();
		ImageView cartImage = (ImageView) m_cartParent
				.findViewById(R.id.start_cart_image);

		if (MyApplication.getInstance().menu.getStatus() != 1
				&& !(MyApplication.getInstance().menu.getDish_menus() == null)
				&& (MyApplication.getInstance().menu.getDish_menus().size() > 0)) {
			cartImage.setImageDrawable(getResources().getDrawable(
					R.drawable.fullcart));
		} else {
			cartImage.setImageDrawable(getResources().getDrawable(
					R.drawable.emptycart));
		}
		setVisiblility(View.VISIBLE);
	}

	private void setVisiblility(int v_isVisible) {
		for (Button temptButton : m_buttonCollection) {
			temptButton.setVisibility(v_isVisible);
		}

	}

	public class StartBrowseButtonListener implements OnClickListener {

		public void onClick(View v) {
			v.startAnimation(m_startButtonAnimation);
			DishType.getDishTypes(new Callback<String>() {

				@Override
				public void excute(String t) {
					// TODO Auto-generated method stub
					Bundle dataBundle = new Bundle();
					dataBundle.putString("function", "browse");
					m_startActivityIntent.replaceExtras(dataBundle);
					m_startActivityIntent.setClass(MainActivity.this,
							BrowseActivity.class);
					MainActivity.this.startActivity(m_startActivityIntent);
				}
			});
		}
	}

	public class StartRecommandButtonListener implements OnClickListener {

		public void onClick(View v) {
			v.startAnimation(m_startButtonAnimation);
			DishType.getDishRecommendTypes(new Callback<String>() {

				@Override
				public void excute(String t) {
					// TODO Auto-generated method stub
					Bundle dataBundle = new Bundle();
					dataBundle.putString("function", "recommand");
					m_startActivityIntent.replaceExtras(dataBundle);
					m_startActivityIntent.setClass(MainActivity.this,
							BrowseActivity.class);
					MainActivity.this.startActivity(m_startActivityIntent);
				}
			});

		}
	}

	@Override
	public void onBackPressed() {
		if (MyApplication.getInstance().menu != null
				&& MyApplication.getInstance().menu.getStatus() != 1) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);

			builder.setMessage("您还没有结账请先结账")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			super.onBackPressed();
		}
	}

	public class StartOnsaleButtonListener implements OnClickListener {

		public void onClick(View v) {
			v.startAnimation(m_startButtonAnimation);
			DishType.getDishOnSaleTypes(new Callback<String>() {

				@Override
				public void excute(String t) {
					// TODO Auto-generated method stub
					Bundle dataBundle = new Bundle();
					dataBundle.putString("function", "onsale");
					m_startActivityIntent.replaceExtras(dataBundle);
					m_startActivityIntent.setClass(MainActivity.this,
							BrowseActivity.class);
					MainActivity.this.startActivity(m_startActivityIntent);
				}
			});
		}
	}

	public class StartHelpButtonListener implements OnClickListener {

		public void onClick(View v) {
			v.startAnimation(m_startServeAnimation);
			// 呼叫服务员
			Toast.makeText(MainActivity.this, "当前功能暂不可用！", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public class StartEntertainmentButtonListener implements OnClickListener {

		public void onClick(View v) {
			v.startAnimation(m_startServeAnimation);
		}
	}

	public class StartAccountButtonListener implements OnClickListener {

		public void onClick(View v) {
			v.startAnimation(m_startServeAnimation);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);

			builder.setMessage("确定要结账么？结账后将不可以再点餐。如果想继续点餐请直接点餐")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// 结账
									Menu.checkOut(new Callback<String>() {

										@Override
										public void excute(String t) {
											// TODO Auto-generated method stub
											JSONObject jsonObject;
											try {
												jsonObject = new JSONObject(t);
												if (jsonObject.getInt("result") == 1) {
													onAddClicked();
													AlertDialog.Builder builder = new AlertDialog.Builder(
															MainActivity.this);
													builder.setMessage(
															"您一共消费了"
																	+ jsonObject
																			.getInt("price")
																	+ "元，谢谢惠顾！")
															.setCancelable(
																	false)
															.setPositiveButton(
																	"我有疑惑",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			// 呼叫服务员
																			Toast.makeText(
																					MainActivity.this,
																					"正在为您呼叫服务员，请耐心等待！",
																					Toast.LENGTH_SHORT)
																					.show();
																			dialog.cancel();
																		}
																	})
															.setNegativeButton(
																	"我知道了",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			dialog.cancel();
																		}
																	});
													AlertDialog alertDialog = builder
															.create();
													alertDialog.show();
												}
												Toast.makeText(
														MainActivity.this,
														jsonObject
																.getString("message"),
														Toast.LENGTH_SHORT)
														.show();

											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									});
									dialog.cancel();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	public class StartCartLayoutListener implements OnClickListener {

		@SuppressLint("CommitTransaction")
		public void onClick(View v) {
			v.startAnimation(m_startCartAnimation);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
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
	}

	@Override
	public void onDetailChanged(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddClicked() {
		// TODO Auto-generated method stub
		ImageView cartImage = (ImageView) m_cartParent
				.findViewById(R.id.start_cart_image);

		if (!(MyApplication.getInstance().menu.getDish_menus() == null)
				&& (MyApplication.getInstance().menu.getDish_menus().size() > 0)) {
			cartImage.setImageDrawable(getResources().getDrawable(
					R.drawable.fullcart));
		} else {
			cartImage.setImageDrawable(getResources().getDrawable(
					R.drawable.emptycart));
		}
		OrderDialogFragment prev = (OrderDialogFragment) getFragmentManager()
				.findFragmentByTag("OrderDialogFragment");
		if (prev != null) {
			prev.notifyDataSetChanged();
		}
	}
}
