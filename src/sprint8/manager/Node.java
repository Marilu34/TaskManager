package sprint8.manager;

import sprint8.tasks.Task;

class Node {

    Node prev;
    Task element;
    Node next;

    public Node(Node prev, Task element, Node next) {
        this.prev = prev;
        this.element = element;
        this.next = next;

    }


    public Task getElement() {
        return element;
    }

    public void setElement(Task element) {
        this.element = element;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
