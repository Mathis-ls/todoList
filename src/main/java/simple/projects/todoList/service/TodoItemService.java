package simple.projects.todoList.service;

import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;

import java.util.List;

public interface TodoItemService {

    TodoItem findById(int id);

    List<TodoItem> findAll();
    TodoItem save(TodoItem todoItem);

    TodoItem updatePriority(int id, Priority priority);

    TodoItem updateContent(int id, String content);

    TodoItem update(int id, TodoItem todoItem);




    TodoItem deleteById(int id);





}
