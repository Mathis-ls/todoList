package simple.projects.todoList.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
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
        //remove possible other values like id
        todoItem = new TodoItem(todoItem.getContent(),todoItem.getPriority());

        if (todoItem.getContent() == null || todoItem.getPriority() == null) {
            throw new IllegalArgumentException("Content and priority must not be null.");
        }
        return todoItemService.save(todoItem);
    }

    @PutMapping("/{id}")
    public TodoItem updateTodoItem(@PathVariable int id, @RequestBody TodoItem update){
        TodoItem updatedTodoItem = todoItemService.update(id,update);
        if(updatedTodoItem == null) throw new ItemNotFoundException("Todo item with id: " + id + " could not be found.");

        return updatedTodoItem;
    }

    @PatchMapping("/{id}")
    public TodoItem updateTodoItemFields(@PathVariable int id, @RequestBody Map<String, Object> updates){
        TodoItem updatedItem = todoItemService.findById(id);
        if(null == updatedItem) throw new ItemNotFoundException("Todo item with id: " + id + " could not be found.");

        if(!updates.containsKey("content") && !updates.containsKey("priority")) throw new IllegalArgumentException("Todo item with id: " + id + " could not be patched because no valid key values could be detected in: " + updates);

        if (updates.containsKey("content")) {
            String newContent = (String) updates.get("content");
            updatedItem = todoItemService.updateContent(id,newContent);
        }

        if (updates.containsKey("priority")) {
            Priority newPriority = Priority.valueOf(((String)updates.get("priority")).toUpperCase());
            updatedItem = todoItemService.updatePriority(id,newPriority);
        }

        return updatedItem;
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

    @ExceptionHandler
    public ResponseEntity<TodoItemErrorResponse> handleWrongParameter(IllegalArgumentException exc) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
        TodoItemErrorResponse errorResponse = new TodoItemErrorResponse(HttpStatus.BAD_REQUEST, exc.getMessage(), timeStamp);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<TodoItemErrorResponse> handleWrongParameter(HttpMessageNotReadableException exc){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
        String message = "Parameter does not match expected type.";
        TodoItemErrorResponse errorResponse = new TodoItemErrorResponse(HttpStatus.BAD_REQUEST,message,timeStamp);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<TodoItemErrorResponse> handleWrongParameter(ClassCastException exc){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date());
        String message = "Parameter does not match expected type.";
        TodoItemErrorResponse errorResponse = new TodoItemErrorResponse(HttpStatus.BAD_REQUEST,message,timeStamp);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}
