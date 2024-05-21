package simple.projects.todoList.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;
import simple.projects.todoList.service.TodoItemService;

import java.util.List;

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
    public TodoItem setPriority(@PathVariable int id, @RequestBody Priority priority){
        return todoItemService.changePriority(id,priority);
    }

    @DeleteMapping("/{id}")
    public TodoItem deleteTodoItem(@PathVariable int id){
        return todoItemService.deleteById(id);
    }
}
