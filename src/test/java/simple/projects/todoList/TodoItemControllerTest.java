package simple.projects.todoList;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import simple.projects.todoList.dao.TodoItemRepository;
import simple.projects.todoList.entity.Priority;
import simple.projects.todoList.entity.TodoItem;
import simple.projects.todoList.service.TodoItemService;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoItemControllerTest {

    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.4.0")
            .withInitScript("schema.sql");

    @LocalServerPort
    private int port;

    @Autowired
    private TodoItemService todoItemService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeAll
    static void beforeAll(){
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll(){
        mySQLContainer.stop();
    }
    @Autowired
    TodoItemRepository todoItemRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        todoItemRepository.deleteAll();
    }

    @Test
    void testGetTodoItem() {
        TodoItem todoItem = new TodoItem("Learn TestContainers", Priority.HIGH);
        int id = todoItemService.save(todoItem);

        given()
                .when()
                .get("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo("Learn TestContainers"))
                .body("priority", equalTo(Priority.HIGH.toString()));
    }

    @Test
    void testGetTodoItems() {
        TodoItem todoItem1 = new TodoItem("Learn TestContainers", Priority.HIGH);
        TodoItem todoItem2 = new TodoItem("Implement Tests", Priority.MEDIUM);
        int id1 = todoItemService.save(todoItem1);
        int id2 = todoItemService.save(todoItem2);

        given()
                .when()
                .get("/todoitems")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("find { it.id == " + id1 + " }.content", equalTo("Learn TestContainers"))
                .body("find { it.id == " + id1 + " }.priority", equalTo("HIGH"))
                .body("find { it.id == " + id2 + " }.content", equalTo("Implement Tests"))
                .body("find { it.id == " + id2 + " }.priority", equalTo("MEDIUM"));
    }

    @Test
    void testSaveTodoItem() {
        TodoItem todoItem = new TodoItem("Learn TestContainers", Priority.HIGH);

        // Send POST request to save the todoItem
        Integer savedItemId = given()
                .contentType(ContentType.JSON)
                .body(todoItem)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(Integer.class);

        assertNotNull(savedItemId);

        TodoItem savedTodoItem = todoItemService.findById(savedItemId);
        assertNotNull(savedTodoItem);
        assertEquals(todoItem.getContent(), savedTodoItem.getContent());
        assertEquals(todoItem.getPriority(), savedTodoItem.getPriority());
    }

    @Test
    void testUpdateTodoItem() {
        TodoItem todoItem = new TodoItem("Original Content", Priority.MEDIUM);
        int id = todoItemService.save(todoItem);

        TodoItem updatedTodoItem = new TodoItem("Updated Content", Priority.HIGH);

        given()
                .contentType(ContentType.JSON)
                .body(updatedTodoItem)
                .when()
                .put("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo("Updated Content"))
                .body("priority", equalTo(Priority.HIGH.toString()));
    }

    @Test
    void testUpdateTodoItemFields() {
        TodoItem todoItem = new TodoItem("Original Content", Priority.MEDIUM);
        int id = todoItemService.save(todoItem);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("content", "Partially Updated Content"))
                .when()
                .patch("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo("Partially Updated Content"))
                .body("priority", equalTo(Priority.MEDIUM.toString()));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("priority", "LOW"))
                .when()
                .patch("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo("Partially Updated Content"))
                .body("priority", equalTo(Priority.LOW.toString()));
    }

    @Test
    void testDeleteTodoItem() {
        TodoItem todoItem = new TodoItem("Learn TestContainers", Priority.HIGH);
        int id = todoItemService.save(todoItem);

        given()
                .when()
                .delete("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo("Learn TestContainers"))
                .body("priority", equalTo(Priority.HIGH.toString()));

        given()
                .when()
                .get("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
