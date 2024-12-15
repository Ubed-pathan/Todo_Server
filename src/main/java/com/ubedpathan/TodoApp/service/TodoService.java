package com.ubedpathan.TodoApp.service;

import com.ubedpathan.TodoApp.entity.TodoEntries;
import com.ubedpathan.TodoApp.entity.UserEntity;
import com.ubedpathan.TodoApp.repository.TodoRepository;
import com.ubedpathan.TodoApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TodoService {
    List<TodoEntries> todos = new ArrayList<>();

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired UserService userService;


    public String addTodos(TodoEntries todoEntries, String userName) {

            try{
                UserEntity userData = userRepository.findByUsername(userName);
                todoEntries.setDate(LocalDateTime.now());
                TodoEntries saved = todoRepository.save(todoEntries);
                userData.getTodoEntriesList().add(saved);
                userService.saveUserTodoEntry(userData);
                return "Added successfully !";
            }catch (Exception e){
                log.error("Error",e);
                return null;
            }
    }

    public List<TodoEntries> getUserTodos(String userName) {
        List<TodoEntries> userTodos = userService.getUserTodos(userName);
        if(userTodos != null) {
            return userTodos;
        }else{
            return new ArrayList<>();
        }
    }


    public boolean updateTodo(Map<String, Object> editTodo) {
        ObjectId todoId = new ObjectId(String.valueOf(editTodo.get("id")));
        Optional<TodoEntries> optionalTodo = todoRepository.findById(todoId);
        if(optionalTodo.isEmpty()){
            return false;
        }
        else{
            TodoEntries todo = optionalTodo.get();
            todo.setTitle((String) editTodo.get("title"));
            todo.setContent((String) editTodo.get("content"));
            todoRepository.save(todo);
            return true;
        }

    }

    public boolean deleteTodoById(ObjectId id, String userName) {
        boolean removed = false;
        UserEntity userEntity = userService.getUserByUserName(userName);
        removed = userEntity.getTodoEntriesList().removeIf(x -> x.getId().equals(id));
        if(removed){
            userService.saveUserTodoEntry(userEntity);
            Optional<TodoEntries> findTodoById = todoRepository.findById(id);
            if(findTodoById.isEmpty()){
                return false;
            }
            else {
                todoRepository.deleteById(id);
                return true;
            }
        }
        else{
            return false;
        }

    }
}
