package test;

import com.example.acageneractivweb.model.Group;
import com.example.acageneractivweb.model.Item;
import com.example.acageneractivweb.repository.GroupRepository;
import com.example.acageneractivweb.repository.ItemRepository;
import com.example.acageneractivweb.util.filereader.ItemFileReader;
import com.example.acageneractivweb.util.idgenerator.GroupIdGenerator;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestCRUD {
    static int GROUPING = 3;
    static String DIRECORY = "src/resources/Item.csv";

    GroupRepository groupRepository = GroupRepository.getInstance();

    @Test
    public void testIdGeneration() {
        int currentIdBefore = GroupIdGenerator.getCURRENT();
        Group group = new Group(GroupIdGenerator.getNextId(), "group1");
        int currentIdAfter = GroupIdGenerator.getCURRENT();
        assertEquals(currentIdBefore + 1, currentIdAfter);
    }

    @Test
    public void testReadCsvFileDirectory() {
        assertThrows(RuntimeException.class, () -> ItemFileReader.readScv("wrong Directory"));
    }

    @Test
    void testReadCsvAndCreateItem() {
        GroupIdGenerator.setCURRENT(0);
        for (int i = 1; i <= GROUPING; i++) {
            groupRepository.addGroup(new Group(GroupIdGenerator.getNextId(), "Group " + i));
        }
        ItemFileReader.readScv(DIRECORY);

        assertEquals(GROUPING, groupRepository.size());

        // Test the show if Items add in Repository by their constructor

        Optional<Item> o = ItemRepository.getInstance().getItems().stream().findFirst();
        if (o.isPresent()) {
            Item item = o.get();
            assertEquals(1, item.getId());
            assertEquals(100, item.getBasePrice());
            assertEquals("Test1", item.getName());
        }
    }
}