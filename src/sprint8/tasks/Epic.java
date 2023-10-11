package sprint8.tasks;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;//лист id ПодЗадач в Главных Задачах
    private LocalDateTime endTime;

    public Epic(String description, String name, Status status, TaskTypes taskTypes, LocalDateTime startTime, long duration) {
        super(description, name, status, taskTypes, startTime, duration);
        this.endTime = super.getEndTime();
        this.subtaskIds = new ArrayList<>(); // инициализация списка subtaskIds
    }

    public List<Integer> getSubtaskIds() {
        if (subtaskIds == null) {
            return new ArrayList<>();
        }
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        getSubtaskIds().add(id);
    }


    public void removeSubtask(int id) {
        getSubtaskIds().remove(Integer.valueOf(id));
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }


    @Override
    public String toString() {
        return "Epic{ subtasksIds=" + getSubtaskIds() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration='" + getDuration() +
                '}';
    }
}

