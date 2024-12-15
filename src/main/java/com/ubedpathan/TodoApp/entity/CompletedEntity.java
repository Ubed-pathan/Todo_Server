package com.ubedpathan.TodoApp.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ubedpathan.TodoApp.util.ObjectIdDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "CompletedTodo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedEntity {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private ObjectId id;

    @DBRef  // This makes a reference to the TodoEntries document
    private TodoEntries todoEntry;  // Reference to the TodoEntries document

    private LocalDateTime completedDate;
}
