package simple.projects.todoList.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;
import simple.projects.todoList.exceptions.ItemNotFoundException;
import simple.projects.todoList.exceptions.responses.TodoItemErrorResponse;
import simple.projects.todoList.service.TodoItemService;

import java.text.SimpleDateFormat;
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
        TodoItem foundItem = todoItemService.findById(id);
        if(foundItem == null) throw new ItemNotFoundException("Todo item with id: " + id + " could not be found.");
        return foundItem;
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
        if(foundItem == null) throw new ItemNotFoundException("Todo item with id: " + id + " could not be found.");

        foundItem.setPriority(updateTodoItem.getPriority());
        foundItem.setContent(updateTodoItem.getContent());

        todoItemService.save(foundItem);
        return foundItem;
    }

    @PatchMapping("/{id}")
    public TodoItem updateTodoItemFields(@PathVariable int id, @RequestBody Map<String, Object> updates){
        TodoItem foundItem = todoItemService.findById(id);
        if(foundItem == null) throw new ItemNotFoundException("Todo item with id: " + id + " could not be found.");

        if (updates.containsKey("content")) {
            String newContent = (String) updates.get("content");
            foundItem.setContent(newContent);
        }

        if (updates.containsKey("priority")) {
            String newPriority = (String) updates.get("priority");
            foundItem.setPriority(Priority.valueOf(newPriority.toUpperCase()));
        }

        todoItemService.save(foundItem);
        return foundItem;
    }

    @DeleteMapping("/{id}")
    public TodoItem deleteTodoItem(@PathVariable int id){
        TodoItem deletedItem = todoItemService.deleteById(id);
        if(deletedItem == null) throw new ItemNotFoundException("Todo item with id: " + id + " could not be found.");

        return deletedItem;
    }

    @ExceptionHandler
    public ResponseEntity<TodoItemErrorResponse> handleItemNotFound(ItemNotFoundException exc){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
        TodoItemErrorResponse errorResponse = new TodoItemErrorResponse(HttpStatus.NOT_FOUND,exc.getMessage(),timeStamp);
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
}
