package com.example.acageneractivweb.repository;

import com.example.acageneractivweb.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRepository {
    private static ItemRepository itemInstance;
    private final List<Item> items = new ArrayList<>();

    public static ItemRepository getInstance() {
        if (itemInstance == null) {
            itemInstance = new ItemRepository();
        }
        return itemInstance;
    }

    public boolean addItem(Item item) {
        return items.add(item);
    }

    public boolean addItemAll(List<Item> itemlist) {
        return items.addAll(itemlist);
    }

    public Item findById(int id) {

        return items.stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .<IllegalArgumentException>orElseThrow(() -> {
                    throw new RuntimeException();
                });
    }

    public Item findByName(String name) {
        return items.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .<IllegalArgumentException>orElseThrow(() -> {
                    throw new RuntimeException();
                });
    }

    public List<Item> findHighestPricedItems() {
        Integer max = items.stream().
                max(Comparator.comparingInt(Item::getBasePrice)).
                get().
                getBasePrice();
        return items.stream().
                filter(item -> item.getBasePrice() == max).
                collect(Collectors.toList());
    }

    public List<Item> getItems() {

        return (ArrayList<Item>) items;
    }

    public int size() {
        return items.size();
    }
}
