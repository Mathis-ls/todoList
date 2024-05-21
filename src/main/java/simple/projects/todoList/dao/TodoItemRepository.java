package simple.projects.todoList.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import simple.projects.todoList.entity.TodoItem;

public interface TodoItemRepository extends JpaRepository<TodoItem,Integer> {
}
