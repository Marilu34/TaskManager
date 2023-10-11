package sprint8.manager;


import sprint8.tasks.Epic;
import sprint8.tasks.Status;
import sprint8.tasks.SubTask;
import sprint8.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    private HashMap<Integer, Task> tasks = new HashMap<>();//мапа для Задач
    private HashMap<Integer, Epic> epics = new HashMap<>();//мапа для Главных Задач
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();//мапа ля ПодЗадач

    private Map<LocalDateTime, Task> validateTask = new TreeMap<>();
    private int id = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    public HashMap<Integer, Task> getTaskMap() {
        return tasks;
    }

    public void setTaskMap(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, Epic> getEpicMap() {
        return epics;
    }

    public void setEpicMap(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTasks;
    }

    public void setSubTaskMap(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public int generateID() {//Для генерации идентификаторов числовое поле класса менеджер увеличиваем его на 1
        return ++id;//сначала увеличиваем,потмо кладем.начинаем с 1.
    }

    @Override
    public Task createTask(Task task) { //создать Задачи
        int newTaskId = generateID();//создаем айди
        task.setId(newTaskId);//присваизаем айди новому таску
        tasks.put(newTaskId, task);//кладем в мапу и айди, и таск
        if (isValidateTask(task)) {
            tasks.put(newTaskId, task); // Складываем в мапу. Ключами будут сгенерированные ID, значения - объекты типа Task
        }
        return task;
    }

    public Task createTaskFromFile(Task task) { //создать Задачи
        tasks.put(task.getId(), task);//кладем в мапу и айди, и таск
        if (isValidateTask(task)) {
            tasks.put(task.getId(), task); // Складываем в мапу. Ключами будут сгенерированные ID, значения - объекты типа Task
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {//создать Главные Задачи
        int newEpicId = generateID();//создаем айди
        epic.setId(newEpicId);//присваизаем айди новому эпику
        epics.put(newEpicId, epic);//кладем в мапу и айди, и эпик
        if (epic.getStartTime() != null) {
            if (isValidateTask(epic)) {
                epics.put(newEpicId, epic); // Складываем в мапу. Ключами будут сгенерированные ID, значения - объекты типа Task
            }
            updateTimeEpic(epic);
        }
        return epic;
    }

    public Epic createEpicFromFile(Epic epic) {//создать Главные Задачи
        epics.put(epic.getId(), epic);//кладем в мапу и айди, и эпик
        if (isValidateTask(epic)) {
            epics.put(epic.getId(), epic); // Складываем в мапу. Ключами будут сгенерированные ID, значения - объекты типа Task
        }
        updateTimeEpic(epic);

        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {//создать ПодЗадачи
        Integer newSubTasksId = generateID();//создаем айди
        subTask.setId(newSubTasksId);//присваизаем айди новому сабтаску
        Epic epic = epics.get(subTask.getEpicId());// Достаю из списка эпиков таск который лежит в сабтаске
        if (epic == null) {
            System.out.println("Такой Главной Задачи не существует");
        }//если эпик не нулевой
        subTasks.put(newSubTasksId, subTask);////кладем в мапу и айди, и сабтаск
        epic.addSubtaskId(newSubTasksId);//в лист сабтасков эпика кладем айди
        if (isValidateTask(subTask)) {
            epic.addSubtaskId(newSubTasksId); //в лист сабтасков эпика кладем айди
            subTasks.put(newSubTasksId, subTask); //кладем в мапу и айди, и сабтаск
        }
        updateStatusEpic(epic);//обновляем статус эпика
        updateTimeEpic(epic);
        return subTask;
    }

    public SubTask createSubTaskFromFile(SubTask subTask) {//создать ПодЗадачи
        Epic epic = epics.get(subTask.getEpicId());// Достаю из списка эпиков таск который лежит в сабтаске
        if (epic == null) {
            System.out.println("Такой Главной Задачи не существует");
        }//если эпик не нулевой
        subTasks.put(subTask.getId(), subTask);////кладем в мапу и айди, и сабтаск
        epic.addSubtaskId(subTask.getId());//в лист сабтасков эпика кладем айди
        if (isValidateTask(subTask)) {
            epic.addSubtaskId(subTask.getId()); //в лист сабтасков эпика кладем айди
            subTasks.put(subTask.getId(), subTask); //кладем в мапу и айди, и сабтаск
        }
        updateStatusEpic(epic);//обновляем статус эпика
        updateTimeEpic(epic);

        return subTask;
    }

    @Override
    public Task getTaskById(int id) {//получить Задачи по айди
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id)); // Добавляем задачу в историю
            return tasks.get(id);
        } else { //Тут скорее всего нужно обернуть в эксепшен, но не уверен
            return null; // Не придумал как по-нормальному избежать null
        }
    }

    @Override
    public Epic getEpicById(int id) {//получить Главные Задачи по айди
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id)); // добавляем эпик в список истории
            return epics.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubtaskById(int id) {//получить ПодЗадачи по айди
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id)); // добавляем подзадачу в список истории
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getAllTasks() {//получить Задачи
        return new ArrayList<>(tasks.values());//
    }

    @Override
    public List<Epic> getAllEpics() {//получить Главные Задачи
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubtasks() {//получить ПодЗадачи
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void updateTask(Task task) {//обновить Задачи
        if (tasks.containsKey(task.getId())) {//проверяем,если таски содержат таск с конкретным айди
            tasks.put(task.getId(), task);// обновляем - кладем в мапу таск айди и таск
        }
    }

    @Override
    public void updateEpic(Epic epic) {//обновить Главные Задачи
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {//обновить ПодЗадачи
        int id = subTask.getId();
        int epicID = subTask.getEpicId();
        if (subTasks.containsKey(id) && (epics.containsKey(epicID))) {//проверяем,если сабтаски содержат таск с конкретным айди
            subTasks.put(id, subTask);//обновляем мапу -кладем в него айди и таск
            updateStatusEpic(getEpicById(epicID));//обновляем эпики,тк в них лист сабтасков по айди
        }
    }

    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            int newCount = 0;
            int doneCount = 0;
            for (Integer subtaskId : epic.getSubtaskIds()) {
                SubTask subtask = subTasks.get(subtaskId);
                if (subtask.getStatus() == Status.NEW) {
                    newCount++;
                } else if (subtask.getStatus() == Status.DONE) {
                    doneCount++;
                }
            }
            if (newCount == 0 && doneCount == 0) {
                epic.setStatus(Status.NEW);
            } else if (doneCount == epic.getSubtaskIds().size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
            epics.put(epic.getId(), epic);
        }
    }


    @Override
    public void deleteTaskById(int id) {//удалить по айди Задачи
        if (tasks == null) {
            return;
        }
        if (tasks.containsKey(id)) {//если таски содержат таск с конкретным
            tasks.remove(id);//удаляем таск по айди в тасках
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {//удалить по айди Главные Задачи
        Epic epic = epics.get(id);//достаем из эпиков эпик по айди
        if (epic == null) {
            return;
        }//если нашли
        for (Integer subtaskId : epic.getSubtaskIds()) {//проходимся по списку сабтасков (по айди эпика)
            subTasks.remove(subtaskId);//удаляем по айди сабтаски,  связанные с конкретным эпиком
        }
        epics.remove(id);//удаляем по айди сам эпик
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {//удалить по айди ПодЗадачи
        SubTask subtask = subTasks.get(id);//достаем по айди сабтаск
        if (subtask == null) {
            return;
        }//если нашли
        Epic epic = epics.get(subtask.getEpicId());//получаем по айди эпика листа сабтасков из эпиков
        epic.removeSubtask(id);//по айди сабтаска удаляем сабатаск эпика
        updateStatusEpic(epic);//обновляем статус эпика
        subTasks.remove(id);//удаляем по айди сабтаск
        historyManager.remove(id);
    }

    @Override
    public void deleteTasks() { // Удалить 3адачи
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();//удаляем все
    }

    @Override
    public void deleteEpics() {   // Удалить Главные Задачи
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() { // Удалить ПодЗадачи
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    @Override
    public void addHistory(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
        } else if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
    }

    @Override
    public void addTask(Task task) {
        createTask(task);

    }

    @Override
    public void addEpic(Epic epic) {
        createEpic(epic);
    }

    @Override
    public void addSubtask(SubTask subtask) {
        createSubTask(subtask);
    }

    public void updateTimeEpic(Epic epic) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        List<SubTask> subTasksByEpicId = getSubtasksByEpicId(epic.getId());
        long duration = 0;
        for (SubTask subTask : subTasksByEpicId) {
            if (startTime == null || subTask.getStartTime().isBefore(startTime)) {
                startTime = subTask.getStartTime();
            }
            if (endTime == null || subTask.getEndTime().isAfter(endTime)) {
                endTime = subTask.getEndTime();
            }
            Duration subtaskDuration = Duration.between(subTask.getStartTime(), subTask.getEndTime());
            duration += subtaskDuration.toMillis();
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    public List<SubTask> getSubtasksByEpicId(int epicId) {
        List<SubTask> subTasksByEpicId = new ArrayList<>();
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            for (Integer subTaskId : epic.getSubtaskIds()) {
                SubTask subTask = getSubtaskById(subTaskId);
                if (subTask != null) {
                    subTasksByEpicId.add(subTask);
                }
            }
        }
        return subTasksByEpicId;
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(validateTask.values());
    }

    private boolean isValidateTime(Task task2) {
        if (validateTask.isEmpty()) {
            return true;
        }

        if (task2.getStartTime() == null) {
            task2.setStartTime(LocalDateTime.MAX);
        }
        for (Task task : validateTask.values()) {
            LocalDateTime startTask1 = task.getStartTime();
            LocalDateTime startTask2 = task2.getStartTime();
            LocalDateTime endTask2 = task2.getEndTime();
            LocalDateTime endTask1 = task.getEndTime();
            if (task.getStartTime() == null) {
                continue;
            }
            if ((startTask1.isBefore(endTask2) || startTask1.equals(endTask2)) &&
                    (endTask1.isAfter(startTask2) || endTask1.equals(startTask2))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidateTask(Task task) {
        boolean isTaskValidate = isValidateTime(task);
        if (isTaskValidate) {
            validateTask.put(task.getStartTime(), task);
        }
        return isTaskValidate;
    }

}