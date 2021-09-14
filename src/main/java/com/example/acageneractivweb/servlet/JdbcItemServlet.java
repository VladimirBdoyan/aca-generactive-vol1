package com.example.acageneractivweb.servlet;

import com.example.acageneractivweb.model.GenerativeItem;
import com.example.acageneractivweb.model.Item;
import com.example.acageneractivweb.model.StockItem;
import com.example.acageneractivweb.repository.JdbcGroupRepository;
import com.example.acageneractivweb.repository.JdbcItemRepository;
import com.example.acageneractivweb.util.constant.HttpConstants;
import com.example.acageneractivweb.util.idgenerator.ItemType;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "JdbcItemServlet", value = "/jdbcItem-servlet/*")
public class JdbcItemServlet extends HttpServlet {
    private static final String PARAM_TYPE = "type";
    private final ObjectMapper objectMapper = new ObjectMapper();
    ItemType type;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(HttpConstants.ContentType.APPLICATION_JSON);
        String response = objectMapper.writeValueAsString(JdbcItemRepository.getAllItems(getTableName(req, resp)));
        PrintWriter writer = resp.getWriter();
        writer.write(response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // accept only application/json
        if (!req.getContentType().equals(HttpConstants.ContentType.APPLICATION_JSON)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not_supported_format");
            return;
        }
        // Read the body content
        BufferedReader reader = req.getReader();
        String body = reader.lines().collect(Collectors.joining());
        Item item;
        long id;
        switch (getTableName(req, resp)) {
            case "stock_items":
                item = objectMapper.readValue(body, StockItem.class);
                id = JdbcItemRepository.insert(item, ItemType.STOCK.getValue());
                item.setId(id);
                item.setGroup(JdbcGroupRepository.findGroupById(item.getGroup_id()));
                resp.getWriter().write(objectMapper.writeValueAsString(item));
                break;
            case "generative_items":
                item = objectMapper.readValue(body, GenerativeItem.class);
                id = JdbcItemRepository.insert(item, ItemType.GENERATIVE.getValue());
                item.setId(id);
                resp.getWriter().write(objectMapper.writeValueAsString(item));
                break;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int group_id;
        int updateItemId = retrieveItemId(req);
        String tableName = getTableName(req, resp);
        String body = req.getReader().lines().collect(Collectors.joining());
        Item transferItem = null;
        switch (tableName) {
            case "stock_items":
                transferItem = objectMapper.readValue(body, StockItem.class);
                transferItem.setId(updateItemId);
                group_id = transferItem.getGroup_id();
                transferItem.setGroup(JdbcGroupRepository.findGroupById(group_id));
                break;
            case "generative_items":
                transferItem = objectMapper.readValue(body, GenerativeItem.class);
                group_id = transferItem.getGroup_id();
                transferItem.setGroup(JdbcGroupRepository.findGroupById(group_id));
                break;
        }
        // Get Item from Database by Http request : it's current Item
        try {
            Item currentItem = JdbcItemRepository.getItemById(updateItemId, tableName);
            if (transferItem != null) {
                int changed = JdbcItemRepository.updateItem(transferItem, currentItem, tableName);
                if (changed == 0) {
                    resp.getWriter().write("You didn't change any properties ");
                } else {
                    resp.getWriter().write(objectMapper.writeValueAsString(JdbcItemRepository.getItemById(updateItemId, tableName)));
                }
            }
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Wrong item Json Body :))) ");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int removedId = retrieveItemId(req);
        String tableName = getTableName(req, resp);

        if (JdbcItemRepository.findItemById(removedId, tableName) > 0) {
            JdbcItemRepository.delete(removedId, tableName);
            resp.getWriter().write("Entity by ID" + removedId + " is deleted");
        } else {
            resp.getWriter().write("Invalid removed id");
        }
    }

    private static int retrieveItemId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }
        return Integer.parseInt(pathInfo);
    }

    private String getTableName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String typeParam = req.getParameter(PARAM_TYPE);
        String tableName = null;
        if (typeParam == null || typeParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing param " + PARAM_TYPE);
            return "Missing param";
        }

        try {
            type = ItemType.valueOf(typeParam);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid type");
            return "Invalid type for get database table name";
        }
        switch (type) {
            case STOCK:
                tableName = ItemType.STOCK.getValue();
                break;
            case GENERATIVE:
                tableName = ItemType.GENERATIVE.getValue();
                break;
        }
        return tableName;
    }
}
