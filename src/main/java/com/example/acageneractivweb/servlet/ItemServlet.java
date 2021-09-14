package com.example.acageneractivweb.servlet;

import com.example.acageneractivweb.model.GenerativeItem;
import com.example.acageneractivweb.model.Item;
import com.example.acageneractivweb.model.StockItem;
import com.example.acageneractivweb.repository.ItemRepository;
import com.example.acageneractivweb.repository.JdbcItemRepository;
import com.example.acageneractivweb.util.ErrorEntity;
import com.example.acageneractivweb.util.constant.HttpConstants;
import com.example.acageneractivweb.util.idgenerator.IdGenerator;
import com.example.acageneractivweb.util.idgenerator.ItemType;
import com.example.acageneractivweb.util.idgenerator.Type;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ItemServlet", value = "/item-servlet/*")

public class ItemServlet extends HttpServlet {

    private static final String PARAM_TYPE = "type";

    private final ItemRepository itemRepository = ItemRepository.getInstance();

    // ObjectMapper parse Java objects to JSON and vise a versa.
    private final ObjectMapper objectMapper = new ObjectMapper();
    PreparedStatement stmt = null;
    ResultSet rs = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Item> items = new ArrayList<>();
        try (Connection conn = JdbcItemRepository.generactiveJdbcConnection()) {
            stmt = conn.prepareStatement("SELECT * FROM  item");
            rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int base_price = rs.getInt("base_price");
                int group_id = rs.getInt("group_id");
                Item item = new StockItem(id, base_price, name);
                items.add(item);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        resp.setContentType(HttpConstants.ContentType.APPLICATION_JSON);
        String response = objectMapper.writeValueAsString(items);
        PrintWriter writer = resp.getWriter();
        writer.write(response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String typeParam = req.getParameter(PARAM_TYPE);

        // accept only application/json
        if (!req.getContentType().equals(HttpConstants.ContentType.APPLICATION_JSON)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not_supported_format");
            return;
        }
        if (typeParam == null || typeParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing param " + PARAM_TYPE);
            return;
        }

        // Read the body content
        BufferedReader reader = req.getReader();
        String body = reader.lines().collect(Collectors.joining());

        ItemType type;

        try {
            type = ItemType.valueOf(typeParam);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid type");
            return;
        }
        Item item;

        try {
            switch (type) {
                case STOCK:
                    item = objectMapper.readValue(body, StockItem.class);
                    long id = JdbcItemRepository.insert(item,ItemType.STOCK.getValue());
                    item.setId(id);
                    resp.getWriter().write(objectMapper.writeValueAsString(item));
                    break;
                case GENERATIVE:
                    item = objectMapper.readValue(body, GenerativeItem.class);
                    item.setId(IdGenerator.getNext(Type.ITEM));
                    itemRepository.addItem(item);
                    resp.getWriter().write(objectMapper.writeValueAsString(item));
                    break;
            }
        } catch (RuntimeException e) {
            ErrorEntity errorEntity = new ErrorEntity("json_parse_failed:" + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(errorEntity));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // accept only application/json
        if (!req.getContentType().equals(HttpConstants.ContentType.APPLICATION_JSON)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not_supported_format");
            return;
        }

        int id = retrieveItemId(req);
        // Change basePrice by selected id from request
        String body = req.getReader().lines().collect(Collectors.joining());
        Item transferItem = objectMapper.readValue(body, StockItem.class);
        try {
            itemRepository.findById(id).setBasePrice(transferItem.getBasePrice());
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Wrong id : " + id);
        }
        resp.getWriter().write(objectMapper.writeValueAsString(itemRepository.findById(id)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int id = retrieveItemId(req);
        try {
            itemRepository.getItems().remove(itemRepository.findById(id));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Wrong id : " + id);
        }
        resp.getWriter().write("Done");
    }

    private static int retrieveItemId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }
        return Integer.parseInt(pathInfo);
    }
}