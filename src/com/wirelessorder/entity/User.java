package com.wirelessorder.entity;

import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.JsonUtil;

@Table(name = "user")
public class User {
	@Id(column = "user_id")
	private int user_id;
	private int id;
	private String account;
	private String name;
	private String password;
	private String phone;
	private String role;

	public String getAccount() {
		return account;
	}

	public int getId() {
		return id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// 登陆接口
	public static void login(final String accString, final String passString,
			final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/login/sign";
		AjaxParams params = new AjaxParams();
		params.put("account", accString);
		params.put("password", passString);
		FinalHttp http = MyApplication.getInstance().http;
		http.post(url, params, new AjaxCallBack<String>() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				// 网络请求成功，处理返回信息
				try {
					JSONObject jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						// 保存登录的用户名和密码
						MyApplication.getInstance().setLoginData("account",
								accString);
						MyApplication.getInstance().setLoginData("password",
								passString);
						User user = (User) JsonUtil.json2object(
								jsonObject.getString("user"),
								new TypeReference<User>() {
								});
						FinalDb db = MyApplication.getInstance().db;
						List<User> oldUsers = db.findAllByWhere(User.class,
								"id=" + user.getId());
						if (oldUsers == null || oldUsers.size() == 0) {
							db.saveBindId(user);
						} else {
							user.setUser_id(oldUsers.get(0).getUser_id());
							db.update(user);
						}
					} else {
						Toast.makeText(
								MyApplication.getInstance()
										.getApplicationContext(),
								jsonObject.getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (callback != null) {
					callback.excute(t);
				}
			}

			@Override
			// 网络请求失败
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(
						MyApplication.getInstance().getApplicationContext(),
						strMsg, Toast.LENGTH_SHORT).show();
			}

		});
	}
}
