package tests.system;

import org.junit.jupiter.api.BeforeEach;
import sprint8.manager.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }
   }