package com.ubedpathan.TodoApp.repository;

import com.ubedpathan.TodoApp.entity.TodoEntries;
import com.ubedpathan.TodoApp.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
     UserEntity findByUsername(String username);
}
