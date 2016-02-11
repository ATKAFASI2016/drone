package com.atkafasi.model;

import java.util.List;

public class Warehouse {

	private List<Integer> itemInTheStoreMap;
	private Location location;

	public Warehouse(List<Integer> tempList, Location warehouseLocaton) {
		itemInTheStoreMap = tempList;
		this.location = warehouseLocaton;
	}

	public List<Integer> getItemInTheStoreMap() {
		return itemInTheStoreMap;
	}

	public void setItemInTheStoreMap(List<Integer> itemInTheStoreMap) {
		this.itemInTheStoreMap = itemInTheStoreMap;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
