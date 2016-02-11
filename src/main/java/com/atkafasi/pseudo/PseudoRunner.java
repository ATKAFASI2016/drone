package com.atkafasi.pseudo;

import com.atkafasi.model.Repository;
import com.atkafasi.reader.InputReader;

import java.util.*;

public class PseudoRunner {

    public static void main(String[] args) {
        PseudoRunner pseudoRunner = new PseudoRunner();
        pseudoRunner.run();
    }

    public PseudoRunner() {
    }

    private class BaseObject {
        private int number;
        private int x = 0;
        private int y = 0;

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
        public boolean containsProduct(ProductType productType) {
            return true;
        }

        public void removeProduct(ProductType productType) {
        }
    }

    private class ProductType {
        private int number;
        private int quantity;

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
    }

    private class Order extends BaseObject {

        private int orderX = 0;
        private int orderY = 0;

        public void removeProducts(ProductType[] productTypes) {
        }

    }

    private class Drone {
        private int droneNumber;
        private int waitingTime = 0;
        private int droneCurrentX = 0;
        private int droneCurrentY = 0;
        private int droneMaximumPayload = 100;
        private Map<Integer, Operation> turnNumberLocationMap = new TreeMap<Integer, Operation>();

        public Drone(int droneNumber) {
            this.droneNumber = droneNumber;
        }

        public void run(int currentTurn) {
            // if the waiting time is zero than run, otherwise do nothing, this.waitingTime will be decreased after if condition
            if (this.waitingTime == 0) {
                Order currentOrder = null;
                ProductType[] productTypes = null;
                // find nearest orders for this drone in a distance ordered array
                // TODO find nearest order!!! single
                Order[] orders = this.findNearestOrders(this.droneCurrentX, this.droneCurrentY);
                for (Order order : orders) {
                    // find productTypes that this drone can carry in this order
                    productTypes = this.findProductsThatThisDroneCanCarry(order, droneMaximumPayload);
                    // if there are productTypes available for this drone
                    if (productTypes.length > 0) {
                        // if there are productTypes then remove them from order, this drone will handle/deliver these productTypes
                        this.removeProductsFromOrder(order, productTypes);
                        // this is drone's current order
                        currentOrder = order;
                        break;
                    }
                }
                // current order should not be null
                if (currentOrder != null) {
                    // find nearest warehouses in order
                    Warehouse[] warehouses = this.findNearestWarehouses(this.droneCurrentX, this.droneCurrentY);
                    // convert product array to list, we will use iterator to remove productTypes from this list
                    List<ProductType> productTypeList = Arrays.asList(productTypes);
                    Iterator<ProductType> productIterator = productTypeList.iterator();
                    while (productIterator.hasNext()) {
                        // current productType
                        ProductType productType = productIterator.next();
                        // check every warehouse
                        for (Warehouse warehouse : warehouses) {
                            // if this warehouse got this productType
                            if (warehouse.containsProduct(productType)) {
                                // remove the productType so others wont be able to grab it
                                // TODO productType type and productType numbers ???
                                warehouse.removeProduct(productType);
                                // find distance from drone's current location to warehouse
                                int distanceFromCurrentLocationToWarehouse =
                                        this.findDistanceToLocation(this.droneCurrentX, this.droneCurrentY, warehouse.getX(), warehouse.getY());
                                // put turn number and location to map
                                this.turnNumberLocationMap.put(
                                        currentTurn + distanceFromCurrentLocationToWarehouse,
                                        new Operation("L", warehouse.getNumber(), "-", "" + productTypes.length)
                                );
                                // move to warehouse location
                                this.waitingTime += distanceFromCurrentLocationToWarehouse;
                                // load from warehouse
                                this.waitingTime++;
                                // set location of the drone to warehouse
                                this.droneCurrentX = warehouse.getX();
                                this.droneCurrentY = warehouse.getY();
                                // remove this productType from list
                                productIterator.remove();
                                // skip to next productType, this one is handled
                                break;
                            }
                        }
                    }
                    if (productTypeList.size() > 0) {
                        throw new RuntimeException("jesus! could not find this(these) product(s) in any warehose? product(s): " + productTypeList);
                    }
                    // find distance from drone's current location to order location
                    int distanceFromCurrentLocationToOrder =
                            this.findDistanceToLocation(this.droneCurrentX, this.droneCurrentY, currentOrder.getX(), currentOrder.getY());
                    // put turn number and location to map
                    this.turnNumberLocationMap.put(
                            currentTurn + distanceFromCurrentLocationToOrder,
                            new Operation("D", currentOrder.getNumber(), "-", "" + productTypes.length)
                    );
                    // move from last warehouse to order location
                    this.waitingTime += distanceFromCurrentLocationToOrder;
                    // TODO deliver per product type ???
                    // deliver at order location
                    this.waitingTime++;
                } else {
                    throw new RuntimeException("wtf?");
                }
            }
            // for every turn, decrease the waiting time by one
            this.waitingTime--;
        }

        private int findDistanceToLocation(int fromX, int fromY, int toX, int toY) {
            return 3;
        }

        private Warehouse[] findNearestWarehouses(int fromLocationX, int fromLocationY) {
            return new Warehouse[]{new Warehouse(), new Warehouse()};
        }

        private void removeProductsFromOrder(Order order, ProductType[] productTypes) {
            order.removeProducts(productTypes);
        }

        private ProductType[] findProductsThatThisDroneCanCarry(Order order, int droneMaximumPayload) {
            return new ProductType[]{new ProductType(), new ProductType()};
        }

        private Order[] findNearestOrders(int droneCurrentX, int droneCurrentY) {
            return new Order[]{new Order(), new Order(), new Order(), new Order()};
        }

        public void writeCommands(int totalNumberOfTurns) {
            for (int turnNumber : turnNumberLocationMap.keySet()) {
                if (turnNumber <= totalNumberOfTurns) {
                    Operation operation = turnNumberLocationMap.get(turnNumber);
                    //  0 L 9 2 3   (Command  to  drone 0, load at warehouse 9, products of product type 2, 3 of them)
                    //  0 D 1 5 4   (Command  to  drone 0, deliver for order 1, products of product type 5, 4 of them)
                    System.out.println(droneNumber + " " + operation.toString());
                }
            }
        }
    }

    public void run() {
        // read input
        Repository repository = this.readInput();
        // find all drones
        Drone[] drones = this.createDrones(repository);
        // find total number of turns
        int totalNumberOfTurns = 100;
        // loop through turns
        for (int currentTurn = 0; currentTurn < totalNumberOfTurns; currentTurn++) {
            // for each turn, run every single drone once, each drone decides what to do
            for (Drone drone : drones) {
                drone.run(currentTurn);
            }
        }
        for (Drone drone : drones) {
            // drones will write their operations
            drone.writeCommands(totalNumberOfTurns);
        }
    }

    private Drone[] createDrones(Repository repository) {
        Drone[] drones = new Drone[repository.getDronesCount()];
        for (int i = 0; i < repository.getDronesCount(); i++) {
            Drone drone = new Drone(i);
            drones[i] = drone;
        }
        return drones;
    }

    private Repository readInput() {
        return InputReader.readFile("busy_day.in");
    }
}
