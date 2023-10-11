package sprint8.manager;

import sprint8.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();//должен возвращать последние 10 просмотренных задач
}
