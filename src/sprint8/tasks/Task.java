package sprint8.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String description;
    private int id;
    private String name;
    private Status status;
    private TaskTypes taskTypes;          //Тип задачи
    //Название

    private LocalDateTime startTime;  //Дата начала
    private long duration;

    public Task(String description, String name, Status status, TaskTypes taskTypes, LocalDateTime startTime, long duration) {
        this.description = description;
        this.name = name;
        this.status = status;
        this.taskTypes = taskTypes;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String description, String name) {
        this.description = description;
        this.name = name;
        this.status = Status.NEW;
        this.startTime = null;
        this.duration = 0;
    }

    public String getDescription() {
        return description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskTypes getType() {
        return taskTypes;
    }

    //Получение длительности выполнения задачи
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    //Получение даты начала выполнения задачи
    public LocalDateTime getStartTime() {
        return startTime;
    }

    //Задание даты начала выполнения задачи
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    //Вычисление времени окончания задачи
    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(duration);
    }


    //Отображение задачи

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(name, task.name) && Objects.equals(status, task.status) && Objects.equals(taskTypes, task.taskTypes) && Objects.equals(startTime, task.startTime) && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, name, status, taskTypes, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ID=" + id +
                ", status=" + status +
                ", type=" + taskTypes +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
