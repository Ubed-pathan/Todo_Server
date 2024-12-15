package com.ubedpathan.TodoApp.service;

import com.ubedpathan.TodoApp.entity.CompletedEntity;
import com.ubedpathan.TodoApp.entity.TodoEntries;
import com.ubedpathan.TodoApp.entity.UserEntity;
import com.ubedpathan.TodoApp.repository.CompletedRepository;
import com.ubedpathan.TodoApp.repository.TodoRepository;
import com.ubedpathan.TodoApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompletedService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CompletedRepository completedRepository;

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public boolean completeTodo(ObjectId id, String userName) {
        Optional<TodoEntries> todoOptional = todoRepository.findById(id);
        if (todoOptional.isEmpty()) {
            return false;
        }

        // Create a new CompletedEntity and set the reference to the TodoEntries
        try{
            TodoEntries todo = todoOptional.get();
            todo.setCompleted(true);
            CompletedEntity completedEntity = new CompletedEntity();
            completedEntity.setTodoEntry(todo);  // Store the reference to the TodoEntries
            completedEntity.setCompletedDate(LocalDateTime.now());
            CompletedEntity completedTodo = completedRepository.save(completedEntity);
            userService.saveUserCompleteTodoEntry(id, userName, completedTodo);
            todoRepository.save(todo);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public List<CompletedEntity> getAllCompletedTodos(String userName) {
        List<CompletedEntity> completedTodo = userService.userCompletedTodo(userName);
        return completedTodo;
    }

    public boolean deleteCompletedTodoAndTodo(ObjectId completedId,ObjectId todoId, String userName) {
                boolean deleted = userService.deleteCompletedTodo(completedId, userName);
                if(deleted){
                    todoRepository.deleteById(todoId);
                    completedRepository.deleteById(completedId);
                    return true;
                }
                else{
                    return false;
                }
            }


}
