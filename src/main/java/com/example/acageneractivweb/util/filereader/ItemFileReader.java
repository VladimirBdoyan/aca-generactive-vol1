package com.example.acageneractivweb.util.filereader;


import com.example.acageneractivweb.model.StockItem;
import com.example.acageneractivweb.repository.GroupRepository;
import com.example.acageneractivweb.repository.ItemRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ItemFileReader {
    public static void readScv(String url) {
        File file = new File(url);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("Wrong File directory");
        }
        while (sc.hasNext()) {
            String[] itemData = sc.nextLine().split(",");
            int id = Integer.parseInt(itemData[0]);
            int basePrice = Integer.parseInt(itemData[1]);
            String name = itemData[2];
            String imageUrl = itemData[3];
            int groupId = Integer.parseInt(itemData[4]);
            StockItem item = new StockItem(id, basePrice, name);
            item.setImageUrl(imageUrl);
            GroupRepository.getInstance().findGroupById(groupId).addItem(item);
            ItemRepository.getInstance().addItem(item);
        }
    }
}
