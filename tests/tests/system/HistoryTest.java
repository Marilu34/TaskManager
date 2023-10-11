package tests.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sprint8.manager.HistoryManager;
import sprint8.manager.InMemoryHistoryManager;
import sprint8.manager.InMemoryTaskManager;
import sprint8.tasks.Epic;
import sprint8.tasks.SubTask;
import sprint8.tasks.Task;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryTest extends TaskManagerTest<InMemoryTaskManager>{
    HistoryManager history;
    @BeforeEach
    public void beforeEach() {
               history = new InMemoryHistoryManager();
       manager = new InMemoryTaskManager();
    }

    @Test
    void checkHowPartsOfHistoryDelete() {
        //история без задач
        assertNotNull(history, "история не пуста");
        assertEquals(0, history.getHistory().size(), "история должна быть пуста");

        task = createTask();

        history.add(task);
        assertEquals(1, history.getHistory().size(), "только одна задача!");

        //дублирование
        manager.addHistory(task.getId());  //повтор
        assertEquals(1, manager.getHistory().size(), "только одна задача");
        manager.remove(task.getId());
        //удалаляем: начало, середина, конец.
        Task beginning = createTask();
        manager.addHistory(beginning.getId());

        Task middle = createTask();
        manager.addHistory(middle.getId());

        Task ending = createTask();
        manager.addHistory(ending.getId());

        history.remove(beginning.getId());
        assertEquals(1, history.getHistory().size(), "начало не была удалено");

        history.remove(middle.getId());
        assertEquals(1, history.getHistory().size(), "середина не была удалена");

        history.remove(ending.getId());
        assertEquals(1, history.getHistory().size(), "конец не был удален");
    }
    //Дублирование
    @Test
    void duplication() {
        Task task1 = createTask();
        Task task2 = createTask();
        task1.setId(1);
        task2.setId(2);

        manager.getTaskById(1);
        manager.getTaskById(2);

        final List<Task> history = manager.getHistory();
        int counterIdTask1 = 0;
        int counterIdTask2 = 0;
        for (Task task : history) {
            if (task.getId() == 1) {
                counterIdTask1 ++;
            }
            if (task.getId() == 2) {
                counterIdTask2 ++;
            }
        }
        assertEquals(1, counterIdTask1, "Есть Дубликат");
        assertEquals(1, counterIdTask2, "Есть дубликат");
        assertEquals(2, history.size(), "Нет дублирования задач");
    }

    @Test
    public void testRemove() {
        Task task1 = createTask();
        manager.addHistory(task1.getId());
        manager.remove(task1.getId());
        assertFalse(manager.getHistory().contains(task));
    }

    @Test
    public void testAddAndGetHistory() {

        Task task = createTask();
        manager.addTask(task);
        Epic epic = createEpic();

        SubTask subtask = createSubTask();
        manager.addSubtask(subtask);
        manager.addHistory(epic.getId());//добавляем в историю
        manager.addHistory(task.getId());
        manager.addHistory(subtask.getId());

        assertTrue(manager.getHistory().contains(epic));//получаем
        assertTrue(manager.getHistory().contains(task));
        assertTrue(manager.getHistory().contains(subtask));
    }
    //проверяем getHistory()
    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void ifDoesNotFoundTaskWillBeEmptyHistory() {
        manager.getTaskById(333);
        manager.getSubtaskById(333);
        manager.getEpicById(333);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        SubTask sub = createSubTask();
        SubTask sub1 = createSubTask();
        manager.addSubtask(sub);
        manager.addSubtask(sub1);
        manager.getEpicById(1);
        manager.getSubtaskById(4);
        List<Task> list = manager.getHistory();
        assertEquals(4, list.size());
        assertTrue(list.contains(manager.getEpicById(1)));
        assertTrue(list.contains(manager.getSubtaskById(4)));
    }

    @Test
    public void testDeleteAllTasksInHistory() {
        SubTask subtask = createSubTask();
        manager.addSubtask(subtask);
        manager.deleteEpics();
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

}
