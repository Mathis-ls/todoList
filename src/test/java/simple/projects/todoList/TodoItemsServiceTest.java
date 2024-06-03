package simple.projects.todoList;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;
import simple.projects.todoList.dao.TodoItemRepository;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;
import simple.projects.todoList.service.TodoItemService;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TodoItemsServiceTest {
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.4.0")
            .withInitScript("schema.sql");

    @BeforeAll
    static void beforeAll(){
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll(){
        mySQLContainer.stop();
    }

    @Autowired
    private TodoItemRepository todoItemRepository;

    @BeforeEach
    void beforeEach(){
        todoItemRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username",mySQLContainer::getUsername);
        registry.add("spring.datasource.password",mySQLContainer::getPassword);
    }

    @Autowired
    private TodoItemService todoItemService;

    @Test
    void addTodoItemTest(){
        String content = "Learn TestContainers";
        Priority priority = Priority.HIGH;
        TodoItem todoItem = new TodoItem(content,priority);
        int id = todoItemService.save(todoItem);

        assertEquals(1, todoItemService.findAll().size());

        TodoItem foundTodoItem = todoItemService.findById(id);
        if(foundTodoItem == null) fail();
        assertEquals(id,foundTodoItem.getId());
        assertEquals(content,foundTodoItem.getContent());
        assertEquals(priority,foundTodoItem.getPriority());
    }

    @Test
    void updateTodoItemTest() {
        TodoItem todoItem = new TodoItem("Original Content", Priority.MEDIUM);
        int id = todoItemService.save(todoItem);

        // Modify the priority of the saved item
        Priority updatePriority = Priority.HIGH;
        TodoItem updatedTodoItem = todoItemService.updatePriority(id, updatePriority);
        TodoItem foundUpdatedTodoItem = todoItemService.findById(id);
        assertNotNull(foundUpdatedTodoItem);
        assertEquals(updatedTodoItem,foundUpdatedTodoItem);

        // Modify the content of the saved item
        String updateContent = "Updated Content";
        updatedTodoItem = todoItemService.updateContent(id, updateContent);
        foundUpdatedTodoItem = todoItemService.findById(id);
        assertNotNull(foundUpdatedTodoItem);
        assertEquals(updatedTodoItem,foundUpdatedTodoItem);

        // Modify both the priority and content of the saved item
        TodoItem updateItem = new TodoItem("Final Content", Priority.LOW);
        updatedTodoItem = todoItemService.update(id,updateItem);
        foundUpdatedTodoItem = todoItemService.findById(id);
        assertNotNull(foundUpdatedTodoItem);
        assertEquals(updatedTodoItem,foundUpdatedTodoItem);
    }


    @Test
    void deleteByIdTest() {
        TodoItem todoItem = new TodoItem("Learn TestContainers", Priority.HIGH);
        int id = todoItemService.save(todoItem);

        TodoItem deletedTodoItem = todoItemService.deleteById(id);
        //assert values of deleted Item:
        assertNotNull(deletedTodoItem);
        assertEquals(id, deletedTodoItem.getId());
        assertEquals(todoItem.getContent(), deletedTodoItem.getContent());
        assertEquals(todoItem.getPriority(), deletedTodoItem.getPriority());

        //assert deleted Item no longer exists in DB
        TodoItem notFoundTodoItem = todoItemService.findById(id);
        assertNull(notFoundTodoItem);
    }

    @Test
    void findAllTest() {
        TodoItem todoItem1 = new TodoItem("Learn TestContainers", Priority.HIGH);
        TodoItem todoItem2 = new TodoItem("Implement Tests", Priority.MEDIUM);
        int id1 = todoItemService.save(todoItem1);
        int id2 = todoItemService.save(todoItem2);

        List<TodoItem> todoItems = todoItemService.findAll();

        //Assert amount of found elements
        assertNotNull(todoItems);
        assertEquals(2, todoItems.size());

        //assert the values of the found elements
        TodoItem foundTodoItem1 = todoItems.stream().filter(item -> item.getId() == id1).findFirst().orElse(null);
        TodoItem foundTodoItem2 = todoItems.stream().filter(item -> item.getId() == id2).findFirst().orElse(null);

        assertNotNull(foundTodoItem1);
        assertEquals(todoItem1.getContent(), foundTodoItem1.getContent());
        assertEquals(todoItem1.getPriority(), foundTodoItem1.getPriority());

        assertNotNull(foundTodoItem2);
        assertEquals(todoItem2.getContent(), foundTodoItem2.getContent());
        assertEquals(todoItem2.getPriority(), foundTodoItem2.getPriority());
    }

}

