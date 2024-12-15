package com.ubedpathan.TodoApp.repository;

import com.ubedpathan.TodoApp.entity.TodoEntries;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TodoRepository extends MongoRepository<TodoEntries, ObjectId>{
    List<TodoEntries> findByCompletedFalse();
}
