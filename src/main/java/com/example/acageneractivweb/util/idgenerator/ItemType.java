package com.example.acageneractivweb.util.idgenerator;

public enum ItemType {
    STOCK ("stock_items"), GENERATIVE ("generative_items");

    private String tableName;

    ItemType(String tableName) {
        this.tableName = tableName;
    }
    public String getValue(){
        return this.tableName;
    }

    public static void main(String[] args) {
        System.out.println(ItemType.STOCK.getValue());

    }
}


