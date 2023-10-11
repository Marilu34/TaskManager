package tests.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sprint8.manager.TaskManager;
import sprint8.tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T manager;
    Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Task1", "Описание1", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 11);

    }
    public void setManager(T manager) {
        this.manager = manager;
    }

    @Test
    public void testGenerateID() {

        int id = manager.generateID();
        assertTrue(id > 0);
        int nextID = manager.generateID();
        assertEquals(id + 1, nextID);
    }


    Task createTask() {
        Task task1 = new Task("Описание Задачи", "Название Задачи", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 2);
        manager.createTask(task1);//создаем Таски
        return task1;
    }


    Epic createEpic() {//создаем Эпики
        Epic epic = new Epic("Описание Главной Задачи", "Название Главной Задачи", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 200000002);
        manager.createEpic(epic);
        return epic;
    }

    SubTask createSubTask() {//Создаем СабТаски
        Task epic = createEpic();
        SubTask subTask = new SubTask("SubTask3", "Описание",
                Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(3333, 3, 3, 5, 33),
                44, epic.getId());

        return subTask;
    }

    @Test
    public void shouldCreateTask() {//должен создасться Таск
        Task task = createTask();//Создается таск с помощью метода createTask()

        List<Task> tasks = manager.getAllTasks();//Получаем список всех задач с помощью метода getAllTasks() менеджера и сохраняем его в переменную tasks
        assertNotNull(task.getStatus());//Проверяем, что значение статуса задачи не равно null
        assertEquals(Status.NEW, task.getStatus());// Проверяем, что статус задачи равен Status.NEW
        assertEquals(tasks, manager.getAllTasks());// Проверяем, что список задач содержит только одну созданную задачу}
    }


    @Test
    public void shouldCreateEpic() {//должен создасться Эпик
        Epic epic = createEpic();

        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubtaskIds());
        assertEquals(epics, manager.getAllEpics());
    }

    @Test
    public void shouldCreateSubTask() {//должен создасться СабТаск
        Epic epic = createEpic();
        SubTask subtask = createSubTask();
        List<SubTask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(manager.getAllSubtasks(), subtasks);
        assertEquals(subtasks, epic.getSubtaskIds());
    }

    @Test
    void testUpdateStatus() {
        //Формирование первого Эпика с ПодЗадачами
        Epic epic = createEpic();
        assertEquals(0, manager.getAllSubtasks().size());
        //если у нас пустой список сабтасков.
        assertEquals(Status.NEW, epic.getStatus(), "Эпик в пустым списком задач должен иметь статус NEW!");
        //если все сабтаски со статусом NEW.
        SubTask subTask1 = new SubTask("Подзадача 1 ", "СабТаск 1", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.now(), 334, 1);
        SubTask subTask2 = new SubTask("Подзадача 2 ", "СабТаск 2", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.now(), 45678, 1);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик с подзадачами в статусе NEW должен иметь статус IN_PROGRESS!");
        //если все сабтаски со статусами NEW и DONE.
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.DONE);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик с подзадачами в статусе NEW и DONE должен иметь статус IN_PROGRESS!");
        //если сабтаски со статусом DONE
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик с подзадачей в статусе IN_PROGRESS должен иметь статус IN_PROGRESS!");
        //если сабтаски со статусом IN_PROGRESS.
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик со всеми подзадачами в статусе DONE должен иметь статус DONE!");
    }

    @Test
    public void getAllTask() {
        List<Task> task = new ArrayList<>() {
            Task task1 = createTask();
            Task task2 = createTask();
            Task task3 = createTask();
        };
        assertEquals(3, manager.getAllTasks().size());
    }

    //
    @Test
    public void getAllEpics() {
        List<Task> epics = new ArrayList<>() {
            Epic task1 = createEpic();
            Epic task2 = createEpic();
        };
        assertEquals(2, manager.getAllEpics().size());
    }

    @Test
    public void getAllSubTask() {
        SubTask sub = createSubTask();
        SubTask sub1 = createSubTask();
        manager.addSubtask(sub);
        manager.addSubtask(sub1);
        assertEquals(2, manager.getAllSubtasks().size());
    }

    @Test
    public void deleteAllTasks() {
        List<Task> task = new ArrayList<>() {
            Task task1 = createTask();
            Task task2 = createTask();
            Task task3 = createTask();

        };
        manager.deleteTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void deleteAllEpics() {
        List<Task> epics = new ArrayList<>() {
            Epic task1 = createEpic();
            Epic task2 = createEpic();
        };
        manager.deleteEpics();
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void deleteAllSubTask() {
        SubTask sub = createSubTask();
        SubTask sub1 = createSubTask();
        manager.addSubtask(sub);
        manager.addSubtask(sub1);
        manager.deleteSubTasks();
        assertEquals(0, manager.getAllSubtasks().size());
    }

    //Обновление Task
    @Test
    public void updateTask() {
        Task task1 = createTask();
        task1.setId(1);
        manager.updateTask(task1);
        assertEquals(task1, manager.getTaskById(1), "Задачи не совпадают");
    }

    //Обновление Subtask
    @Test
    public void updateSubtask() {
        SubTask sub1 = createSubTask();
        sub1.setId(2);
        manager.addSubtask(sub1);
        assertEquals(sub1, manager.getSubtaskById(2), "Задачи не совпадают");
    }

    //Обновление Epic
    @Test
    public void updateEpic() {
        Epic epic = createEpic();
        epic.setId(3);
        assertEquals(3, epic.getId(), "Задачи не совпадают");
    }


    @Test
    public void getByIDTasks() {
        List<Task> task = new ArrayList<>() {
            Task task1 = createTask();
            Task task2 = createTask();
            Task task3 = createTask();

        };
        assertEquals(1, manager.getTaskById(1).getId());
    }

    //Получение Epic по идентификатору
    @Test
    public void getByIDEpics() {
        List<Task> epics = new ArrayList<>() {
            Epic task1 = createEpic();
            Epic task2 = createEpic();
        };
        assertEquals(2, manager.getEpicById(2).getId());
    }

    //Получение Subtask по идентификатору
    @Test
    public void getByIDSubTasks() {
        SubTask sub = createSubTask();
        SubTask sub1 = createSubTask();
        manager.addSubtask(sub);
        manager.addSubtask(sub1);
        assertEquals(3, sub.getId());
    }


    //Удаление Task по идентификатору
    @Test
    public void deleteByIDTasks() {
        createTask();
        createTask();
        createTask();
        manager.deleteTaskById(1);
        List<Task> task = manager.getAllTasks();
        assertEquals(2, task.size());
    }

    //Удаление Epic по идентификатору
    @Test
    public void deleteByIDEpics() {
        createEpic();
        createEpic();
        manager.deleteEpicById(2);
        assertEquals(1, manager.getAllEpics().size());
    }

    //Удаление Subtask по идентификатору
    @Test
    public void deleteByIDSubtask() {
        SubTask sub = createSubTask();
        SubTask sub1 = createSubTask();
        manager.addSubtask(sub);
        manager.addSubtask(sub1);
        manager.deleteSubTaskById(1);
        assertEquals(2, manager.getAllSubtasks().size());
    }

    @Test
    public void ifDidNotFoundTasks() {//возвращаем пустой лист
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void ifDidNotFoundEpics() {//возвращаем пустой лист
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void ifDidNotFoundSubTasks() {//возвращаем пустой лист
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void ifDidNotFoundTasksById() {
        assertNull(manager.getTaskById(666));
    }

    @Test
    public void ifDidNotFoundEpicsById() {
        assertNull(manager.getEpicById(777));
    }

    @Test
    public void ifDidNotFoundSubTasksById() {
        assertNull(manager.getSubtaskById(888));
    }


    @Test
    public void testAddTask() {

        Task task = new Task("Описание Задачи", "Название Задачи", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 2000);
        manager.addTask(task);
        assertTrue(manager.getAllTasks().contains(task));
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Эпик 1 ", "Эпик 1", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 29090);
        manager.addEpic(epic);
        assertTrue(manager.getAllEpics().contains(epic));
    }

    @Test
    public void testAddSubtask() {
        SubTask subtask = createSubTask();
        manager.addSubtask(subtask);
        assertTrue(manager.getAllSubtasks().contains(subtask));
    }

    @Test
    public void testUpdateTimeEpic() {
        // Создаем тестовый эпик и список подзадач
        Epic epic = new Epic("Эпик 1 ", "Эпик 1", Status.NEW, TaskTypes.EPIC, LocalDateTime.of(2023, 4, 2, 10, 0), 3);
        List<SubTask> subTasks = new ArrayList<>();
        subTasks.add(new SubTask("Описание ПодЗадачи1", "Описание ПодЗадачи1", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(2023, 4, 2, 10, 0), 3, 1));
        subTasks.add(new SubTask("Описание ПодЗадачи2", "Описание ПодЗадачи2", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(2023, 4, 2, 10, 0), 3, 2));
        subTasks.add(new SubTask("Описание ПодЗадачи3", "Описание ПодЗадачи3", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(2023, 4, 2, 10, 0), 3, 3));
        epic.setId(1);
        epic.getSubtaskIds();

        // Проверяем, что начальная и конечная даты установлены правильно
        assertEquals(LocalDateTime.of(2023, 4, 2, 10, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 4, 2, 10, 3), epic.getEndTime());

        // Проверяем, что продолжительность эпика вычислена правильно
        assertEquals(3, epic.getDuration());
    }

}
