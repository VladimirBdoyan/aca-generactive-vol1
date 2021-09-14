package com.example.acageneractivweb.repository;

import com.example.acageneractivweb.model.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcGroupRepository {
    private static PreparedStatement stmt;
    private static ResultSet rs = null;
    public static Group findGroupById(int id) {
        Group group = null;
        try (Connection connection = JdbcItemRepository.generactiveJdbcConnection()) {
            String query = "SELECT * FROM groups WHERE id = " + id;
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()){
                int groupId = rs.getInt("id");
                String name = rs.getString("name");
                int parentGroup = rs.getInt("parent_group_id");
                group = new Group(groupId,name);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return group;
    }
}
