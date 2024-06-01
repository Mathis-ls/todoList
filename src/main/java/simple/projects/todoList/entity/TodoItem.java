package simple.projects.todoList.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="todo_item")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    int id;

    @Column(name="content")
    String content;

    @Column(name="priority")
    Priority priority;

    public TodoItem() {
    }

    public TodoItem(String content, Priority priority) {
        this.content = content;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return id == todoItem.id && Objects.equals(content, todoItem.content) && priority == todoItem.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, priority);
    }
}
