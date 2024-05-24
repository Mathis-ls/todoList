package simple.projects.todoList.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;
import simple.projects.todoList.service.TodoItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todoitems")
public class TodoItemController {

    private TodoItemService todoItemService;

    @Autowired
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    @GetMapping("/{id}")
    public TodoItem getTodoItem(@PathVariable int id){
        return todoItemService.findById(id);
    }

    @GetMapping()
    public List<TodoItem> getTodoItems(){
        return todoItemService.findAll();
    }

    @PostMapping
    public int saveTodoItem(@RequestBody TodoItem todoItem){
        return todoItemService.save(todoItem);
    }

    @PutMapping("/{id}")
    public TodoItem updateTodoItem(@PathVariable int id, @RequestBody TodoItem updateTodoItem){
        TodoItem foundItem = todoItemService.findById(id);
        if(foundItem == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TodoItem not found");

        foundItem.setPriority(updateTodoItem.getPriority());
        foundItem.setContent(updateTodoItem.getContent());

        todoItemService.save(foundItem);
        return foundItem;
    }

    @PatchMapping("/{id}")
    public TodoItem updateTodoItemFields(@PathVariable int id, @RequestBody Map<String, Object> updates){
        TodoItem todoItem = todoItemService.findById(id);
        if (todoItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TodoItem not found");
        }

        if (updates.containsKey("content")) {
            String newContent = (String) updates.get("content");
            todoItem.setContent(newContent);
        }

        if (updates.containsKey("priority")) {
            String newPriority = (String) updates.get("priority");
            todoItem.setPriority(Priority.valueOf(newPriority.toUpperCase()));
        }

        todoItemService.save(todoItem);
        return todoItem;
    }

    @DeleteMapping("/{id}")
    public TodoItem deleteTodoItem(@PathVariable int id){
        return todoItemService.deleteById(id);
    }
}
