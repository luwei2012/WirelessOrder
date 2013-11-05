package com.wirelessorder.entity;

import java.util.Iterator;
import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;
import net.tsz.afinal.http.AjaxCallBack;

import org.codehaus.jackson.type.TypeReference;

import android.widget.Toast;

import com.wirelessorder.global.MyApplication;
import com.wirelessorder.util.Callback;
import com.wirelessorder.util.JsonUtil;

@Table(name = "dish_style")
public class DishStyle {
	@Id(column = "dish_style_id")
	private int dish_style_id;
	private int id;
	@Transient
	private List<Dish> dishes;
	private String describe;
	private String name;

	public int getDish_style_id() {
		return dish_style_id;
	}

	public void setDish_style_id(int dish_style_id) {
		this.dish_style_id = dish_style_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Dish> getDishes() {
		return dishes;
	}

	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void getDishStyles(final Callback<String> callback) {
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_dish/dish_style_list";
		MyApplication.getInstance().http.get(url, new AjaxCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t); 
				List<DishStyle> dishStyles = (List<DishStyle>) JsonUtil
						.json2object(t, new TypeReference<List<DishStyle>>() {
						});
				FinalDb db = MyApplication.getInstance().db;
				for (Iterator<DishStyle> iterator = dishStyles.iterator(); iterator
						.hasNext();) {
					DishStyle dishStyle = (DishStyle) iterator.next();
					List<DishStyle> oldDishStyles = db.findAllByWhere(
							DishStyle.class, "id=" + dishStyle.getId());
					if (oldDishStyles == null || oldDishStyles.size() == 0) {
						db.saveBindId(dishStyle);
					} else {
						dishStyle.setDish_style_id(oldDishStyles.get(0)
								.getDish_style_id());
						db.update(dishStyle);
					}

					if (dishStyle.getDishes() != null
							&& dishStyle.getDishes().size() > 0) {
						for (Dish dish : dishStyle.getDishes()) {
							List<Dish> oldDishes = db.findAllByWhere(
									Dish.class, "id=" + dish.getId());
							if (oldDishes == null || oldDishes.size() == 0) {
								db.saveBindId(dish);
							} else {
								dish.setDish_id(oldDishes.get(0).getDish_id());
								db.update(dish);
							}
						}
					} else {
						iterator.remove();
					}
				}
				MyApplication.getInstance().dishStyles = dishStyles;
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
						"获取菜单类别失败，请重试！", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
