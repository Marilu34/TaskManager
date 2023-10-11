package tests.system;

import org.junit.jupiter.api.Test;
import sprint8.tasks.Epic;
import sprint8.tasks.Status;
import sprint8.tasks.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sprint8.tasks.Status.STATUS;

class EpicTest {


    // Тестирование метода addSubtaskId():

    @Test
    public void testAddSubtaskId() {

        Epic epic = new Epic("description", "name", STATUS, TaskTypes.TASK, LocalDateTime.now(), 2345);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        epic.addSubtaskId(1);
        epic.addSubtaskId(2);

        assertEquals(2, subtaskIds.size());
        assertTrue(subtaskIds.contains(1));
        assertTrue(subtaskIds.contains(2));
    }


    //Тестирование метода removeSubtask():

    @Test
    public void testRemoveSubtask() {
        Epic epic = new Epic("description1", "name1", STATUS, TaskTypes.TASK, LocalDateTime.now(), 3);
        Epic epic1 = new Epic("description2", "name2", STATUS, TaskTypes.TASK, LocalDateTime.now(), 5);
        epic.addSubtaskId(1);
        epic.addSubtaskId(2);
        epic.removeSubtask(1);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        assertEquals(1, subtaskIds.size());
        assertFalse(subtaskIds.contains(1));
        assertTrue(subtaskIds.contains(2));
    }


    // Тестирование метода getEndTime():

    @Test
    public void testGetEndTime() {
        LocalDateTime expectedEndTime = LocalDateTime.parse("2023-03-26T19:02:00");
        LocalDateTime startTime = LocalDateTime.parse("2023-03-26T19:00:00");
        long duration = 2;
        Epic epic = new Epic("description", "name", STATUS, TaskTypes.TASK, startTime, duration);
        LocalDateTime actualEndTime = epic.getEndTime();
        assertEquals(expectedEndTime, actualEndTime);
    }


    //Тестирование метода setEndTime():

    @Test
    public void testSetEndTime() {
        LocalDateTime startTime = LocalDateTime.parse("2023-03-26T19:00:00");
        long duration = 2;
        Epic epic = new Epic("description", "name", STATUS, TaskTypes.TASK, startTime, duration);
        LocalDateTime newEndTime = startTime.plus(Duration.ofHours(3));
        epic.setEndTime(newEndTime);
        assertEquals(newEndTime, epic.getEndTime());
    }

    //Тестирование метода equals():

    @Test
    public void testEquals() {
        Epic epic1 = new Epic("description", "name", STATUS, TaskTypes.TASK, LocalDateTime.of(1111, 1, 1, 11, 11), 2);
        epic1.addSubtaskId(1);
        epic1.addSubtaskId(2);

        Epic epic2 = new Epic("description", "name", STATUS, TaskTypes.TASK, LocalDateTime.of(1111, 1, 1, 11, 11), 2);
        epic2.addSubtaskId(1);
        epic2.addSubtaskId(2);

        Epic epic3 = new Epic("description", "name", STATUS, TaskTypes.TASK, LocalDateTime.now(), 2);
        epic3.addSubtaskId(1);

        assertTrue(epic1.equals(epic2));
        assertFalse(epic2.equals(epic3));
        assertFalse(epic1.equals(epic3));
        assertFalse(epic1.equals(null));
    }

    @Test
    public void testToString() {
        LocalDateTime startTime = LocalDateTime.parse("2023-03-26T19:00:00");
        long duration = 2;
        Epic epic1 = new Epic("description", "name", Status.NEW, TaskTypes.TASK, startTime, duration);
        epic1.addSubtaskId(1);
        epic1.addSubtaskId(2);

        String expectedString = "Epic{ subtasksIds=[1, 2], description='description', id=0', name='name', status=NEW', startTime='2023-03-26T19:00', endTime='2023-03-26T19:02', duration='2}";
        assertEquals(expectedString, epic1.toString());
    }

}