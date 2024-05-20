package simple.projects.todoList.entity;

import jakarta.persistence.*;

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
}
