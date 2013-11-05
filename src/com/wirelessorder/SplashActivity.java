package com.wirelessorder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.wirelessorder.entity.Dish;
import com.wirelessorder.entity.DishStyle;
import com.wirelessorder.entity.DishType;
import com.wirelessorder.entity.Table;
import com.wirelessorder.entity.User;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;

public class SplashActivity extends Activity {

	public boolean netwifi = false;
	public boolean mConnectionFlag = false;
	private Handler mHandler;
	private TextView textView;
	private final int FINISH_SELF = 0;
	private final int LOGIN_INTENT = 1;
	private final int MAIN_INTENT = 2;

	// 检查网络，这里因为是pad，只检查wifi状况
	private void checkNetworkConnection() {
		netwifi = false;
		ConnectivityManager conMan = null;
		State wifi = null;
		try {
			conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			mConnectionFlag = true;
			netwifi = true;
			return;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		MyApplication.getInstance().activityList.add(this);
		mHandler = new Handler();
		setContentView(R.layout.activity_splash);
		ImageView imageView = (ImageView) this
				.findViewById(R.id.imageView_splash);
		textView = (TextView) findViewById(R.id.textView1);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		checkNetworkConnection();
	}

	private void getTableList() {
		textView.setText("正在获取餐桌信息...");
		Table.getTableList(new Callback<String>() {
			@Override
			public void excute(String t) {
				// 跳转到登陆界面
				sendMessage(LOGIN_INTENT, 0);
			}
		});
	}

	public void onResume() {
		super.onResume();
		if (mConnectionFlag) {
			// 更新菜品信息
			textView.setText("正在更新菜品信息...");
			Dish.getDishes(new Callback<String>() {

				@Override
				public void excute(String t) {
					// TODO Auto-generated method stub
					// 更新菜品类别信息
					textView.setText("更新菜品类别信息...");
					DishStyle.getDishStyles(new Callback<String>() {

						@Override
						public void excute(String t) {
							// TODO Auto-generated method stub
							// 更新菜品类型信息
							textView.setText("更新菜品类型信息...");
							DishType.getDishTypes(new Callback<String>() {

								@Override
								public void excute(String t) {
									// TODO Auto-generated method stub
									// 首先获取餐桌列表
									if (MyApplication.getInstance().tableNum < 0) {
										// 尚未选桌号，必须跳转到登陆界面,首先获取可选桌号列表
										getTableList();
									} else {
										// 已经登录过，默认使用之前的配置，直接登陆然后开桌
										login();
									}
								}
							});
						}
					});
				}
			});

		} else {
			// wifi状态不可用则推出应用
			Toast.makeText(this, "您的网络当前不可用，请先检查！", Toast.LENGTH_LONG).show();
			sendMessage(FINISH_SELF, 0);
		}
	}

	private void login() {
		// TODO Auto-generated method stub
		String accString = MyApplication.getInstance().getLoginData("account",
				null);
		String passString = MyApplication.getInstance().getLoginData(
				"password",
				null);
		if (accString != null && passString != null) {
			// 登陆信息完整则后台登陆
			textView.setText("正在验证个人信息...");
			User.login(accString, passString, new Callback<String>() {
				@Override
				// 登陆的回调函数，根据登陆接口返回的string分析登陆是否成功
				public void excute(String t) {
					// TODO Auto-generated method stub
					try {
						JSONObject jsonObject = new JSONObject(t);
						int flag = jsonObject.getInt("result");
						if (flag == 1) {
							// 登陆成功,开桌
							textView.setText("登陆成功，正在开桌...");
							textView.setText("正在验证餐桌编号是否可用...");
							Table.takeTable(new Callback<String>() {
								@Override
								public void excute(String t) {
									// TODO Auto-generated method stub
									try {
										JSONObject jsonObject = new JSONObject(
												t);
										if (jsonObject.getInt("result") == 1) {
											// 记录服务器返回的菜单号
											sendMessage(MAIN_INTENT, 0);
										} else {
											// 桌号被占用，退出登陆，返回登陆界面
											Toast.makeText(
													SplashActivity.this,
													jsonObject
															.getString("message"),
													Toast.LENGTH_SHORT).show();
											getTableList();
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});

						} else {
							// 登陆信息错误，跳转到登陆页面
							textView.setText("登陆失败，正在获取餐桌列表...");
							getTableList();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else {
			// 登陆信息不完整，需要跳到登陆界面
			getTableList();
		}

	}

	private void sendMessage(int MessageType, long postDelay) {
		if (FINISH_SELF == MessageType) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SplashActivity.this.finish();
				}
			}, postDelay);
		} else if (LOGIN_INTENT == MessageType) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// 跳转到登陆页面
					SplashActivity.this.startActivity(new Intent(
							SplashActivity.this, LoginActivity.class));
					SplashActivity.this.finish();
				}
			}, postDelay);
		} else if (MAIN_INTENT == MessageType) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method
					// stub
					SplashActivity.this.startActivity(new Intent(
							SplashActivity.this, MainActivity.class));
					SplashActivity.this.finish();
				}
			}, postDelay);
		}
	};

	public void onPause() {
		super.onPause();
	}

}
