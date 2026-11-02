/*
 * Name: valentin esparza
 * Date: 11/01/2026
 * Assignment: SDC330 Course Project - Database Implementation
 * Description: Main runs the terminal menu for the Esparza LLC Clothing
 *              Inventory and Order Manager. All data is stored in a SQLite
 *              database through the DatabaseManager class.
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static DatabaseManager db;

    public static void main(String[] args) {
        System.out.println("Esparza LLC Clothing Inventory and Order Manager");
        db = new DatabaseManager();
        seedSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            System.out.println();
            switch (choice) {
                case 1 -> addItem();
                case 2 -> viewInventory();
                case 3 -> updateItem();
                case 4 -> deleteItem();
                case 5 -> createOrder();
                case 6 -> viewOrders();
                case 7 -> lowStockReport();
                case 8 -> deleteOrder();
                case 9 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option. Please choose 1-9.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== MENU =====");
        System.out.println("1) Add item");
        System.out.println("2) View inventory");
        System.out.println("3) Update item");
        System.out.println("4) Delete item");
        System.out.println("5) Create order");
        System.out.println("6) View orders");
        System.out.println("7) Low-stock report");
        System.out.println("8) Delete order");
        System.out.println("9) Exit");
    }

    // Adds a few starter items only when the database is empty
    private static void seedSampleData() {
        if (!db.getAllItems().isEmpty()) {
            return;
        }
        db.addItem(new Shirt(0, "Classic Tee", "M", "White", 19.99, 25, "Short"));
        db.addItem(new Pants(0, "Slim Chinos", "32", "Khaki", 49.99, 12, 32));
        db.addItem(new Jacket(0, "Rain Shell", "L", "Navy", 89.99, 4, true, "Spring"));
    }

    private static void addItem() {
        System.out.println("Category: 1) Shirt  2) Pants  3) Jacket");
        int type = readInt("Choose a category: ");
        if (type < 1 || type > 3) {
            System.out.println("Invalid category.");
            return;
        }

        String name = readString("Name: ");
        String size = readString("Size: ");
        String color = readString("Color: ");
        double price = readDouble("Base price: ");
        int quantity = readInt("Quantity in stock: ");
        if (price < 0 || quantity < 0) {
            System.out.println("Price and quantity cannot be negative. Item not added.");
            return;
        }

        ClothingItem item;
        switch (type) {
            case 1 -> {
                String sleeves = readString("Sleeve style (Short/Long): ");
                item = new Shirt(0, name, size, color, price, quantity, sleeves);
            }
            case 2 -> {
                int inseam = readInt("Inseam (inches): ");
                item = new Pants(0, name, size, color, price, quantity, inseam);
            }
            default -> {
                boolean waterproof = readYesNo("Waterproof? (y/n): ");
                String season = readString("Season: ");
                item = new Jacket(0, name, size, color, price, quantity, waterproof, season);
            }
        }

        if (db.addItem(item) > 0) {
            System.out.println("Item added:");
            System.out.println(item);
        } else {
            System.out.println("The item could not be saved.");
        }
    }

    private static void viewInventory() {
        ArrayList<ClothingItem> inventory = db.getAllItems();
        if (inventory.isEmpty()) {
            System.out.println("The inventory is empty.");
            return;
        }
        System.out.println("--- Inventory ---");
        // Polymorphism: each item prints with its own toString and discount
        for (ClothingItem item : inventory) {
            System.out.println(item);
        }
    }

    private static void updateItem() {
        int id = readInt("Item ID to update: ");
        ClothingItem item = db.getItemById(id);
        if (item == null) {
            System.out.println("No item found with ID " + id + ".");
            return;
        }
        System.out.println("Current: " + item);
        double price = readDouble("New base price: ");
        int quantity = readInt("New quantity in stock: ");
        if (price < 0 || quantity < 0) {
            System.out.println("Price and quantity cannot be negative. No changes made.");
            return;
        }
        item.setBasePrice(price);
        item.setQuantityInStock(quantity);
        if (db.updateItem(item)) {
            System.out.println("Item updated:");
            System.out.println(item);
        } else {
            System.out.println("The item could not be updated.");
        }
    }

    private static void deleteItem() {
        int id = readInt("Item ID to delete: ");
        ClothingItem item = db.getItemById(id);
        if (item == null) {
            System.out.println("No item found with ID " + id + ".");
            return;
        }
        if (db.isItemOnOrder(id)) {
            System.out.println("This item is on an existing order and cannot be deleted.");
            System.out.println("Set its quantity to 0 instead, or delete those orders first.");
            return;
        }
        if (db.deleteItem(id)) {
            System.out.println("Deleted: " + item.getName());
        } else {
            System.out.println("The item could not be deleted.");
        }
    }

    private static void createOrder() {
        if (db.getAllItems().isEmpty()) {
            System.out.println("There are no items to order.");
            return;
        }
        String customer = readString("Customer name: ");
        Order order = new Order(0, customer, LocalDate.now().toString());

        while (true) {
            int id = readInt("Item ID to add (0 to finish): ");
            if (id == 0) {
                break;
            }
            ClothingItem item = db.getItemById(id);
            if (item == null) {
                System.out.println("No item found with ID " + id + ".");
                continue;
            }
            int quantity = readInt("Quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                continue;
            }
            int available = item.getQuantityInStock() - quantityInOrder(order, id);
            if (quantity > available) {
                System.out.println("Only " + available + " available.");
                continue;
            }
            order.addLine(item, quantity);
            System.out.println("Added " + quantity + " x " + item.getName());
        }

        if (order.getLines().isEmpty()) {
            System.out.println("No items were added. Order cancelled.");
            return;
        }
        if (db.saveOrder(order)) {
            System.out.println();
            System.out.println("--- Order Summary ---");
            System.out.println(order);
        } else {
            System.out.println("The order could not be saved.");
        }
    }

    // How many of an item are already on the order being built
    private static int quantityInOrder(Order order, int itemId) {
        int total = 0;
        for (OrderLine line : order.getLines()) {
            if (line.getItem().getItemId() == itemId) {
                total += line.getQuantity();
            }
        }
        return total;
    }

    private static void viewOrders() {
        ArrayList<Order> orders = db.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("There are no orders yet.");
            return;
        }
        System.out.println("--- Orders ---");
        for (Order order : orders) {
            System.out.println(order);
            System.out.println();
        }
    }

    private static void deleteOrder() {
        int id = readInt("Order ID to delete: ");
        if (db.deleteOrder(id)) {
            System.out.println("Order " + id + " deleted and its items returned to stock.");
        } else {
            System.out.println("No order found with ID " + id + ".");
        }
    }

    private static void lowStockReport() {
        System.out.println("--- Low Stock (" + LOW_STOCK_THRESHOLD + " or fewer) ---");
        ArrayList<ClothingItem> lowStock = db.getLowStockItems(LOW_STOCK_THRESHOLD);
        if (lowStock.isEmpty()) {
            System.out.println("No items are low on stock.");
            return;
        }
        for (ClothingItem item : lowStock) {
            System.out.println(item);
        }
    }

    // ----- Input helpers -----

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static boolean readYesNo(String prompt) {
        while (true) {
            String answer = readString(prompt).toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Please enter y or n.");
        }
    }
}
