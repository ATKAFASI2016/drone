package com.atkafasi.model;

import java.util.List;

public class Order {

	private Location orderLocation;
	private List<Item> items;

	public Order(Location orderLocation, List<Item> items) {
		super();
		this.orderLocation = orderLocation;
		this.items = items;
	}

	public Location getOrderLocation() {
		return orderLocation;
	}

	public void setOrderLocation(Location orderLocation) {
		this.orderLocation = orderLocation;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
