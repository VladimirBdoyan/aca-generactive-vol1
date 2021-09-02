package test;

import com.example.acageneractivweb.model.Group;
import com.example.acageneractivweb.repository.GroupRepository;
import com.example.acageneractivweb.util.idgenerator.GroupIdGenerator;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupRepositoryTest {
    GroupRepository groupRepository = GroupRepository.getInstance();
    @Test
    public void testAdd(){
        int sizeBefore = groupRepository.size();
        int currentIdBefore = GroupIdGenerator.getCURRENT();
        Group group = new Group(GroupIdGenerator.getNextId(), "ss");
        groupRepository.addGroup(group);
        int sizeAfter = groupRepository.size();
        int currentIdAfter = GroupIdGenerator.getCURRENT();
        assertEquals(sizeBefore+1,sizeAfter);
        assertEquals(currentIdBefore+1,currentIdAfter);
    }
    @Test
    public void testFindGroupById(){
        Group group = new Group(GroupIdGenerator.getNextId(), "ss");
        groupRepository.addGroup(group);
        assertEquals(group, groupRepository.findGroupById(GroupIdGenerator.getCURRENT()));
    }

//    @Test
//    public void testFindGroupByName(){
//        String name = "ss";
//        Group group = new Group(GroupIdGenerator.getNextId(), name);
//        groupRepository.addGroup(group);
//        assertEquals(group, groupRepository.findGroupByName(name));
//    }

}
