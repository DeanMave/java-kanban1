package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> linkHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    public void linkLast(Node<Task> node) {
        final Node<Task> oldTail = tail;
        tail = node;
        if (oldTail == null) {
            head = node;
        } else {
            oldTail.next = node;
            node.prev = oldTail;
        }
    }

    // Получение списка задач из двусвязного списка
    public List<Task> getTasks() {
        Node<Task> currentNode = head;
        List<Task> history = new ArrayList<>();
        while (currentNode != null) {
            history.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return history;
    }

    @Override
    public void addTask(Task task) {
        final int id = task.getId();
        if (linkHistory.containsKey(id)) {
            remove(id);
        }
        Node<Task> newNode = new Node<>(null, task, null);
        linkLast(newNode);
        linkHistory.put(id, newNode);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> currentNode = linkHistory.remove(id);
        if (currentNode != null) {
            removeNode(currentNode);
        }
    }

    public void removeNode(Node<Task> currentNode) {
        if (currentNode == null) {
            return;
        }
        if (currentNode == head && currentNode == tail) {
            head = null;
            tail = null;
        } else if (currentNode == head) {
            head = currentNode.next;
            if (head != null) {
                head.prev = null;
            }
        } else if (currentNode == tail) {
            tail = currentNode.prev;
            if (tail != null) {
                tail.next = null;
            }
        } else {
            Node<Task> nextNode = currentNode.next;
            Node<Task> prevNode = currentNode.prev;
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
        }
    }
}