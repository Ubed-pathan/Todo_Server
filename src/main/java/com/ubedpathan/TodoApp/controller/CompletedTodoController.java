package com.ubedpathan.TodoApp.controller;

import com.ubedpathan.TodoApp.entity.CompletedEntity;
import com.ubedpathan.TodoApp.service.CompletedService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/completed")
public class CompletedTodoController {

    @Autowired
    private CompletedService completedService;

    @PutMapping("/{id}")
    public ResponseEntity<String> completeTodo(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean isCompleted = completedService.completeTodo(id, userName);

        if (isCompleted) {
            return new ResponseEntity<>("Todo marked as completed successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Todo not found!", HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping
//    public ResponseEntity<?> getCompletedTodos() {
//        return new ResponseEntity<>(completedService.getAllCompletedTodos(), HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<List<CompletedEntity>> getAllCompletedTodos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        List<CompletedEntity> completedTodos = completedService.getAllCompletedTodos(userName);
        return ResponseEntity.ok(completedTodos);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompletedTodo(@RequestBody Map<String, ObjectId> todoIds){
        if((todoIds.containsKey("completedId") && (todoIds.get("completedId") != null)) && (todoIds.containsKey("todoId") && (todoIds.get("todoId") != null))) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            ObjectId completedId = todoIds.get("completedId");
            ObjectId todoId = todoIds.get("todoId");
            boolean check = completedService.deleteCompletedTodoAndTodo(completedId,todoId,userName);
            if (check)
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
