package sprint8.manager;

import sprint8.exceptions.ManagerSaveException;
import sprint8.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static sprint8.tasks.TaskTypes.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private TaskManager taskManagerDefault;
    //    private TaskManager taskDefault;
    protected File file;

    private static final String CSV_HEADER = "id,type,name,status,description,startTime,duration,endTimeEpic,epicID" + "\n";

    public FileBackedTaskManager() {

    }

    public FileBackedTaskManager(TaskManager taskManagerDefault, File file) {
        this.taskManagerDefault = Managers.getDefaultManager();
        this.file = file;
    }

//    public FileBackedTaskManager(TaskManager taskDefault) {
//        this.taskDefault = Managers.getDefault();
//    }


    public static void main(String[] args) throws IOException {
//
        Path path = Path.of("resources/data.csv");
        File file = new File(String.valueOf(path));

        FileBackedTaskManager file1 = new FileBackedTaskManager(Managers.getDefaultManager(), file);

        //Создаём несколько задач
        Task task1 = new Task("Task1", "Описание Таска", Status.DONE, TASK,
                LocalDateTime.of(1111, 1, 1, 11, 11), 11);
        file1.createTask(task1);

        Epic epic1 = new Epic("Epic1", "Описание Эпика 1", Status.NEW, EPIC, LocalDateTime.of(1111, 1, 1, 11, 11), 11);
        file1.createEpic(epic1);

        Epic epic2 = new Epic("Epic2", "Описание Эпика2", Status.NEW, EPIC, LocalDateTime.of(1111, 1, 1, 11, 11), 11);
        file1.createEpic(epic2);

        SubTask subTask1 = new SubTask("SubTask1", "Описание Сабтаска1", Status.DONE, TaskTypes.SUBTASK, LocalDateTime.of(2222, 2, 2, 22, 22),
                22, epic1.getId());
        file1.createSubTask(subTask1);

        SubTask subTask4 = new SubTask("SubTask2", "Описание Сабтаска2",
                Status.IN_PROGRESS, TaskTypes.SUBTASK, LocalDateTime.of(3333, 3, 3, 3, 33),
                33, epic2.getId());
        file1.createSubTask(subTask4);

        //Смотрим задачи и добавляем в историю
        file1.getTaskById(task1.getId());
        file1.getEpicById(epic1.getId());

        file1.getHistory();
        // Файл сохранён и заполнен

        //Создаём новый объект для восстановления из файла
        FileBackedTaskManager fileDownLoad = FileBackedTaskManager.loadFromFile(file);

        // Проверка через консоль.
        System.out.println("Вывод мап с задачами, жэпиками и подзадачами");
        System.out.println(fileDownLoad.getAllTasks());
        System.out.println(fileDownLoad.getAllEpics());
        System.out.println(fileDownLoad.getAllSubtasks());
        System.out.println();

        //Показ истории
        System.out.println("История");
        System.out.println(fileDownLoad.getHistoryManager().getHistory());
        System.out.println();


    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fb = new FileBackedTaskManager(Managers.getDefaultManager(), file);
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) { // пропустить заголовок
                String dataFromFile = lines.get(i);
                if (!dataFromFile.isBlank()) {
                    Task task = fb.fromString(dataFromFile);//fromString()-метод создания задачи из строки
                    if (task instanceof Epic) {
                        fb.addEpicFromFile((Epic) task);
                    } else if (task instanceof SubTask) {
                        fb.addSubtaskFromFile((SubTask) task);
                    } else {
                        fb.addTaskFromFile(task);
                    }
                }
            }
            for (int id : historyFromString(lines.get(lines.size() - 1))) {//historyFromString()-метод сохранения и восстановления менеджера истории из CSV
                fb.addHistory(id);
            }
            return fb;
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(CSV_HEADER);
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubtasks()) {
                writer.write(toString(subTask) + "\n");
            }
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            writer.write("\n");
            writer.write("\n" + (historyToString(Managers.getDefaultHistory())));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в  файл ");
        }
    }


    private static TaskTypes getType(Task task) {
        if (task instanceof Epic) {
            return EPIC;
        } else if (task instanceof SubTask) {
            return TaskTypes.SUBTASK;
        }
        return TASK;
    }

// Метод сохранения задачи в строку

    public String toString(Task task) {
        String startTime = task.getStartTime() == null ? "1900-01-01T00:00:00.000000" : task.getStartTime().toString();
        String endTime = task.getEndTime() == null ? "1900-01-01T00:00:00.000000" : task.getEndTime().toString();
        if (task instanceof Epic) {
            return task.getId() + "," + EPIC.name() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTime + "," + task.getDuration() + "," + endTime + ",";
        } else if (task instanceof SubTask) {
            return task.getId() + "," + SUBTASK.name() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTime + "," + task.getDuration() + "," + endTime + "," + ((SubTask) task).getEpicId();
        }
        return task.getId() + "," + TASK.name() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + startTime + "," + task.getDuration() + "," + endTime + ",";
    }//Здесь надо проверить, является ли task - subtask или epic, если является, то по id найти какому epic принадлежит этот subtask или сам epic в TaskManager и добавить сюда.

    private Task fromString(String value) {
        String[] params = value.split(",");
        System.out.println(value);
        String id = params[0];
        TaskTypes type = TaskTypes.valueOf(params[1]);
        String name = params[2];
        Status status = Status.valueOf(params[3].toUpperCase());
        String description = params[4];
        LocalDateTime startTime = LocalDateTime.parse(params[5]);
        long duration = Long.parseLong(params[6]);
        switch (type) {
            case EPIC:
                Epic epic = new Epic(description, name, status, EPIC, startTime, duration);
                epic.setId(Integer.parseInt(id));
                return epic;
            case SUBTASK:
                int epicId = params.length >= 9 ? Integer.parseInt(params[8]) : 0;
                SubTask subTask = new SubTask(description, name, status, TaskTypes.SUBTASK, startTime, duration, epicId);
                subTask.setId(Integer.parseInt(params[0]));
                return subTask;
            case TASK:
                Task task = new Task(description, name, status, TASK, startTime, duration);
                task.setId(Integer.parseInt(id));
                return task;
        }
        return null;
    }


    public static String historyToString(HistoryManager manager) {// Метод для сохранения истории в CSV
        List<Task> savedHistory = manager.getHistory();
        if (savedHistory.isEmpty()) {
            return ""; // возвращаем пустую строку, если история пустая
        }
        StringBuilder str = new StringBuilder();

        for (Task task : savedHistory) {
            str.append(task.getId()).append(",");
        }
        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }
        return str.toString();
    }


    public static List<Integer> historyFromString(String value) {// Метод восстановления менеджера истории из CSV
        if (value == null || value.isEmpty()) { // проверяем, что строка не пустая
            return new ArrayList<>();
        }
        List<Integer> restoredHistory = new ArrayList<>();
        String[] ids = value.split(","); // изменяем название переменной на множественное число,
        for (String id : ids) { // изменяем название переменной на ед. число
            restoredHistory.add(Integer.parseInt(id.trim())); // добавляем id в список, удаляем пробелы
        }
        return restoredHistory;
    }

    @Override
    public void addTask(Task task) {
        super.createTask(task);
        save();
    }

    public void addTaskFromFile(Task task) {
        super.createTaskFromFile(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    public void addEpicFromFile(Epic epic) {
        super.createEpicFromFile(epic);
        save();
    }

    @Override
    public void addSubtask(SubTask subtask) {
        super.createSubTask(subtask);
        save();
    }

    public void addSubtaskFromFile(SubTask subtask) {
        super.createSubTaskFromFile(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void remove(int id) {
        super.remove(id);
        save();
    }
}