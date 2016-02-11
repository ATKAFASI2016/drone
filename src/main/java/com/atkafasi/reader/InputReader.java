package com.atkafasi.reader;

import com.atkafasi.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class InputReader {

    public static void main(String[] args) {
        InputReader.readFile("busy_day.in");
    }

    public static Repository readFile(String fileName) {
        Repository repository = null;
        try {
            String file = InputReader.class.getClassLoader().getResource(fileName).getFile();
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            sCurrentLine = bufferedReader.readLine();
            String[] strings = sCurrentLine.trim().split(" ");

            // SYSTEM SETTINGS READ
            if (strings.length == 5) {
                repository = new Repository(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                        Integer.parseInt(strings[3]), Integer.parseInt(strings[4]));
            }

            // PRODUCT TYPES READ
            sCurrentLine = bufferedReader.readLine();
            int productTypeCount = Integer.parseInt(sCurrentLine.trim());
            sCurrentLine = bufferedReader.readLine();
            List<Integer> tempList = getIntListFromString(sCurrentLine);
            repository.setProductTypes(tempList);

            // WAREHOUSE READ
            sCurrentLine = bufferedReader.readLine();
            int wareHouseCount = Integer.parseInt(sCurrentLine.trim());
            List<Warehouse> warehouseList = new ArrayList<Warehouse>();
            for (int i = 0; i < wareHouseCount; i++) {
                sCurrentLine = bufferedReader.readLine();
                tempList = getIntListFromString(sCurrentLine);
                Location warehouseLocaton = new Location(tempList.get(0), tempList.get(1));
                sCurrentLine = bufferedReader.readLine();
                tempList = getIntListFromString(sCurrentLine);
                Warehouse warehouse = new Warehouse(tempList, warehouseLocaton);
                warehouseList.add(warehouse);
            }
            repository.setWarehouseList(warehouseList);

            // ORDER READ
            sCurrentLine = bufferedReader.readLine();
            int orderCount = Integer.parseInt(sCurrentLine.trim());
            List<Order> orderList = new ArrayList<Order>();
            for (int i = 0; i < orderCount; i++) {
                sCurrentLine = bufferedReader.readLine();
                tempList = getIntListFromString(sCurrentLine);
                Location orderLocation = new Location(tempList.get(0), tempList.get(1));
                sCurrentLine = bufferedReader.readLine();

                // Read Items
                int itemsCountInTheOrder = Integer.parseInt(sCurrentLine.trim());
                sCurrentLine = bufferedReader.readLine();
                tempList = getIntListFromString(sCurrentLine);
                List<Item> itemsInTheOrder = new ArrayList<Item>();
                for (Integer itemType : tempList) {
                    int itemWeight = repository.getItemWeightByType(itemType);
                    Item item = new Item(itemType, itemWeight);
                    itemsInTheOrder.add(item);
                }
                Order order = new Order(orderLocation, itemsInTheOrder);
                orderList.add(order);
            }
            repository.setOrderList(orderList);

//            repository.isValid(productTypeCount, wareHouseCount, orderCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return repository;
    }

    private static List<Integer> getIntListFromString(String sCurrentLine) {
        List<Integer> result = new ArrayList<Integer>();
        String[] strings = sCurrentLine.trim().split(" ");
        for (String string : strings) {
            result.add(Integer.parseInt(string));
        }
        return result;
    }
}
