package com.vogella.jpa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.vogella.jpa.model.Todo;
import com.vogella.jpa.model.TodoService;

@Component(service = TodoService.class, property = { "osgi.command.scope=test", "osgi.command.function=printTodos" })
public class TodoServiceImpl implements TodoService {

	private static AtomicInteger current = new AtomicInteger(1);
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	@Activate
	@SuppressWarnings("unchecked")
	protected void activateComponent() {
		@SuppressWarnings("rawtypes")
		Map map = new HashMap();
		map.put(PersistenceUnitProperties.CLASSLOADER, getClass().getClassLoader());

		PersistenceProvider persistenceProvider = new PersistenceProvider();
		entityManagerFactory = persistenceProvider.createEntityManagerFactory("h2-eclipselink", map);
		entityManager = entityManagerFactory.createEntityManager();
		
		getTodos(todos -> {
			if(todos.isEmpty()) {
				List<Todo> initialModel = createInitialModel();
				initialModel.forEach(this::saveTodo);
			}
		});
	}

	@Deactivate
	protected void deactivateComponent() {
		entityManager.close();
		entityManagerFactory.close();
		entityManager = null;
		entityManagerFactory = null;
	}

	public TodoServiceImpl() {
	}

	@Override
	public void getTodos(Consumer<List<Todo>> todosConsumer) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Todo> cq = cb.createQuery( Todo.class );
        Root<Todo> rootTodo = cq.from( Todo.class );
        CriteriaQuery<Todo> allTodos = cq.select( rootTodo );
        TypedQuery<Todo> todosQuery = entityManager.createQuery( allTodos );

        List<Todo> todoList = todosQuery.getResultList();
        todosConsumer.accept(todoList);
	}

	// create or update an existing instance of Todo
	@Override
	public synchronized boolean saveTodo(Todo newTodo) {
		// hold the Optional object as reference to determine, if the Todo is
		// newly created or not
		Optional<Todo> todoOptional = getTodo(newTodo.getId());

		// get the actual todo or create a new one
		Todo todo = todoOptional.orElse(new Todo(current.getAndIncrement()));
		todo.setSummary(newTodo.getSummary());
		todo.setDescription(newTodo.getDescription());

		// send out events
		if (todoOptional.isPresent()) {
			entityManager.getTransaction().begin();
			entityManager.merge(todo);
			entityManager.getTransaction().commit();
		} else {
			entityManager.getTransaction().begin();
			entityManager.persist(todo);
			entityManager.getTransaction().commit();
		}
		return true;
	}

	@Override
	public Optional<Todo> getTodo(int id) {
		entityManager.getTransaction().begin();
		Todo find = entityManager.find(Todo.class, id);
		entityManager.getTransaction().commit();

		return Optional.ofNullable(find);
	}

	@Override
	public boolean deleteTodo(int id) {
		entityManager.getTransaction().begin();
		Todo find = entityManager.find(Todo.class, id);
		entityManager.remove(find);
		entityManager.getTransaction().commit();

		return true;
	}

	@Override
	public void printTodos() {
		getTodos(todoList -> todoList.forEach(System.out::println));
	}
	
	private List<Todo> createInitialModel() {
		List<Todo> list = new ArrayList<>();
		list.add(createTodo("Application model", "Flexible and extensible"));
		list.add(createTodo("DI", "@Inject as programming mode"));
		list.add(createTodo("OSGi", "Services"));
		list.add(createTodo("SWT", "Widgets"));
		list.add(createTodo("JFace", "Especially Viewers!"));
		list.add(createTodo("CSS Styling", "Style your application"));
		list.add(createTodo("Eclipse services", "Selection, model, Part"));
		list.add(createTodo("Renderer", "Different UI toolkit"));
		list.add(createTodo("Compatibility Layer", "Run Eclipse 3.x"));
		return list;
	}
	
	private Todo createTodo(String summary, String description) {
		return new Todo(current.getAndIncrement(), summary, description);
	}

}
