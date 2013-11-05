package com.wirelessorder.entity;

import java.util.Iterator;
import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Transient;
import net.tsz.afinal.http.AjaxCallBack;

import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.JsonUtil;

public class Table {
	@Id(column = "table_id")
	private int table_id;
	private int id;
	private String name;
	private int number;
	private int size;
	private int status;
	@Transient
	private List<Menu> menus;

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public static void getTableList(final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table";
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, new AjaxCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				List<Table> tables = (List<Table>) JsonUtil.json2object(t,
						new TypeReference<List<Table>>() {
						});
				FinalDb db = MyApplication.getInstance().db;
				for (Iterator<Table> iterator = tables.iterator(); iterator
						.hasNext();) {
					Table table = (Table) iterator.next();
					List<Table> oldTables = db.findAllByWhere(Table.class,
							"id=" + table.getId());
					if (oldTables == null || oldTables.size() == 0) {
						db.saveBindId(table);
					} else {
						table.setTable_id(oldTables.get(0).getTable_id());
						db.update(table);
					}
					if (table.status != 0) {
						iterator.remove();
					}
				}
				MyApplication.getInstance().tables = tables;
				if (callback != null) {
					callback.excute(t);
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(
						MyApplication.getInstance().getApplicationContext(),
						strMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void takeTable(final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table/order?table_id="
				+ MyApplication.getInstance().tableNum;
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, new AjaxCallBack<String>() {
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
						// 记录服务器返回的菜单号
						
						MyApplication.getInstance().menu = (Menu) JsonUtil
								.json2object(jsonObject.getString("menu"),
										new TypeReference<Menu>() {
										});
						FinalDb db = MyApplication.getInstance().db;
						Menu menu = MyApplication.getInstance().menu;
						List<Menu> oldMenus = db.findAllByWhere(Menu.class,
								"id=" + menu.getId());
						if (oldMenus == null || oldMenus.size() == 0) {
							db.saveBindId(menu);
						} else {
							menu.setMenu_id(oldMenus.get(0).getMenu_id());
							db.update(menu);
						}
						MyApplication.getInstance().setLoginData("table", MyApplication.getInstance().tableNum);
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
