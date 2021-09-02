package com.example.acageneractivweb.repository;

import com.example.acageneractivweb.model.Basket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BascketRepository {

    private static BascketRepository bascketInstance;
    private final List<Basket> baskets = new ArrayList();

    public BascketRepository getBascketInstance() {
        if (bascketInstance == null) {
            bascketInstance = new BascketRepository();
        }
        return bascketInstance;
    }

    public boolean addBascket(Basket basket) {
        return baskets.add(basket);
    }

    public boolean addAllBascket(List<Basket> basketList) {
        return baskets.addAll(basketList);
    }
    public boolean removeBascket(Basket basket){
        return baskets.remove(basket);
    }

    public List<Basket> findAllOrderByPrice(int price) {

       return baskets.stream()
               .filter(basket -> basket.getTotalPrice() >= price)
               .collect(Collectors.toList());
    }

}
