package sprint8.manager;


import sprint8.tasks.Epic;
import sprint8.tasks.SubTask;
import sprint8.tasks.Task;

import java.util.List;

public interface TaskManager {


    int generateID();

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubtasks();

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubTasks();

    List<Task> getHistory();

    void remove(int id);

    void addHistory(int id);
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(SubTask subtask);
}
