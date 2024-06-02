-- Create the todo_item table
CREATE TABLE IF NOT EXISTS todo_item (
    id INT NOT NULL,
    priority TINYINT CHECK (priority BETWEEN 0 AND 3),
    content VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Create the todo_item_seq table
CREATE TABLE IF NOT EXISTS todo_item_seq (
    next_val BIGINT
) ENGINE=InnoDB;

-- Insert initial value into todo_item_seq
INSERT INTO todo_item_seq VALUES (1);
