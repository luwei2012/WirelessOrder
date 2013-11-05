package com.wirelessorder.entity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.JsonUtil;

@Table(name = "menu")
public class Menu {
	@Id(column = "menu_id")
	private int menu_id;
	private int id;
	private int price;
	private float salse;
	private int status;
	private int table_id;
	@Transient
	private List<DishMenu> dish_menus;

	public Menu() {
		super();
		this.dish_menus = new ArrayList<DishMenu>();
		// TODO Auto-generated constructor stub
	}

	public Menu(int menu_id, int id, int price, float salse, int status,
			int table_id, List<DishMenu> dish_menus) {
		super();
		this.menu_id = menu_id;
		this.id = id;
		this.price = price;
		this.salse = salse;
		this.status = status;
		this.table_id = table_id;
		this.dish_menus = dish_menus;
	}

	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public float getSalse() {
		return salse;
	}

	public void setSalse(float salse) {
		this.salse = salse;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}

	public List<DishMenu> getDish_menus() {
		return dish_menus;
	}

	public void setDish_menus(List<DishMenu> dish_menus) {
		this.dish_menus = dish_menus;
	}

	public static void addDish(final int dish_id, final int amount,
			final String remarks, final Callback<String> callback) {
		// 首先检查我们是否有一个菜单的记录可以用来存储菜单
		if (MyApplication.getInstance().menu.getStatus() == 1) {
			// 说明该菜单已经结账，无法再改变，需要重新创建一个
			com.wirelessorder.entity.Table.takeTable(new Callback<String>() {

				@Override
				public void excute(String t) {
					// TODO Auto-generated method stub
					try {
						JSONObject jsonObject = new JSONObject(t);
						if (jsonObject.getInt("result") == 1) {
							// 记录服务器返回的菜单号
							insertDish(dish_id, amount, remarks, callback);
						} else {
							// 桌号被占用，退出登陆，返回登陆界面
							jsonObject.put("flag", true);
							callback.excute(jsonObject.toString());
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		} else {
			insertDish(dish_id, amount, remarks, callback);
		}

	}

	public static void insertDish(int dish_id, int amount, String remarks,
			final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table/add_dish";
		AjaxParams params = new AjaxParams();
		params.put("dish_id", dish_id + "");
		params.put("menu_id", MyApplication.getInstance().menu.getId() + "");
		params.put("amount", amount + "");
		params.put("remarks", remarks);
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						DishMenu dishMenu = (DishMenu) JsonUtil.json2object(
								jsonObject.getString("dish_menu"),
								new TypeReference<DishMenu>() {
								});
						FinalDb db = MyApplication.getInstance().db;
						List<DishMenu> oldDishMenus = (List<DishMenu>) db
								.findAllByWhere(DishMenu.class, "id="
										+ dishMenu.getId());
						if (oldDishMenus == null || oldDishMenus.size() == 0) {
							db.saveBindId(dishMenu);
						} else {
							dishMenu.setDish_menu_id(oldDishMenus.get(0)
									.getDish_menu_id());
							db.update(dishMenu);
						}
						getMenu(callback);
					} else {
						Toast.makeText(
								MyApplication.getInstance()
										.getApplicationContext(),
								jsonObject.getString("message"),
								Toast.LENGTH_SHORT).show();
						if (callback != null) {
							callback.excute(t);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (callback != null) {
						callback.excute(t);
					}
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

	public static void removeDish(int dish_id, int amount,
			final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table/remove_dish";
		AjaxParams params = new AjaxParams();
		params.put("dish_id", dish_id + "");
		params.put("menu_id", MyApplication.getInstance().menu.getId() + "");
		params.put("amount", amount + "");
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						DishMenu dishMenu = (DishMenu) JsonUtil.json2object(
								jsonObject.getString("dish_menu"),
								new TypeReference<DishMenu>() {
								});
						FinalDb db = MyApplication.getInstance().db;
						List<DishMenu> oldDishMenus = (List<DishMenu>) db
								.findAllByWhere(DishMenu.class, "id="
										+ dishMenu.getId());
						if (oldDishMenus != null && oldDishMenus.size() != 0) {
							dishMenu.setDish_menu_id(oldDishMenus.get(0)
									.getDish_menu_id());
							if (dishMenu.getAmount() > 0) {
								db.update(dishMenu);
							} else {
								db.delete(dishMenu);
							}

						}
						getMenu(callback);
					} else {
						Toast.makeText(
								MyApplication.getInstance()
										.getApplicationContext(),
								jsonObject.getString("message"),
								Toast.LENGTH_SHORT).show();
						if (callback != null) {
							callback.excute(t);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (callback != null) {
						callback.excute(t);
					}
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

	public static void checkOut(final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table/check_out?menu_id="
				+ MyApplication.getInstance().menu.getId();
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						MyApplication.getInstance().menu.setPrice(jsonObject
								.getInt("price"));
						MyApplication.getInstance().menu.status = 1;
						FinalDb db = MyApplication.getInstance().db;
						db.update(MyApplication.getInstance().menu);
						MyApplication.getInstance().menu.setDish_menus(null);
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
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(
						MyApplication.getInstance().getApplicationContext(),
						strMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void getMenu(final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table/dish_list";
		AjaxParams params = new AjaxParams();
		params.put("table_id", MyApplication.getInstance().menu.getTable_id()
				+ "");
		params.put("menu_id", MyApplication.getInstance().menu.getId() + "");
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						Menu menu = (Menu) JsonUtil.json2object(
								jsonObject.getString("menu"),
								new TypeReference<Menu>() {
								});
						FinalDb db = MyApplication.getInstance().db;
						List<Menu> oldMenus = (List<Menu>) db.findAllByWhere(
								Menu.class, "id=" + menu.getId());
						if (oldMenus == null || oldMenus.size() == 0) {
							db.saveBindId(menu);
						} else {
							menu.setMenu_id(oldMenus.get(0).getMenu_id());
							db.update(menu);
						}
						MyApplication.getInstance().menu = menu;
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
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				Toast.makeText(
						MyApplication.getInstance().getApplicationContext(),
						strMsg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static void verify_menu(final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_table/verify_menu?menu_id="
				+ MyApplication.getInstance().menu.getId();
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(t);
					if (jsonObject.getInt("result") == 1) {
						MyApplication.getInstance().menu.setStatus(2);
						FinalDb db = MyApplication.getInstance().db;
						db.update(MyApplication.getInstance().menu);
					}
					Toast.makeText(
							MyApplication.getInstance().getApplicationContext(),
							jsonObject.getString("message"), Toast.LENGTH_SHORT)
							.show();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
}
