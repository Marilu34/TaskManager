package sprint8.manager;


import sprint8.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList list = new CustomLinkedList();
    private final Map<Integer, Node> table = new HashMap<>();

    @Override
    public void add(Task task) {
        Node node = table.get(task.getId());
        if (node != null) {
            list.removeNode(node);
        }
        list.linkLast(task);
        table.put(task.getId(), list.tail);
    }

    @Override
    public void remove(int id) {
        Node node = table.get(id);
        if (node != null) {
            list.removeNode(node);
            table.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return list.getTasks();
    }
}

class CustomLinkedList {
    private final Map<Integer, Node> table = new HashMap<>();
    public Node head;
    public Node tail;

    public void linkLast(Task task) {
        final Node node = new Node(tail, task, null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    public List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node element = head;
        while (element != null) {
            result.add(element.element);
            element = element.next;
        }
        return result;
    }

    public void removeNode(Node node) {
        if (node != null) {
            table.remove(node.getElement().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();
            if (head.equals(node)) {
                head = node.getNext();
            }
            if (tail.equals(node)) {
                tail = node.getPrev();
            }

            if (prev != null) {
                prev.setNext(next);
            }

            if (next != null) {
                next.setPrev(prev);
            }
        }
    }

    Node getNode(int id) {
        return table.get(id);
    }
}

