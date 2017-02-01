package com.vogella.jpa.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface TodoService {

	void getTodos(Consumer<List<Todo>> todosConsumer);

	boolean saveTodo(Todo newTodo);
	
	Optional<Todo> getTodo(int id);
	
	boolean deleteTodo(int id);
	
	void printTodos();
}
