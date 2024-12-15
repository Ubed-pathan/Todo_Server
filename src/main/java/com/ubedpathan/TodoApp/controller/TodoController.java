package com.ubedpathan.TodoApp.controller;


import com.ubedpathan.TodoApp.entity.TodoEntries;
import com.ubedpathan.TodoApp.service.TodoService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @GetMapping
    private ResponseEntity<?> getUserTodos(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        List<TodoEntries> userTodos = todoService.getUserTodos(userName);
        if (!userTodos.isEmpty()) {
            return new ResponseEntity<>(userTodos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No todos found for the user", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    private ResponseEntity<String> addTodo(@RequestBody TodoEntries todoEntries){
        if((todoEntries.getTitle() != null && todoEntries.getTitle() != "") && (todoEntries.getContent() != null && todoEntries.getContent() != "")){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            String response = todoService.addTodos(todoEntries, userName);
            if(response != null){
                return new ResponseEntity<>(response, HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
            }
        }
        else{
            return new ResponseEntity<>("Please Fill all information", HttpStatus.OK);
        }
    }

    @PutMapping
    private ResponseEntity<?> updateTodo(@RequestBody Map<String, Object> editTodo){
        List<String> requiredKeys = List.of("title", "content", "id");
        boolean isValid = requiredKeys.stream()
                .allMatch(key -> editTodo.containsKey(key) && editTodo.get(key) != null && editTodo.get(key) != "");

        if (!isValid) {
            return new ResponseEntity<>("Please Fill all the field !",HttpStatus.BAD_REQUEST);
        }

        boolean data = todoService.updateTodo(editTodo);
        if(data != false){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Todo not found !", HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping
    private ResponseEntity<?> deleteTodo(@RequestBody Map<String, ObjectId> id) {
        if(id != null){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            boolean data = todoService.deleteTodoById(id.get("id"), userName);
            if(data) {
                return new ResponseEntity<>(HttpStatus.OK);

            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }
        else{
            return new ResponseEntity<>("please enter todo title !", HttpStatus.NOT_FOUND);
        }

    }
    





}
