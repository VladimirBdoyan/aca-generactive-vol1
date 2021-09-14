package com.example.acageneractivweb.repository;

import com.example.acageneractivweb.model.GenerativeItem;
import com.example.acageneractivweb.model.Item;
import com.example.acageneractivweb.model.StockItem;
import com.example.acageneractivweb.util.idgenerator.ItemType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcItemRepository {
    private static PreparedStatement stmt;
    private static ResultSet rs = null;


    public static List<Item> getAllItems(String tableName) {
        List<Item> items = new ArrayList<>();
        try (Connection conn = JdbcItemRepository.generactiveJdbcConnection()) {
            stmt = conn.prepareStatement("SELECT * FROM " + tableName);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int base_price = rs.getInt("base_price");
                int group_id = rs.getInt("group_id");
                Item item = new StockItem(id, base_price, name);
                item.setGroup(JdbcGroupRepository.findGroupById(group_id));
                items.add(item);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Open connection to repository, read Java Item object and  add it to database table
    public static long insert(Item item, String tableName) {
        long rv;
        try (Connection conn = JdbcItemRepository.generactiveJdbcConnection()) {
            String name = item.getName();
            int base_price = item.getBasePrice();
            int group_id = item.getGroup_id();
            String query = "INSERT INTO " + tableName + " (name,base_price, group_id) VALUES ("
                    + "'" + name + "'"
                    + "," + base_price
                    + "," + group_id + ")";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            rv = generatedKeys.next() ? generatedKeys.getLong(1) : -1;
            return rv;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void delete(int removedId, String tableName) {
        try (Connection conn = JdbcItemRepository.generactiveJdbcConnection()) {
            String query = "DELETE FROM " + tableName + " WHERE id " + '=' + removedId;
            stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static int updateItem(Item updatedItem, Item currentItem, String tableName) {
        int rv = 0;
        if (compareItem(updatedItem, currentItem) < 0) {
            if (!updatedItem.getName().equals(currentItem.getName())) {
                currentItem.setName(updatedItem.getName());
            } else if (updatedItem.getBasePrice() != currentItem.getBasePrice()) {
                currentItem.setBasePrice(updatedItem.getBasePrice());
            } else if (!updatedItem.getGroup().equals(currentItem.getGroup())) {
                currentItem.setGroup(updatedItem.getGroup());
            }
            try (Connection connection = JdbcItemRepository.generactiveJdbcConnection()) {
                String query = "UPDATE " + tableName + " SET name = '"
                        + currentItem.getName() + "'"
                        + ", " + "base_price = '" + currentItem.getBasePrice() + "'"
                        + ", " + "group_id = '" + currentItem.getGroup_id() + "'"
                        + " WHERE id = " + currentItem.getId();
                stmt = connection.prepareStatement(query);
                stmt.executeUpdate();
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            rv = 1;
        } else {
            return rv;
        }
        return rv;
    }

    public static int findItemById(int id, String tableName) {
        int deletedId = 0;
        try (Connection conn = JdbcItemRepository.generactiveJdbcConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE id = " + id;
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                deletedId = rs.getInt("id");
            }
            if (deletedId > 0) {
                return id;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Item getItemById(int id, String tableName) {
        Item item = null;
        try (Connection connection = generactiveJdbcConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE id = " + id;
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                int itemId = rs.getInt("id");
                String itemName = rs.getString("name");
                int base_price = rs.getInt("base_price");
                int group_id = rs.getInt("group_id");
                if (tableName.equals(ItemType.STOCK.getValue())) {
                    item = new StockItem(itemId, base_price, itemName);
//                    item.setGroup_id(group_id);
                    item.setGroup(JdbcGroupRepository.findGroupById(group_id));
                } else if (tableName.equals(ItemType.GENERATIVE.getValue())) {
                    item = new GenerativeItem(itemId, base_price, itemName);
                    item.setGroup(JdbcGroupRepository.findGroupById(group_id));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return item;
    }

    private static int compareItem(Item updatedItem, Item currentItem) {
        int rv = 0;

        if (!updatedItem.getName().equals(currentItem.getName())) {
            return -1;
        } else if (updatedItem.getBasePrice() != currentItem.getBasePrice()) {
            return -1;
        } else if (!updatedItem.getGroup().equals(currentItem.getGroup())) {
            return -1;
        }
        return rv;
    }


    public static Connection generactiveJdbcConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "123456";
        return DriverManager.getConnection(url, user, password);
    }
}
