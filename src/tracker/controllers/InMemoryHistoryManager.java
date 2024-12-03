package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();
    private Map<Integer, Node<Task>> linkHistory = new HashMap<>();
    private Node head;
    private Node tail;

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(null, task, oldTail);
        tail = newTail;
        if (oldTail == null) {
            tail = newTail;
        } else {
            oldTail.prev = newTail;
        }
    }

    public List<Task> getTasks() {
        Node<Task> currentNode = head;
        while (currentNode != null) {
            history.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return history;
    }

    @Override
    public void addTask(Task task) {
        if (linkHistory.containsKey(task.getId())) {
         return;
        } else {
            Node newNode = new Node(null, task, null);
            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                linkLast(task);
            }
            linkHistory.put(task.getId(), newNode);
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> copyHistory = new ArrayList<>(history);
        return copyHistory;
    }

    @Override
    public void remove(int id) {
        Node currentNode = linkHistory.get(id);
        removeNode(currentNode);
    }

    public void removeNode(Node currentNode) {
        if (currentNode == null) {
            return;
        }
        if (currentNode == head && currentNode != tail) {
            Node nextNode = currentNode.next;
            nextNode.prev = null;
            head = nextNode;
        } else if (currentNode == tail && currentNode != head) {
            Node prevNode = currentNode.prev;
            prevNode.next = null;
            tail = prevNode;
        } else if (currentNode == head && currentNode == tail) {
            head = null;
            tail = null;
        } else {
            Node nextNode = currentNode.next;
            Node prevNode = currentNode.prev;
            prevNode.next = currentNode.next;
            nextNode.prev = currentNode.prev;
        }
    }
}
