/*
 * Name: valentin esparza
 * Date: 10/25/2026
 * Assignment: SDC330 Course Project - Class Implementation
 * Description: App runs the terminal menu for the Esparza LLC Clothing
 *              Inventory and Order Manager. Data is held in memory until the
 *              database is added in Week 4.
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<ClothingItem> inventory = new ArrayList<>();
    private static final ArrayList<Order> orders = new ArrayList<>();
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static int nextItemId = 1;
    private static int nextOrderId = 1;

    public static void main(String[] args) {
        System.out.println("Esparza LLC Clothing Inventory and Order Manager");
        loadSampleData();

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
                case 8 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option. Please choose 1-8.");
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
        System.out.println("8) Exit");
    }

    private static void loadSampleData() {
        inventory.add(new Shirt(nextItemId++, "Classic Tee", "M", "White", 19.99, 25, "Short"));
        inventory.add(new Pants(nextItemId++, "Slim Chinos", "32", "Khaki", 49.99, 12, 32));
        inventory.add(new Jacket(nextItemId++, "Rain Shell", "L", "Navy", 89.99, 4, true, "Spring"));
    }

    private static ClothingItem findItem(int id) {
        for (ClothingItem item : inventory) {
            if (item.getItemId() == id) {
                return item;
            }
        }
        return null;
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
                item = new Shirt(nextItemId, name, size, color, price, quantity, sleeves);
            }
            case 2 -> {
                int inseam = readInt("Inseam (inches): ");
                item = new Pants(nextItemId, name, size, color, price, quantity, inseam);
            }
            default -> {
                boolean waterproof = readYesNo("Waterproof? (y/n): ");
                String season = readString("Season: ");
                item = new Jacket(nextItemId, name, size, color, price, quantity,
                        waterproof, season);
            }
        }

        inventory.add(item);
        nextItemId++;
        System.out.println("Item added:");
        System.out.println(item);
    }

    private static void viewInventory() {
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
        ClothingItem item = findItem(id);
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
        System.out.println("Item updated:");
        System.out.println(item);
    }

    private static void deleteItem() {
        int id = readInt("Item ID to delete: ");
        ClothingItem item = findItem(id);
        if (item == null) {
            System.out.println("No item found with ID " + id + ".");
            return;
        }
        inventory.remove(item);
        System.out.println("Deleted: " + item.getName());
    }

    private static void createOrder() {
        if (inventory.isEmpty()) {
            System.out.println("There are no items to order.");
            return;
        }
        String customer = readString("Customer name: ");
        Order order = new Order(nextOrderId, customer, LocalDate.now().toString());

        while (true) {
            int id = readInt("Item ID to add (0 to finish): ");
            if (id == 0) {
                break;
            }
            ClothingItem item = findItem(id);
            if (item == null) {
                System.out.println("No item found with ID " + id + ".");
                continue;
            }
            int quantity = readInt("Quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                continue;
            }
            if (quantity > item.getQuantityInStock()) {
                System.out.println("Only " + item.getQuantityInStock() + " in stock.");
                continue;
            }
            order.addLine(item, quantity);
            item.setQuantityInStock(item.getQuantityInStock() - quantity);
            System.out.println("Added " + quantity + " x " + item.getName());
        }

        if (order.getLines().isEmpty()) {
            System.out.println("No items were added. Order cancelled.");
            return;
        }
        orders.add(order);
        nextOrderId++;
        System.out.println();
        System.out.println("--- Order Summary ---");
        System.out.println(order);
    }

    private static void viewOrders() {
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

    private static void lowStockReport() {
        System.out.println("--- Low Stock (" + LOW_STOCK_THRESHOLD + " or fewer) ---");
        boolean found = false;
        for (ClothingItem item : inventory) {
            if (item.getQuantityInStock() <= LOW_STOCK_THRESHOLD) {
                System.out.println(item);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No items are low on stock.");
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