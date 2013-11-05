package com.wirelessorder.entity;

import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;
import net.tsz.afinal.http.AjaxCallBack;

import org.codehaus.jackson.type.TypeReference;

import android.widget.Toast;

import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.JsonUtil;

@Table(name = "dish")
public class Dish {
	@Id(column = "dish_id")
	private int dish_id;
	private int id;
	private int dish_style_id;
	private int dish_type_id;
	private int cost_time;
	private int count;
	private String imageUrl;
	private String name;
	private int price;
	private String remarks;
	private float sales;
	private int status;
	@Transient
	private List<DishMenu> dish_menus;

	public int getDish_id() {
		return dish_id;
	}

	public void setDish_id(int dish_id) {
		this.dish_id = dish_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDish_style_id() {
		return dish_style_id;
	}

	public void setDish_style_id(int dish_style_id) {
		this.dish_style_id = dish_style_id;
	}

	public int getDish_type_id() {
		return dish_type_id;
	}

	public void setDish_type_id(int dish_type_id) {
		this.dish_type_id = dish_type_id;
	}

	public int getCost_time() {
		return cost_time;
	}

	public void setCost_time(int cost_time) {
		this.cost_time = cost_time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public float getSales() {
		return sales;
	}

	public void setSales(float sales) {
		this.sales = sales;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<DishMenu> getDish_menus() {
		return dish_menus;
	}

	public void setDish_menus(List<DishMenu> dish_menus) {
		this.dish_menus = dish_menus;
	}

	public static void getDishes(final Callback<String> callback) {
		// TODO Auto-generated method stub
		String url = MyApplication.getInstance().getString() + "/mobile/m_dish";
		FinalHttp http = MyApplication.getInstance().http;
		http.get(url, new AjaxCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				List<Dish> dishes = (List<Dish>) JsonUtil.json2object(t,
						new TypeReference<List<Dish>>() {
						});
				FinalDb db = MyApplication.getInstance().db;
				for (Dish dish : dishes) {
					List<Dish> oldDishes = db.findAllByWhere(Dish.class, "id="
							+ dish.getId());
					if (oldDishes == null || oldDishes.size() == 0) {
						db.saveBindId(dish);
					} else {
						dish.setDish_id(oldDishes.get(0).getDish_id());
						db.update(dish);
					}
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
