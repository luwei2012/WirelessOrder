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

@Table(name = "dish_type")
public class DishType {
	@Id(column = "dish_type_id")
	private int dish_type_id;
	private int id;
	private String describe;
	private String name;
	@Transient
	private List<Dish> dishes;

	public int getDish_type_id() {
		return dish_type_id;
	}

	public void setDish_type_id(int dish_type_id) {
		this.dish_type_id = dish_type_id;
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

	public static void getDishTypes(final Callback<String> callback) {
		String url = MyApplication.getInstance().getString()
				+ "/mobile/m_dish/dish_type_list";
		MyApplication.getInstance().http.get(url, new AjaxCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				List<DishType> dishTypes = (List<DishType>) JsonUtil
						.json2object(t, new TypeReference<List<DishType>>() {
						});
				FinalDb db = MyApplication.getInstance().db;
				for (Iterator<DishType> iterator = dishTypes.iterator(); iterator
						.hasNext();) {
					DishType dishType = (DishType) iterator.next();
					List<DishType> oldDishTypes = db.findAllByWhere(
							DishType.class, "id=" + dishType.getId());
					if (oldDishTypes == null || oldDishTypes.size() == 0) {
						db.saveBindId(dishType);
					} else {
						dishType.setDish_type_id(oldDishTypes.get(0)
								.getDish_type_id());
						db.update(dishType);
					}
					if (dishType.getDishes() != null
							&& dishType.getDishes().size() > 0) {
						for (Dish dish : dishType.getDishes()) {
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
				MyApplication.getInstance().dishTypes = dishTypes;
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
						"获取菜单列表失败，请重试！", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
