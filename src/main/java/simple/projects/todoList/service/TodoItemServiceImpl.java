package simple.projects.todoList.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.projects.todoList.dao.TodoItemRepository;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;

import java.util.List;
import java.util.Optional;

@Service
public class TodoItemServiceImpl implements TodoItemService{

    private TodoItemRepository todoItemRepository;

    @Autowired
    public TodoItemServiceImpl(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @Override
    public TodoItem findById(int id) {
        Optional<TodoItem> foundItem = todoItemRepository.findById(id);
        return foundItem.orElse(null);
    }

    @Override
    public List<TodoItem> findAll() {
        return todoItemRepository.findAll();
    }

    @Override
    @Transactional
    public TodoItem save(TodoItem todoItem) {
        todoItemRepository.save(todoItem);
        return todoItem;
    }

    @Override
    @Transactional
    public TodoItem updatePriority(int id, Priority priority) {
       TodoItem foundTodoItem = this.findById(id);
       if(foundTodoItem == null) return null;
       foundTodoItem.setPriority(priority);
       return foundTodoItem;
    }

    @Override
    @Transactional
    public TodoItem updateContent(int id, String content) {
        TodoItem foundTodoItem = this.findById(id);
        if(foundTodoItem == null) return null;
        foundTodoItem.setContent(content);
        return foundTodoItem;
    }

    @Override
    @Transactional
    public TodoItem update(int id, TodoItem todoItem) {
        TodoItem foundTodoItem = this.findById(id);
        if(foundTodoItem == null) return null;
        foundTodoItem.setContent(todoItem.getContent());
        foundTodoItem.setPriority(todoItem.getPriority());
        return foundTodoItem;
    }


    @Override
    @Transactional
    public TodoItem deleteById(int id) {
       TodoItem foundTodoItem = this.findById(id);
       if(foundTodoItem == null) return null;
       todoItemRepository.deleteById(id);
       return foundTodoItem;
    }
}
