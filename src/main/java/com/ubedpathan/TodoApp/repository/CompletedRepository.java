package com.ubedpathan.TodoApp.repository;

import com.ubedpathan.TodoApp.entity.CompletedEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompletedRepository extends MongoRepository<CompletedEntity, ObjectId> {
}
