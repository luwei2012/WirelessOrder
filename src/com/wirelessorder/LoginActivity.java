package com.wirelessorder;

import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wirelessorder.entity.Table;
import com.wirelessorder.entity.User;
import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.SysUtil;
import com.wirelessorder.widget.MyProgressDialog;

public class LoginActivity extends Activity {

	// private LoginHandler loginHandler;
	private MyProgressDialog progressDialog;
	private EditText account;
	private EditText password;
	private ViewPager viewPager;
	private NumberPickerAdapter adapter;
	// private int isBinded = 0;
	public static final int LOGIN_SUCCESS = 0;
	public static final int LOGIN_ERROR = 1;
	public static final int TIME_OUT = 2;
	public static final int UNKNOW_ERROR = 3;
	public static final int GO_REGISTER = 4;
	private List<Table> tables = MyApplication.getInstance().tables;

	// private Boolean isPush = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		MyApplication.getInstance().activityList.add(this);
		setContentView(R.layout.activity_login);
		account = (EditText) this.findViewById(R.id.account);
		password = (EditText) this.findViewById(R.id.password);
		viewPager = (ViewPager) this.findViewById(R.id.viewpager);
		adapter = new NumberPickerAdapter(this);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				MyApplication.getInstance().tableNum = tables.get(arg0).getId();
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		int size = MyApplication.getInstance().tables.size();
		viewPager.setCurrentItem(size, true);
		MyApplication.getInstance().tableNum = MyApplication.getInstance().tables
				.get(size == 0 ? 0 : size - 1).getId();

		Boolean debugBoolean = MyApplication.getInstance()
				.getBooleanGlobalData("isDebug", BuildConfig.DEBUG);
		MyApplication.getInstance().isDebug = debugBoolean;
		progressDialog = new MyProgressDialog(this,
				R.style.CustomProgressDialog);
		progressDialog.setText("正在登陆......");

		String accString = MyApplication.getInstance().getLoginData("account",
				null);
		String passString = MyApplication.getInstance().getLoginData(
				"password", null);
		if (accString != null && passString != null) {
			account.setText(accString);
			password.setText(passString);
		}

	}

	// 这里用来接受退出程序的指令

	protected void onStart() {
		int flag = getIntent().getIntExtra("flag", 0);
		if (flag == SysUtil.EXIT_APPLICATION) {
			finish();
		} else if (flag == SysUtil.LOGOUT_APPLICATION) {

		} else if (flag == SysUtil.TIME_OUT) {
			Toast toast = Toast.makeText(LoginActivity.this, "连接超时，请重新登陆！",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		super.onStart();
	}

	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}

	public void onPause() {
		super.onPause();
	}

	// 登录按钮事件
	public void loginListener(View arg0) {
		MyApplication.getInstance().setBooleanGlobalData("isDebug",
				MyApplication.getInstance().isDebug);
		if (validateInput()) {
			if (progressDialog != null) {
				progressDialog.show();
			} else {
				progressDialog = new MyProgressDialog(LoginActivity.this,
						R.style.CustomProgressDialog);
				progressDialog.setText("正在登陆......");
				progressDialog.show();
			}
			User.login(account.getText().toString(), password.getText()
					.toString(), new Callback<String>() {

				@Override
				public void excute(String t) {
					// TODO Auto-generated method stub
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					try {
						JSONObject jsonObject = new JSONObject(t);
						if (jsonObject.getInt("result") == 1) {
							Table.takeTable(new Callback<String>() {

								@Override
								public void excute(String t) {
									// TODO Auto-generated method stub
									// 跳转到主界面
									JSONObject jsonObject;
									try {
										jsonObject = new JSONObject(t);
										if (jsonObject.getInt("result") == 1) {
											LoginActivity.this
													.startActivity(new Intent(
															LoginActivity.this,
															MainActivity.class));
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							});
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
	}

	private boolean validateInput() {
		String msg = null;
		boolean result = true;

		if (account.getText().toString().equals("")) {
			msg = "账号不能为空！";
			result = false;
		} else if (password.getText().toString().equals("")) {
			msg = "密码不能为空！";
			result = false;
		}

		if (!result) {

			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage(msg);
			builder.setTitle("提示");
			builder.setPositiveButton("返回", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();

		}

		return result;
	}

	// add by luwei
	// 工具函数，用来得到android设备的唯一标识ID
	public String getAndroid_udid() {
		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - MyApplication.touchTime) >= MyApplication.waitTime) {
				Toast.makeText(LoginActivity.this, "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				MyApplication.touchTime = currentTime;
			} else {
				SysUtil.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	public static class NumberPickerAdapter extends PagerAdapter {
		private Context context;

		public NumberPickerAdapter(Context context) {
			super();
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return MyApplication.getInstance().tables.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			Table table = MyApplication.getInstance().tables.get(position);
			View view = ((Activity) context).getLayoutInflater().inflate(
					R.layout.number_page, null);
			TextView textView = (TextView) view;
			textView.setText("" + table.getNumber());
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			if (object != null) {
				container.removeView((View) object);
			}
		}

	}
}
