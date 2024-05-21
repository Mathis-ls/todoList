package simple.projects.todoList.service;

import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;

import java.util.List;

public interface TodoItemService {

    TodoItem findById(int id);

    List<TodoItem> findAll();
    int save(TodoItem todoItem);

    TodoItem changePriority(int id, Priority priority);

    TodoItem deleteById(int id);





}
