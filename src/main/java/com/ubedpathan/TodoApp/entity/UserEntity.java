package com.ubedpathan.TodoApp.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private ObjectId id;

    private String username;

    private String email;

    private String password;

    private List<String> roles;

    private LocalDateTime date;

    @DBRef
    private List<TodoEntries> todoEntriesList = new ArrayList<>();

    @DBRef
    private List<CompletedEntity> completedEntitiesList = new ArrayList<>();


}
