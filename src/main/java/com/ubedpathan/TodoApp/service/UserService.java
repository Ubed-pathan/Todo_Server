package com.ubedpathan.TodoApp.service;

import com.ubedpathan.TodoApp.DTOs.UserDTO;
import com.ubedpathan.TodoApp.entity.CompletedEntity;
import com.ubedpathan.TodoApp.entity.TodoEntries;
import com.ubedpathan.TodoApp.entity.UserEntity;
import com.ubedpathan.TodoApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public boolean handleUserSignup(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(encoder.encode(userDTO.getPassword()));
        userEntity.setRoles(Arrays.asList("USER"));
        userEntity.setDate(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(userEntity);
        if (savedUser != null && savedUser.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    public String handleUserSignin(UserEntity user){
        try{
            // remember that in spring config we created one bean of AuthenticationManager because by default user authentication handle by AuthenticationManager but it is interface so it's implemented class handle it by default but it work on default flow but for login authentication we also want AuthenticationManager object to call at login time
            // because of AuthenticationManager is interface we create a bean by using it's implemented class to take control in our hand
            // here we are stopping AuthenticationManager default flow and taking contol in our hand and calling it by it's object which is created by it's implemented class
            // here new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword() ) this code only for to represent user it not do any thing it-self it only represent user mean stored user data into UsernamePasswordAuthenticationToken object
            //because authenticationManager.authenticate want data in UsernamePasswordAuthenticationToken object representation
            //but authenticationManager is interface and it has only one method authenticate so this process handle by it's implementation class mostly AuthenticationProvider handle AuthenticationMnager implementation
            // and AuthenticationProvider deligate or send this process to provider manager so this below authentication process handled by provider manager and provider manager wants data in object representation of UsernamePasswordAuthenticationToken
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword() ));
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(user.getUsername());
            } else {
                return "fail";
            }
        }catch(Exception e){
            return "fail";
        }
    }

    public void saveUserTodoEntry(UserEntity user){
        userRepository.save(user);
    }

    public List<TodoEntries> getUserTodos(String userName) {
        try {
            UserEntity userData = userRepository.findByUsername(userName);
            if (userData != null){
                return userData.getTodoEntriesList();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error retrieving user todos", e);
            return null;
        }
    }

    public UserEntity getUserByUserName(String userName){
        return userRepository.findByUsername(userName);
    }

    public boolean saveUserCompleteTodoEntry(ObjectId id,String userName, CompletedEntity completedTodo) {
        UserEntity userData = userRepository.findByUsername(userName);
        if(userData != null){
            try{
                userData.getCompletedEntitiesList().add(completedTodo);
                userData.getTodoEntriesList().removeIf(x -> x.getId().equals(id));
                userRepository.save(userData);
                return true;
            } catch (Exception e) {
                log.error("Error", e);
                return false;
            }
        }
        else{
            return false;
        }
    }

    public List<CompletedEntity> userCompletedTodo(String userName) {
        UserEntity userData = userRepository.findByUsername(userName);
        if(userData != null) {
            return userData.getCompletedEntitiesList();
        }
        else{
            return null;
        }
    }

    public boolean deleteCompletedTodo(ObjectId completedId, String userName) {
        try{
            UserEntity userEntity = userRepository.findByUsername(userName);
            List<CompletedEntity> userCompletedEntities = userEntity.getCompletedEntitiesList();
            userCompletedEntities.removeIf(x -> x.getId().equals(completedId));
            userRepository.save(userEntity);
            return true;
        }
        catch (Exception e){
            log.info("Error", e);
            return false;
        }
    }
}
