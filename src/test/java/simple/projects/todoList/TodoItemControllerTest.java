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
import static org.junit.jupiter.api.Assertions.*;

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
        String content = "Learn TestContainers";
        Priority priority = Priority.HIGH;
        TodoItem todoItem = new TodoItem(content, priority);
        int id = todoItemService.save(todoItem).getId();

        given()
                .when()
                .get("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo(content))
                .body("priority", equalTo(priority.toString()));
    }

    @Test
    void testGetTodoItems() {
        String content1 = "Learn TestContainers";
        Priority priority1 = Priority.HIGH;
        TodoItem todoItem1 = new TodoItem(content1, priority1);

        String content2 = "Implement Tests";
        Priority priority2 = Priority.MEDIUM;
        TodoItem todoItem2 = new TodoItem(content2, priority2);

        int id1 = todoItemService.save(todoItem1).getId();
        int id2 = todoItemService.save(todoItem2).getId();

        given()
                .when()
                .get("/todoitems")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("find { it.id == " + id1 + " }.content", equalTo(content1))
                .body("find { it.id == " + id1 + " }.priority", equalTo(priority1.toString()))
                .body("find { it.id == " + id2 + " }.content", equalTo(content2))
                .body("find { it.id == " + id2 + " }.priority", equalTo(priority2.toString()));
    }

    @Test
    void testSaveTodoItem() {
        String content = "Learn TestContainers";
        Priority priority = Priority.HIGH;
        TodoItem todoItem = new TodoItem(content, priority);

        // Send POST request to save the todoItem
        TodoItem returnedItem = given()
                .contentType(ContentType.JSON)
                .body(todoItem)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(TodoItem.class);

        //assert that the values of the returned Item match the ones sent to the endpoint
        assertNotNull(returnedItem.getId());
        assertEquals(content,returnedItem.getContent());
        assertEquals(priority,returnedItem.getPriority());

        //assert that the item was saved in the db
        TodoItem savedTodoItem = todoItemService.findById(returnedItem.getId());
        assertNotNull(savedTodoItem);
        assertEquals(content, savedTodoItem.getContent());
        assertEquals(priority, savedTodoItem.getPriority());
    }

    @Test
    void testUpdateTodoItem() {
        TodoItem todoItem = new TodoItem("Original Content", Priority.MEDIUM);
        int id = todoItemService.save(todoItem).getId();

        String updatedContent = "Updated Content";
        Priority updatedPriority = Priority.HIGH;
        TodoItem updatedTodoItem = new TodoItem(updatedContent, updatedPriority);

        given()
                .contentType(ContentType.JSON)
                .body(updatedTodoItem)
                .when()
                .put("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo(updatedContent))
                .body("priority", equalTo(updatedPriority.toString()));
    }

    @Test
    void testUpdateTodoItemFields() {
        Priority originalPriority = Priority.MEDIUM;
        TodoItem todoItem = new TodoItem("Original Content", originalPriority);
        int id = todoItemService.save(todoItem).getId();

        String updatedContent = "Updated Content";

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("content", updatedContent))
                .when()
                .patch("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo(updatedContent))
                .body("priority", equalTo(originalPriority.toString()));

        Priority updatedPriority = Priority.LOW;

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("priority", updatedPriority))
                .when()
                .patch("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo(updatedContent))
                .body("priority", equalTo(updatedPriority.toString()));
    }

    @Test
    void testDeleteTodoItem() {
        String content = "Learn TestContainers";
        Priority priority = Priority.HIGH;
        TodoItem todoItem = new TodoItem(content, priority);
        int id = todoItemService.save(todoItem).getId();

        given()
                .when()
                .delete("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(id))
                .body("content", equalTo(content))
                .body("priority", equalTo(priority.toString()));

        given()
                .when()
                .get("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    //Error Tests:

    @Test
    void testGetTodoItemNotFound() {
        int invalidId = 0;

        given()
                .when()
                .get("/todoitems/{id}", invalidId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Todo item with id: " + invalidId + " could not be found."));
    }


    @Test
    void testPostTodoItemMissingPriority() {
        TodoItem todoItem = new TodoItem();
        todoItem.setContent("Content without priority");

        given()
                .contentType(ContentType.JSON)
                .body(todoItem)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Content and priority must not be null."));
    }

    @Test
    void testPostTodoItemMissingContent() {
        TodoItem todoItem = new TodoItem();
        todoItem.setPriority(Priority.HIGH);

        given()
                .contentType(ContentType.JSON)
                .body(todoItem)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Content and priority must not be null."));
    }

    @Test
    void testPostTodoItemMissingContentAndPriority() {
        TodoItem todoItem = new TodoItem();

        given()
                .contentType(ContentType.JSON)
                .body(todoItem)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Content and priority must not be null."));
    }

    @Test
    void testPostTodoItemWithId() {
        String content = "Learn TestContainers";
        Priority priority = Priority.HIGH;
        TodoItem todoItem = new TodoItem(content,priority);
        //Save item in DB so a id will be set
        int sentId = todoItemService.save(todoItem).getId();

        Integer savedItemId = given()
                .contentType(ContentType.JSON)
                .body(todoItem)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(TodoItem.class)
                .getId();
        //Id that was used in the json and the id that was given to the todoItem as it was saved
        //should be different
        assertNotEquals(savedItemId,sentId);

        //both todoItems exist int the db under different ids
        given()
                .when()
                .get("/todoitems/{id}", savedItemId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(savedItemId))
                .body("content", equalTo(content))
                .body("priority", equalTo(priority.toString()));

        given()
                .when()
                .get("/todoitems/{id}", sentId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(sentId))
                .body("content", equalTo(content))
                .body("priority", equalTo(priority.toString()));
    }

    @Test
    void testPostTodoItemWithExtraData() {
        String content = "Learn TestContainers";
        Priority priority = Priority.HIGH;

        // Create a map representing the JSON with extra random data
        Map<String, Object> todoItemWithExtraData = Map.of(
                "content", content,
                "priority", priority.toString(),
                "extraField1", "extraValue1",
                "extraField2", 12345,
                "extraField3", true
        );

        // Send POST request to save the todoItem
        Integer savedItemId = given()
                .contentType(ContentType.JSON)
                .body(todoItemWithExtraData)
                .when()
                .post("/todoitems")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response()
                .as(TodoItem.class)
                .getId();

        assertNotNull(savedItemId);

        TodoItem savedTodoItem = todoItemService.findById(savedItemId);
        assertNotNull(savedTodoItem);
        assertEquals(content, savedTodoItem.getContent());
        assertEquals(priority, savedTodoItem.getPriority());
    }

    @Test
    void testPutTodoItemNotFound() {
        int invalidId = 0;
        TodoItem updatedTodoItem = new TodoItem("Updated Content", Priority.HIGH);

        given()
                .contentType(ContentType.JSON)
                .body(updatedTodoItem)
                .when()
                .put("/todoitems/{id}", invalidId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Todo item with id: " + invalidId + " could not be found."));
    }

    @Test
    void testPatchTodoItemNotFound() {
        int invalidId = 0;
        Map<String, Object> updates = Map.of("content", "Updated Content");

        given()
                .contentType(ContentType.JSON)
                .body(updates)
                .when()
                .patch("/todoitems/{id}", invalidId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Todo item with id: " + invalidId + " could not be found."));
    }

    @Test
    void testDeleteTodoItemNotFound() {
        int invalidId = 0;

        given()
                .when()
                .delete("/todoitems/{id}", invalidId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Todo item with id: " + invalidId + " could not be found."));
    }
    @Test
    void testPatchTodoItemWithInvalidKey() {
        TodoItem todoItem = new TodoItem("Original Content", Priority.MEDIUM);
        int id = todoItemService.save(todoItem).getId();

        Map<String, Object> updates = Map.of("invalidKey", "value");

        given()
                .contentType(ContentType.JSON)
                .body(updates)
                .when()
                .patch("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Todo item with id: " + id + " could not be patched because no valid key values could be detected in: " + updates));
    }

    @Test
    void testPatchTodoItemWithWrongValueType() {
        TodoItem todoItem = new TodoItem("Original Content", Priority.MEDIUM);
        int id = todoItemService.save(todoItem).getId();

        Map<String, Object> updates = Map.of("priority", false); // Invalid type, should be a string or integer

        given()
                .contentType(ContentType.JSON)
                .body(updates)
                .when()
                .patch("/todoitems/{id}", id)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Parameter does not match expected type."));
    }
}
