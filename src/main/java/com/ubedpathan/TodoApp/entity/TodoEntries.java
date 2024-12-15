package com.ubedpathan.TodoApp.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ubedpathan.TodoApp.util.ObjectIdDeserializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "Todo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoEntries {

    @Id
    @JsonSerialize(using = ToStringSerializer.class) // Converts ObjectId to a String during serialization
    @JsonDeserialize(using = ObjectIdDeserializer.class) // Converts String to ObjectId during deserialization
    private ObjectId id;

    @NonNull
    private String title;

    @NonNull
    private String content;

    private boolean completed = false;

    private LocalDateTime date;
}
