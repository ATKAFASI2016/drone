package com.atkafasi.model;

import java.util.List;

public class Repository {

	private int rowsCount;
	private int columsCount;
	private int dronesCount;
	private int maxTurns;
	private int maxPayload;

	private List<Integer> productTypeWeighMap;
	private List<Warehouse> warehouseList;
	private List<Order> orderList;

	public Repository(int rowsCount, int columsCount, int dronesCount, int maxTurns, int maxPayload) {
		super();
		this.rowsCount = rowsCount;
		this.columsCount = columsCount;
		this.dronesCount = dronesCount;
		this.maxTurns = maxTurns;
		this.maxPayload = maxPayload;
	}

	public void setProductTypes(List<Integer> integerList) {
		productTypeWeighMap = integerList;
	}

	public void setWarehouseList(List<Warehouse> warehouseList) {
		this.warehouseList = warehouseList;

	}

	public int getItemWeightByType(Integer itemType) {
		return productTypeWeighMap.get(itemType);
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;

	}

	public int getRowsCount() {
		return rowsCount;
	}

	public void setRowsCount(int rowsCount) {
		this.rowsCount = rowsCount;
	}

	public int getColumsCount() {
		return columsCount;
	}

	public void setColumsCount(int columsCount) {
		this.columsCount = columsCount;
	}

	public int getDronesCount() {
		return dronesCount;
	}

	public void setDronesCount(int dronesCount) {
		this.dronesCount = dronesCount;
	}

	public int getMaxTurns() {
		return maxTurns;
	}

	public void setMaxTurns(int maxTurns) {
		this.maxTurns = maxTurns;
	}

	public int getMaxPayload() {
		return maxPayload;
	}

	public void setMaxPayload(int maxPayload) {
		this.maxPayload = maxPayload;
	}

	public List<Integer> getProductTypeWeighMap() {
		return productTypeWeighMap;
	}

	public void setProductTypeWeighMap(List<Integer> productTypeWeighMap) {
		this.productTypeWeighMap = productTypeWeighMap;
	}

	public List<Warehouse> getWarehouseList() {
		return warehouseList;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void isValid(int productTypeCount, int wareHouseCount, int orderCount) {
		System.err.println("productTypeCount:" + productTypeWeighMap.size() + ":" + productTypeCount);
		System.err.println("wareHouseCount:" + warehouseList.size() + ":" + wareHouseCount);
		System.err.println("orderCount:" + orderList.size() + ":" + orderCount);
	}
}
