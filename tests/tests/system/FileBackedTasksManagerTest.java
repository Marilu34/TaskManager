package tests.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sprint8.manager.FileBackedTaskManager;
import sprint8.manager.Managers;
import sprint8.tasks.*;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    public static final Path path = Path.of("resources/data.csv");
    File file = new File(String.valueOf(path));

//    FileBackedTaskManager manager;
//    @AfterEach
//    public void afterEach() {
//        try {
//            Files.delete(path);
//        } catch (IOException exception) {
//            System.out.println(exception.getMessage());
//        }
//    }


    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(Managers.getDefaultManager(), file);
    }


    @Test
    void saveFile() {
        //       FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);  // если список задач пустой
        assertDoesNotThrow(manager::save
                , "Сохранение менеджера с пустым списком задач не должно вызывать исключений!");

        //если эпик без сабтасков
        Epic epic = new Epic("Описание Эпика", "Название Эпика", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 3);
        manager.addTask(epic);
        manager.save();
        //   FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, manager.getAllTasks().size(),
                "Количество задач после восстановления не совпало!");

        //если список истории пустой
        assertEquals(0, manager.getHistory().size(),
                "Количество задач в истории после восстановления не совпало!");

    }

    //
//    //Тестирование метода загрузки списка задач из файла
    @Test
    public void loadFromFile() {   //public static FileBackedTasksManager loadFromFile(Path file)
        Epic epic = new Epic("Описание Эпика", "Название Эпика", Status.NEW, TaskTypes.EPIC, LocalDateTime.of(1111, 1, 1, 11, 11), 2);
        manager.addEpic(epic);
        epic = new Epic("Описание Эпика 1", "Название Эпика 1", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 2);
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Описание СабТаска", "Название СабТаска", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.now(), 23, epic.getId());
        manager.addSubtask(subTask);
        SubTask subTask2 = new SubTask("Описание СабТаска 1", "Название СабТаска 1", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.now(), 3344, epic.getId());
        manager.addSubtask(subTask2);
        Task task = new Task("Описание Таска", "Название Таска", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 20020);
        manager.addTask(task);
        manager.getAllTasks();
        //Добавление истории
        manager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, manager.getAllTasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, manager.getAllEpics().size(), "Количество эпиков менеджера после восстановления не совпало!");
        assertEquals(2, manager.getAllSubtasks().size(), "Количество подзадач менеджера после восстановления не совпало!");
    }
}