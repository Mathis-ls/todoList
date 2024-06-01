package simple.projects.todoList;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;
import simple.projects.todoList.service.TodoItemService;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TodoItemsRestControllerTest {

    @LocalServerPort
    private int port;

    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.4.0");

    @BeforeAll
    static void beforeAll(){
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll(){
        mySQLContainer.stop();
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
        TodoItem todoItem = new TodoItem("Learn TestContainers",Priority.HIGH);
        todoItemService.save(todoItem);

        assertEquals(1, todoItemService.findAll().size());

        TodoItem foundTodoItem = todoItemService.findById(1);
        if(foundTodoItem == null) fail();
        assertEquals(1,foundTodoItem.getId());
        assertEquals("Learn TestContainers",foundTodoItem.getContent());
        assertEquals(Priority.HIGH,foundTodoItem.getPriority());
    }

}

