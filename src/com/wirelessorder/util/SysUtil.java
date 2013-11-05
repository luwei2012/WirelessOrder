package com.wirelessorder.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.wirelessorder.background.service.UpdateService;
import com.wirelessorder.global.MyApplication;

public class SysUtil {
	public static final int EXIT_APPLICATION = 0x0001;
	public static final int LOGOUT_APPLICATION = 0x0002;
	public static final int TIME_OUT = 0x0003;

	private Context mContext;

	public SysUtil(Context context) {
		this.mContext = context;
	}

	public static void exit() {
		for (Activity activity : MyApplication.getInstance().activityList) {
			activity.finish();
		}
	}

	public void forward(Class<?> toContext) {

		Intent mIntent = new Intent();
		mIntent.setClass(mContext, toContext);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		mContext.startActivity(mIntent);
	}

	public void forward(String className) {
		try {
			forward(Class.forName(className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void ReadyToUpdate(String result) {
		try {
			JSONObject json = new JSONObject(result);
			Intent intent = new Intent();
			intent.putExtra("msg", json.getString("msg"));
			intent.putExtra("url", json.getString("url"));
			intent.setClass(mContext, UpdateService.class);
			mContext.startService(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
