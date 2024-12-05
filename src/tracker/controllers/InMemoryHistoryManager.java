package tracker.controllers;

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
        final Node<Task> newTail = node;
        if (oldTail == null) {
            head = newTail;
            tail = newTail;
        } else if (head == tail){
            tail = newTail;
        } else {
            oldTail.prev = newTail;
        }
    }

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
            return;
        } else {
            Node<Task> newNode = new Node(null, task, null);
            linkLast(newNode);
            linkHistory.put(task.getId(), newNode);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> currentNode = linkHistory.remove(id);
        removeNode(currentNode);
    }

    public void removeNode(Node<Task> currentNode) {
        if (currentNode == null) {
            return;
        }
        if (currentNode == head && currentNode != tail) {
            Node<Task> nextNode = currentNode.next;
            nextNode.prev = null;
            head = nextNode;
        } else if (currentNode == tail && currentNode != head) {
            Node<Task> prevNode = currentNode.prev;
            prevNode.next = null;
            tail = prevNode;
        } else if (currentNode == head && currentNode == tail) {
            head = null;
            tail = null;
        } else {
            Node<Task> nextNode = currentNode.next;
            Node<Task> prevNode = currentNode.prev;
            prevNode.next = currentNode.next;
            nextNode.prev = currentNode.prev;
        }
    }
}