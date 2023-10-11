package sprint8.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    /*У эпика есть свой id и список id подзадач, а у подзадачи должен быть свой id и id эпика,
    к которому она относится. Подзадача не может существовать без эпика никак. Значит при создании нужно обязательно указывать id её эпика */
    int epicId;//айди Главных задач для списка Подзадач,к к-ому относится поДзадачи

    public SubTask(String description, String name, Status status, TaskTypes taskTypes, LocalDateTime startTime, long duration, int epicId) {
        super(description, name, status, taskTypes, startTime, duration);
        this.epicId = epicId;//для Epic
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
//id,type,name,status,description,startTime,duration,endTimeEpic,epicID
                ", id=" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", duration='" + getDuration() +
                ", epicId=" + getEpicId() + '}';
    }
}


