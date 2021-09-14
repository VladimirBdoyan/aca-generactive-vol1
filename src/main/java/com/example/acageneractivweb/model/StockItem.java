package com.example.acageneractivweb.model;

public class StockItem extends Item {
    public StockItem(int id, int basePrice, String name) {
        super(id, basePrice, name);
    }
    public StockItem(){

    }

    @Override
    public void setId(long id) {
        super.setId(id);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setBasePrice(int basePrice) {
        super.setBasePrice(basePrice);
    }

    @Override
    public int calculatePrice(Configuration configuration) {
        return getBasePrice() * configuration.getResolution().getCoefficient();
    }
}
