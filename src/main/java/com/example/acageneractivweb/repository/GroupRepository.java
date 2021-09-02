package com.example.acageneractivweb.repository;



import com.example.acageneractivweb.model.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupRepository {

    private static GroupRepository sInstance;

    private final List<Group> groups = new ArrayList<>();

    public static GroupRepository getInstance() {
        if (sInstance == null) {
            sInstance = new GroupRepository();
        }

        return sInstance;
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public void addGroupAll(List<Group> groups) {
        this.groups.addAll(groups);
    }

    public Group findGroupById(int groupId) {
        for (Group group : groups) {
            if (group.getId() == groupId) {
                return group;
            }
        }

        return null;
    }

//    public Group findGroupByName(String name) {
//        return groups.stream().filter(group -> group.getName().equals(name)).findFirst().orElseThrow(() -> {
//            throw new RuntimeException();
//        });
//    }

    public List<Group> getGroupsHierarchy() {
        List<Group> rootGroups = new ArrayList<>();

        for (Group group : groups) {
            if (group.getParentGroup() == null) {
                rootGroups.add(group);
            }
        }
        return rootGroups;
    }

    public List<Group> findSubGroupsByParent(Group parent) {
        return groups.stream().filter(group -> group.getParentGroup() == parent).collect(Collectors.toList());
    }

    public int size() {
        return groups.size();
    }

    private GroupRepository() {

    }
}
