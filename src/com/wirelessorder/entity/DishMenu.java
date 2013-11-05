package com.wirelessorder.entity;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.annotation.sqlite.Transient;

@Table(name = "dish_menu")
public class DishMenu {
	@Id(column = "dish_menu_id")
	private int dish_menu_id;
	private int id;
	private int dish_id;
	private int menu_id;
	private int amount;
	private String remarks;
	@Transient
	private Dish dish;
	@Transient
	private Menu menu;

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public int getDish_menu_id() {
		return dish_menu_id;
	}

	public void setDish_menu_id(int dish_menu_id) {
		this.dish_menu_id = dish_menu_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDish_id() {
		return dish_id;
	}

	public void setDish_id(int dish_id) {
		this.dish_id = dish_id;
	}

	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void copy(DishMenu dishMenu) {
		dish_menu_id = dishMenu.getDish_menu_id();
		id = dishMenu.getId();
		dish_id = dishMenu.getDish_id();
		menu_id = dishMenu.getMenu_id();
		amount = dishMenu.getAmount();
		remarks = dishMenu.getRemarks();
		dish = dishMenu.getDish();
		menu = dishMenu.getMenu();
	}
}
