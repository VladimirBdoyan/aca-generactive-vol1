package com.example.acageneractivweb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group {
    private int id;
    private String name;
    private Group parentGroup;
    private List<Group> subGroups = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public Group getParentGroup() {
        return parentGroup;
    }

    void setParentGroup(Group parentGroup) {
        this.parentGroup = parentGroup;
    }

    public void addSubGroup(Group group) {
        this.subGroups.add(group);
        group.setParentGroup(this);
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setGroup(this);
    }

    public void addItems(List<Item> items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public void print(int level) {
        System.out.printf("GROUP - id: {%d} {%s}%n", id, name);
        printSubGroups(++level);
        printItems(level);
    }

    private void printSubGroups(int level) {

        String subLevelPrefix = "  ";
        subLevelPrefix = repeat(subLevelPrefix,level);
        for (Group group : subGroups) {
            System.out.print(subLevelPrefix);
            group.print(level);
        }
    }

    private void printItems(int level) {
        String subLevelPrefix = "  ";
        subLevelPrefix = repeat(subLevelPrefix,level);
        for (Item item : items) {
            System.out.print(subLevelPrefix);
            item.print();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    private String repeat(String str, int level) {
        StringBuilder stringBuilder = new StringBuilder(str);
        for (int i = 0; i < level; i++) {
            stringBuilder.append(str);
        }
        str = new String(stringBuilder);
        return str;
    }
}
