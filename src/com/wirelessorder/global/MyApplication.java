package com.wirelessorder.global;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.wirelessorder.BuildConfig;
import com.wirelessorder.R;
import com.wirelessorder.entity.DishStyle;
import com.wirelessorder.entity.DishType;
import com.wirelessorder.entity.Menu;
import com.wirelessorder.entity.Table;
import com.wirelessorder.entity.User;

public class MyApplication extends Application {
	private static MyApplication instance;
	public User user;
	public List<Activity> activityList;
	public Boolean isDebug = BuildConfig.DEBUG;
	public static long touchTime = 0;
	public static long waitTime = 2000;
	public static Boolean isNetworkAvailable = false;
	public static long push_time_limit = 30000;
	public FinalHttp http;
	public Menu menu;
	public int tableNum;
	public List<Table> tables;
	public List<DishType> dishTypes;
	public List<DishStyle> dishStyles;
	public FinalDb db;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		user = new User();
		activityList = new LinkedList<Activity>();
		instance = this;
		Boolean debugBoolean = getBooleanGlobalData("isDebug",
				BuildConfig.DEBUG);
		tableNum = getLoginData("table", -1);
		isDebug = debugBoolean;
		http = new FinalHttp(this);
		menu = new Menu();
		tables = new ArrayList<Table>();
		dishTypes = new ArrayList<DishType>();
		dishStyles = new ArrayList<DishStyle>();
		db = FinalDb.create(this, true);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}

	public String getString() {
		if (!isDebug) {
			return getString(R.string.host);
		} else {
			return getString(R.string.debug);
		}
	}

	public String getStringGlobalData(String key, String defaultValue) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"TempGlobalData", Context.MODE_PRIVATE);
		return sharedPreferences.getString(getString() + key, defaultValue);
	}

	public void setStringGlobalData(String key, String value) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"TempGlobalData", Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(getString() + key, value).commit();
	}

	public Boolean getBooleanGlobalData(String key, Boolean defaultValue) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"TempGlobalData", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public void setBooleanGlobalData(String key, Boolean value) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"TempGlobalData", Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public void removeTempGlobalData(String key) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				"TempGlobalData", Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(getString() + key).commit();
	}

	public String getLoginData(String key, String defaultValue) {
		SharedPreferences sharedPreferences = getSharedPreferences("UserData",
				Context.MODE_PRIVATE);
		return sharedPreferences.getString(getString() + key, defaultValue);
	}

	public void setLoginData(String key, String value) {
		SharedPreferences sharedPreferences = getSharedPreferences("UserData",
				Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(getString() + key, value).commit();
	}

	public int getLoginData(String key, int defaultValue) {
		SharedPreferences sharedPreferences = getSharedPreferences("UserData",
				Context.MODE_PRIVATE);
		return sharedPreferences.getInt(getString() + key, defaultValue);
	}

	public void setLoginData(String key, int value) {
		SharedPreferences sharedPreferences = getSharedPreferences("UserData",
				Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(getString() + key, value).commit();
	}

	public void removeLoginData(String key) {
		SharedPreferences sharedPreferences = getSharedPreferences("UserData",
				Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(getString() + key).commit();
	}

	public static MyApplication getInstance() {
		return instance;
	}

}