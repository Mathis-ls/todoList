package simple.projects.todoList.exceptions.responses;

import org.springframework.http.HttpStatus;

public class TodoItemErrorResponse {
    private HttpStatus httpStatus;
    private String message;
    private String timeStamp;

    public TodoItemErrorResponse(HttpStatus httpStatus, String message, String timeStamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
