package com.atkafasi.pseudo;

import com.atkafasi.model.Item;
import com.atkafasi.model.Repository;
import com.atkafasi.reader.InputReader;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class PseudoRunner {

    private List<Order> orders;
    private List<Warehouse> warehouses;

    public static void main(String[] args) {
        System.out.println("--------------------------------------busy_day");
        new PseudoRunner().run("busy_day.in");
        System.out.println("--------------------------------------mother_of_all_warehouses");
        new PseudoRunner().run("mother_of_all_warehouses.in");
        System.out.println("--------------------------------------redundancy");
        new PseudoRunner().run("redundancy.in");
    }

    public PseudoRunner() {
    }

    private class BaseObject {
        private int number;
        private int x = 0;
        private int y = 0;

        public BaseObject(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    private class Operation {
        //  0 L 9 2 3   (Command  to  drone 0, load at warehouse 9, products of product type 2, 3 of them)
        //  0 D 1 5 4   (Command  to  drone 0, deliver for order 1, products of product type 5, 4 of them)
        private String loadLDeliverD = "";
        private int warehouseOrOrderNumber;
        private String productType = "";
        private String numberOfProducts = "";

        public Operation(String loadLDeliverD, int warehouseOrOrderNumber, String productType, String numberOfProducts) {
            this.loadLDeliverD = loadLDeliverD;
            this.warehouseOrOrderNumber = warehouseOrOrderNumber;
            this.productType = productType;
            this.numberOfProducts = numberOfProducts;
        }

        @Override
        public String toString() {
            return loadLDeliverD + " " + warehouseOrOrderNumber + " " + productType + " " + numberOfProducts;
        }
    }

    private class Warehouse extends BaseObject {

        private Map<Integer, ProductType> productTypeMap = new HashMap<>();

        public Warehouse(int number) {
            super(number);
        }

        public Map<Integer, ProductType> getProductTypeList() {
            return productTypeMap;
        }

        public boolean containsProductType(ProductType droneHandledProductType) {
            return productTypeMap.containsKey(droneHandledProductType.getNumber());
        }

        public int decreaseProductTypeByOne(ProductType droneHandledProductType) {
            // get product type from warehouse's inventory
            ProductType warehouseProductType = productTypeMap.get(droneHandledProductType.getNumber());
            // decrease product type's quantity by one
            warehouseProductType.setQuantity(warehouseProductType.getQuantity() - 1);
            // if there isn't any product left for this product type, remove it from warehouse's inventory
            if (warehouseProductType.getQuantity() == 0) {
                productTypeMap.remove(droneHandledProductType.getNumber());
            }
            return warehouseProductType.getQuantity();
        }
    }

    private class ProductType {
        private int number;
        private int quantity;
        private int weight;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }
    }

    private class Order extends BaseObject {

        private Map<Integer, ProductType> productTypeMap = new HashMap<>();

        public Order(int number) {
            super(number);
        }

        public Map<Integer, ProductType> getProductTypeList() {
            return productTypeMap;
        }

    }

    private class Drone {
        private int droneNumber;
        private int waitingTime = 0;
        private int droneCurrentX = 0;
        private int droneCurrentY = 0;
        private int droneMaximumPayload = 100;
        private Map<Integer, List<Operation>> turnNumberLocationMap = new TreeMap<>();

        public Drone(int droneNumber) {
            this.droneNumber = droneNumber;
        }

        public void run(int currentTurn) {
            // if the waiting time is zero than run, otherwise do nothing, this.waitingTime will be decreased after if condition
            if (this.waitingTime == 0) {
                // find nearest orders for this drone in a distance ordered array

                Order currentOrder = this.findNearestOrder(this.droneCurrentX, this.droneCurrentY);
                if (currentOrder != null) {
                    // find productTypes that this drone can carry in this order
                    // remove these productTypes / increase their quantity in order's list,
                    // this drone will handle/deliver these productType(s)-quantities
                    List<ProductType> droneHandledProductTypes = this.findProductsThatThisDroneCanCarry(currentOrder);
                    // will use this map at delivery operation generation
                    Map<Integer, Integer> productTypeNumberProductQuantity = new HashMap<>();
                    for (ProductType droneHandledProductType : droneHandledProductTypes) {
                        productTypeNumberProductQuantity.put(droneHandledProductType.getNumber(), droneHandledProductType.getQuantity());
                    }


                    // find nearest warehouses in order
                    List<Warehouse> warehouses = this.findNearestWarehouses(this.droneCurrentX, this.droneCurrentY);
                    // check every warehouse
                    for (Warehouse warehouse : warehouses) {
                        // we will use iterator to remove drone handled productTypes from this list
                        Iterator<ProductType> droneHandledProductIterator = droneHandledProductTypes.iterator();
                        while (droneHandledProductIterator.hasNext()) {
                            // current productType
                            ProductType droneHandledProductType = droneHandledProductIterator.next();
                            // if this warehouse got this productType
                            if (warehouse.containsProductType(droneHandledProductType)) {

                                // find distance from drone's current location to warehouse
                                int distanceFromCurrentLocationToWarehouse =
                                        this.findDistanceToLocation(this.droneCurrentX, this.droneCurrentY, warehouse.getX(), warehouse.getY());
                                // move to warehouse location
                                this.waitingTime += distanceFromCurrentLocationToWarehouse;
                                // set location of the drone to warehouse
                                this.droneCurrentX = warehouse.getX();
                                this.droneCurrentY = warehouse.getY();

                                // loop for the quantity of the drone handled ProductType
                                int droneHandledProductTypeInitialQuantity = droneHandledProductType.getQuantity();
                                for (int i = 0; i < droneHandledProductTypeInitialQuantity; i++) {
                                    // decrease the productType by one
                                    int remainingProductTypeQuantityInWarehouse = warehouse.decreaseProductTypeByOne(droneHandledProductType);
                                    // decrease drone handled ProductType's quantity
                                    droneHandledProductType.setQuantity(droneHandledProductType.getQuantity() - 1);

                                    // if there isn't any product in this type left for this warehouse, skip to next product
                                    if (remainingProductTypeQuantityInWarehouse == 0) {
                                        break;
                                    }
                                }

                                // load from warehouse for this product type
                                this.waitingTime++;

                                // put turn number and operation to map
                                if (!this.turnNumberLocationMap.containsKey(currentTurn)) {
                                    this.turnNumberLocationMap.put(currentTurn, new LinkedList<>());
                                }
                                this.turnNumberLocationMap
                                        .get(currentTurn)
                                        .add(
                                                new Operation("L", warehouse.getNumber(), "" + droneHandledProductType.getNumber(), "" + (droneHandledProductTypeInitialQuantity - droneHandledProductType.getQuantity()))
                                        );

                                // remove this productType from list if there all of the products obtained
                                if (droneHandledProductType.getQuantity() == 0) {
                                    droneHandledProductIterator.remove();
                                }
                            }
                        }
                    }
                    if (droneHandledProductTypes.size() > 0) {
                        throw new RuntimeException("jesus! could not find this(these) product(s) in any warehouse? product(s): " + droneHandledProductTypes);
                    }

                    // find distance from drone's current location to order location
                    int distanceFromCurrentLocationToOrder =
                            this.findDistanceToLocation(this.droneCurrentX, this.droneCurrentY, currentOrder.getX(), currentOrder.getY());
                    // move from last warehouse to order location
                    this.waitingTime += distanceFromCurrentLocationToOrder;

                    // put turn number and location to map for every product type
                    for (Integer productTypeNumber : productTypeNumberProductQuantity.keySet()) {
                        // deliver at order location
                        this.waitingTime++;

                        // put turn number and operation to map
                        if (!this.turnNumberLocationMap.containsKey(currentTurn)) {
                            this.turnNumberLocationMap.put(currentTurn, new LinkedList<>());
                        }
                        this.turnNumberLocationMap
                                .get(currentTurn)
                                .add(
                                        new Operation("D", currentOrder.getNumber(), "" + productTypeNumber, "" + productTypeNumberProductQuantity.get(productTypeNumber))
                                );
                    }
                }
            }
            // for every turn, decrease the waiting time by one
            this.waitingTime--;
        }

        private int findDistanceToLocation(int fromX, int fromY, int toX, int toY) {
            int x = Math.abs(fromX - toX);
            int y = Math.abs(fromY - toY);
            Long distance = Math.round(Math.sqrt((x * x) + (y * y)));
            return distance.intValue();
        }

        private List<Warehouse> findNearestWarehouses(int fromLocationX, int fromLocationY) {
            List<Warehouse> closeToDistantWarehouses = new ArrayList<>();
            List<Integer> distances = new ArrayList<>();
            Map<Integer, List<Warehouse>> distanceWarehouseMap = new HashMap<>();

            for (Warehouse warehouse : warehouses) {
                int distance = this.findDistanceToLocation(fromLocationX, fromLocationY, warehouse.getX(), warehouse.getY());
                distances.add(distance);
                if(!distanceWarehouseMap.containsKey(distance)){
                    distanceWarehouseMap.put(distance, new LinkedList<>());
                }
                distanceWarehouseMap.get(distance).add(warehouse);
            }
            Collections.sort(distances);
            for (Integer distance : distances) {
                for(Warehouse warehouse : distanceWarehouseMap.get(distance)){
                    closeToDistantWarehouses.add(warehouse);
                }
            }
            return closeToDistantWarehouses;
        }

        private List<ProductType> findProductsThatThisDroneCanCarry(Order order) {
            List<ProductType> droneProductTypes = new ArrayList<>();
            int availableWeight = this.droneMaximumPayload;
            /*
            loop through product types
                loop through product type's quantity
                    check weight is available
                        add product to drone if not exists/increase drone's product type's quantity
                        decrease order's product type's quantity

            remove depleted products from order's list

            check if there are any products left in order, if there are no products in order
                remove order from orders

             */

            // depleted order product types
            List<Integer> depletedOrderProductTypeNumbers = new ArrayList<>();
            // loop through product types
            for (Integer orderProductTypeNumber : order.getProductTypeList().keySet()) {
                ProductType orderProductType = order.getProductTypeList().get(orderProductTypeNumber);
                // loop through product type's quantity
                for (int orderProductTypeQuantity = 0; orderProductTypeQuantity < orderProductType.getQuantity(); orderProductTypeQuantity++) {
                    // check weight is available
                    if (orderProductType.getQuantity() > 0 && (availableWeight - orderProductType.getWeight()) > 0) {
                        // decrease available weight by the product type's weight
                        availableWeight = availableWeight - orderProductType.getWeight();

                        // add product to drone if not exists
                        ProductType existingDroneProductType = null;
                        for (ProductType droneProductType : droneProductTypes) {
                            if (droneProductType.getNumber() == orderProductType.getNumber()) {
                                existingDroneProductType = droneProductType;
                                break;
                            }
                        }
                        // product type does not exist in drone's list
                        if (existingDroneProductType == null) {
                            ProductType droneProductType = new ProductType();
                            droneProductType.setNumber(orderProductType.getNumber());
                            droneProductType.setWeight(orderProductType.getWeight());
                            // set this product type's quantity in drone's product list to 1
                            droneProductType.setQuantity(1);
                            droneProductTypes.add(droneProductType);
                        } else {
                            // product type exist in drone's list
                            // increase drone's product type's quantity
                            existingDroneProductType.setQuantity(existingDroneProductType.getQuantity() + 1);
                        }

                        // decrease order's product type's quantity
                        orderProductType.setQuantity(orderProductType.getQuantity() - 1);
                        if (orderProductType.getQuantity() == 0) {
                            depletedOrderProductTypeNumbers.add(orderProductTypeNumber);
                        }
                    }
                }
            }
            // remove depleted products from order's list
            for (Integer depletedOrderProductTypeNumber : depletedOrderProductTypeNumbers) {
                order.getProductTypeList().remove(depletedOrderProductTypeNumber);
            }

            // check if there are any products left in order, if there are no products in order
            if (order.getProductTypeList().size() == 0) {
                //      remove order from orders
                orders.remove(order);
            }
            // return product types with quantities for this drone
            return droneProductTypes;
        }

        private Order findNearestOrder(int droneCurrentX, int droneCurrentY) {
            if (orders.size() == 0) {
                return null;
            }
            List<Double> distances = new ArrayList<>();
            Map<Double, Order> distanceOrderMap = new HashMap<>();
            for (Order order : orders) {
                double distance = Point2D.distance(droneCurrentX, droneCurrentY, order.getX(), order.getY());
                distances.add(distance);
                distanceOrderMap.put(distance, order);
            }
            Collections.sort(distances);
            Double nearestOrderDistance = distances.get(0);
            Order nearestOrder = distanceOrderMap.get(nearestOrderDistance);
            return nearestOrder;
        }

        public void writeCommands(String fileName, int totalNumberOfTurns) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int turnNumber : turnNumberLocationMap.keySet()) {
                if (turnNumber <= totalNumberOfTurns) {
                    List<Operation> operationList = turnNumberLocationMap.get(turnNumber);
                    // first print L
                    Iterator<Operation> operationIterator = operationList.stream().filter(operation -> "L".equals(operation.loadLDeliverD)).iterator();
                    append(operationIterator, stringBuilder);
                    // then print D
                    operationIterator = operationList.stream().filter(operation -> "D".equals(operation.loadLDeliverD)).iterator();
                    append(operationIterator, stringBuilder);
                }
            }
            try {
                Files.write(Paths.get(fileName + ".out"), (stringBuilder.toString()).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void append(Iterator<Operation> operationIterator, StringBuilder stringBuilder) {
            while (operationIterator.hasNext()) {
                Operation operation = operationIterator.next();
                //  0 L 9 2 3   (Command  to  drone 0, load at warehouse 9, products of product type 2, 3 of them)
                //  0 D 1 5 4   (Command  to  drone 0, deliver for order 1, products of product type 5, 4 of them)
                String line = droneNumber + " " + operation.toString() + "\n";
                System.out.print(line);
                stringBuilder.append(line);
            }
        }
    }

    public void run(String fileName) {
        // read input
        Repository repository = this.readInput(fileName);
        // find all drones
        Drone[] drones = this.createDrones(repository);
        // find Orders
        orders = this.createOrders(repository.getOrderList());
        // find Warehouses
        warehouses = this.createWarehouses(repository.getWarehouseList());

        // find total number of turns
        int totalNumberOfTurns = repository.getMaxTurns();
        // loop through turns
        for (int currentTurn = 0; currentTurn < totalNumberOfTurns; currentTurn++) {
            // for each turn, run every single drone once, each drone decides what to do
            for (Drone drone : drones) {
                drone.run(currentTurn);
            }
        }
        try {
            // create file to write output
            Files.write(Paths.get(fileName + ".out"), "".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Drone drone : drones) {
            // drones will write their operations
            drone.writeCommands(fileName, totalNumberOfTurns);
        }

    }

    private List<Warehouse> createWarehouses(List<com.atkafasi.model.Warehouse> warehouseList) {
        List<Warehouse> warehouses = new ArrayList<>();
        for (int warehouseIndex = 0; warehouseIndex < warehouseList.size(); warehouseIndex++) {
            com.atkafasi.model.Warehouse w = warehouseList.get(warehouseIndex);
            Warehouse warehouse = new Warehouse(warehouseIndex);

            warehouse.setX(w.getLocation().getColumn());
            warehouse.setY(w.getLocation().getRow());

            for (int productTypeNumber = 0; productTypeNumber < w.getItemInTheStoreMap().size(); productTypeNumber++) {
                if (w.getItemInTheStoreMap().get(productTypeNumber) != 0) {
                    ProductType productType = new ProductType();
                    productType.setQuantity(w.getItemInTheStoreMap().get(productTypeNumber));
                    productType.setNumber(productTypeNumber);
                    warehouse.getProductTypeList().put(productTypeNumber, productType);
                }
            }

            warehouses.add(warehouse);
        }
        return warehouses;
    }

    private List<Order> createOrders(List<com.atkafasi.model.Order> orderList) {
        List<Order> orders = new ArrayList<>();
        for (int orderIndex = 0; orderIndex < orderList.size(); orderIndex++) {
            com.atkafasi.model.Order o = orderList.get(orderIndex);
            Order order = new Order(orderIndex);
            order.setNumber(orderIndex);
            order.setX(o.getOrderLocation().getColumn());
            order.setY(o.getOrderLocation().getRow());
            for (Item item : o.getItems()) {
                if (!order.getProductTypeList().containsKey(item.getItemType())) {
                    ProductType productType = new ProductType();
                    productType.setQuantity(1);
                    productType.setNumber(item.getItemType());
                    productType.setWeight(item.getItemWeight());
                    order.getProductTypeList().put(item.getItemType(), productType);
                } else {
                    ProductType productType = order.getProductTypeList().get(item.getItemType());
                    productType.setQuantity(productType.getQuantity() + 1);
                }
            }
            orders.add(order);
        }
        return orders;
    }

    private Drone[] createDrones(Repository repository) {
        com.atkafasi.model.Warehouse warehouseZero = repository.getWarehouseList().get(0);
        Drone[] drones = new Drone[repository.getDronesCount()];
        for (int i = 0; i < repository.getDronesCount(); i++) {
            Drone drone = new Drone(i);
            drone.droneCurrentX = warehouseZero.getLocation().getColumn();
            drone.droneCurrentY = warehouseZero.getLocation().getRow();
            drone.droneMaximumPayload = repository.getMaxPayload();
            drones[i] = drone;
        }
        return drones;
    }

    private Repository readInput(String fileName) {
        return InputReader.readFile(fileName);
    }
}
