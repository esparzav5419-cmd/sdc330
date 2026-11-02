/*
 * Name: valentin esparza
 * Date: 11/01/2026
 * Assignment: SDC330 Course Project - Database Implementation
 * Description: DatabaseManager handles all SQLite access for the application
 *              using JDBC. It creates the tables and performs create, read,
 *              update and delete operations on inventory items and orders.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:esparza.db";

    // Opens the database (SQLite creates the file if needed) and makes the tables
    public DatabaseManager() {
        createTables();
    }

    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    private void createTables() {
        String items = "CREATE TABLE IF NOT EXISTS inventory_items ("
                + "item_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "category TEXT NOT NULL, "
                + "size TEXT NOT NULL, "
                + "color TEXT NOT NULL, "
                + "base_price REAL NOT NULL, "
                + "quantity INTEGER NOT NULL, "
                + "sleeve_style TEXT, "
                + "inseam INTEGER, "
                + "waterproof INTEGER, "
                + "season TEXT)";
        String orders = "CREATE TABLE IF NOT EXISTS orders ("
                + "order_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "customer_name TEXT NOT NULL, "
                + "order_date TEXT NOT NULL)";
        String lines = "CREATE TABLE IF NOT EXISTS order_lines ("
                + "line_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "order_id INTEGER NOT NULL, "
                + "item_id INTEGER NOT NULL, "
                + "quantity INTEGER NOT NULL, "
                + "unit_price REAL NOT NULL, "
                + "FOREIGN KEY (order_id) REFERENCES orders(order_id), "
                + "FOREIGN KEY (item_id) REFERENCES inventory_items(item_id))";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(items);
            stmt.execute(orders);
            stmt.execute(lines);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // ----- CREATE -----

    // Adds an item, sets its new ID, and returns the ID (or -1 on failure)
    public int addItem(ClothingItem item) {
        String sql = "INSERT INTO inventory_items (name, category, size, color, "
                + "base_price, quantity, sleeve_style, inseam, waterproof, season) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS)) {
            bindItem(ps, item);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    item.setItemId(newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
        return -1;
    }

    // Saves an order and its lines, and subtracts the ordered quantities from
    // stock. Everything happens in one transaction, so it all succeeds or none does.
    public boolean saveOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_name, order_date) VALUES (?, ?)";
        String lineSql = "INSERT INTO order_lines (order_id, item_id, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?)";
        String stockSql = "UPDATE inventory_items SET quantity = quantity - ? WHERE item_id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try {
                int orderId = -1;
                try (PreparedStatement ps = conn.prepareStatement(orderSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getCustomerName());
                    ps.setString(2, order.getOrderDate());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            orderId = keys.getInt(1);
                        }
                    }
                }
                if (orderId < 0) {
                    throw new SQLException("Could not get the new order ID.");
                }

                try (PreparedStatement linePs = conn.prepareStatement(lineSql);
                        PreparedStatement stockPs = conn.prepareStatement(stockSql)) {
                    for (OrderLine line : order.getLines()) {
                        linePs.setInt(1, orderId);
                        linePs.setInt(2, line.getItem().getItemId());
                        linePs.setInt(3, line.getQuantity());
                        linePs.setDouble(4, line.getUnitPrice());
                        linePs.executeUpdate();

                        stockPs.setInt(1, line.getQuantity());
                        stockPs.setInt(2, line.getItem().getItemId());
                        stockPs.executeUpdate();
                    }
                }

                conn.commit();
                order.setOrderId(orderId);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error saving order: " + e.getMessage());
        }
        return false;
    }

    // ----- READ -----

    public ArrayList<ClothingItem> getAllItems() {
        ArrayList<ClothingItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_items ORDER BY item_id";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ClothingItem item = buildItem(rs);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error reading items: " + e.getMessage());
        }
        return items;
    }

    // Returns the item, or null if the ID does not exist
    public ClothingItem getItemById(int itemId) {
        String sql = "SELECT * FROM inventory_items WHERE item_id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildItem(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error reading item: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<ClothingItem> getLowStockItems(int threshold) {
        ArrayList<ClothingItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_items WHERE quantity <= ? "
                + "ORDER BY quantity, item_id";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClothingItem item = buildItem(rs);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error reading low-stock items: " + e.getMessage());
        }
        return items;
    }

    // Returns every order with its lines
    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        String orderSql = "SELECT * FROM orders ORDER BY order_id";
        String lineSql = "SELECT i.*, ol.quantity AS line_quantity, ol.unit_price "
                + "FROM order_lines ol "
                + "JOIN inventory_items i ON ol.item_id = i.item_id "
                + "WHERE ol.order_id = ? ORDER BY ol.line_id";
        try (Connection conn = connect()) {
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(orderSql)) {
                while (rs.next()) {
                    orders.add(new Order(rs.getInt("order_id"),
                            rs.getString("customer_name"),
                            rs.getString("order_date")));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(lineSql)) {
                for (Order order : orders) {
                    ps.setInt(1, order.getOrderId());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            ClothingItem item = buildItem(rs);
                            if (item != null) {
                                order.addLine(new OrderLine(item,
                                        rs.getInt("line_quantity"),
                                        rs.getDouble("unit_price")));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error reading orders: " + e.getMessage());
        }
        return orders;
    }

    // True if the item appears on any order (such items cannot be deleted)
    public boolean isItemOnOrder(int itemId) {
        String sql = "SELECT COUNT(*) FROM order_lines WHERE item_id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking orders: " + e.getMessage());
        }
        return false;
    }

    // ----- UPDATE -----

    public boolean updateItem(ClothingItem item) {
        String sql = "UPDATE inventory_items SET name = ?, category = ?, size = ?, "
                + "color = ?, base_price = ?, quantity = ?, sleeve_style = ?, "
                + "inseam = ?, waterproof = ?, season = ? WHERE item_id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            bindItem(ps, item);
            ps.setInt(11, item.getItemId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating item: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStock(int itemId, int newQuantity) {
        String sql = "UPDATE inventory_items SET quantity = ? WHERE item_id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
        }
        return false;
    }

    // ----- DELETE -----

    // Fails (returns false) if the item is on an order
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM inventory_items WHERE item_id = ?";
        try (Connection conn = connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting item: " + e.getMessage());
        }
        return false;
    }

    // Deletes an order and its lines, and returns the ordered items to stock
    public boolean deleteOrder(int orderId) {
        String restoreSql = "UPDATE inventory_items SET quantity = quantity + "
                + "(SELECT SUM(quantity) FROM order_lines "
                + "WHERE order_lines.item_id = inventory_items.item_id AND order_id = ?) "
                + "WHERE item_id IN (SELECT item_id FROM order_lines WHERE order_id = ?)";
        String linesSql = "DELETE FROM order_lines WHERE order_id = ?";
        String orderSql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(restoreSql)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, orderId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(linesSql)) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }
                int removed;
                try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                    ps.setInt(1, orderId);
                    removed = ps.executeUpdate();
                }
                conn.commit();
                return removed > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting order: " + e.getMessage());
        }
        return false;
    }

    // ----- Helpers -----

    // Sets parameters 1-10 (name through season) from an item
    private void bindItem(PreparedStatement ps, ClothingItem item) throws SQLException {
        ps.setString(1, item.getName());
        ps.setString(2, item.getCategory());
        ps.setString(3, item.getSize());
        ps.setString(4, item.getColor());
        ps.setDouble(5, item.getBasePrice());
        ps.setInt(6, item.getQuantityInStock());

        // Columns that belong to only one clothing type default to NULL
        ps.setNull(7, Types.VARCHAR);
        ps.setNull(8, Types.INTEGER);
        ps.setNull(9, Types.INTEGER);
        ps.setNull(10, Types.VARCHAR);

        switch (item) {
            case Shirt shirt -> ps.setString(7, shirt.getSleeveStyle());
            case Pants pants -> ps.setInt(8, pants.getInseam());
            case Jacket jacket -> {
                ps.setInt(9, jacket.getWaterproof() ? 1 : 0);
                ps.setString(10, jacket.getSeason());
            }
            default -> {
            }
        }
    }

    // Builds the correct subclass from the category column
    private ClothingItem buildItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("item_id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        String size = rs.getString("size");
        String color = rs.getString("color");
        double price = rs.getDouble("base_price");
        int quantity = rs.getInt("quantity");

        switch (category) {
            case "Shirt" -> {
                return new Shirt(id, name, size, color, price, quantity,
                        rs.getString("sleeve_style"));
            }
            case "Pants" -> {
                return new Pants(id, name, size, color, price, quantity,
                        rs.getInt("inseam"));
            }
            case "Jacket" -> {
                return new Jacket(id, name, size, color, price, quantity,
                        rs.getInt("waterproof") == 1, rs.getString("season"));
            }
            default -> {
            }
        }
        return null;
    }
}