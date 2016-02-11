package com.atkafasi.model;

public class Item {
	private int itemType;
	private int itemWeight;

	public Item(int itemType, int itemWeight) {
		super();
		this.itemType = itemType;
		this.itemWeight = itemWeight;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public int getItemWeight() {
		return itemWeight;
	}

	public void setItemWeight(int itemWeight) {
		this.itemWeight = itemWeight;
	}

}
