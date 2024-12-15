    package com.ubedpathan.TodoApp.controller;

    import com.ubedpathan.TodoApp.DTOs.UserDTO;
    import com.ubedpathan.TodoApp.entity.UserEntity;
    import com.ubedpathan.TodoApp.service.JWTService;
    import com.ubedpathan.TodoApp.service.UserDetailServiceImpl;
    import com.ubedpathan.TodoApp.service.UserService;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.validation.ObjectError;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.validation.BindingResult;
    import jakarta.validation.Valid;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/user")
    public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private JWTService jwtService;

        @Autowired
        ApplicationContext context;

        @PostMapping("/signup")
        public ResponseEntity<?> handleUserSignUp(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
            System.out.println("request recived "+ userDTO);
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                for (ObjectError error : bindingResult.getAllErrors()) {
                    errorMessage.append(error.getDefaultMessage()).append(" ");
                }
                return new ResponseEntity<>(errorMessage.toString().trim(), HttpStatus.BAD_REQUEST);
            }

            boolean result = userService.handleUserSignup(userDTO);
            if(result)
                return new ResponseEntity<>("SignUp successful", HttpStatus.OK);
            else
                return new ResponseEntity<>("User not SignUp", HttpStatus.BAD_REQUEST);
        }

        @PostMapping("/signin")
        public ResponseEntity<?> handleUserSignIn(@RequestBody UserEntity user, HttpServletResponse response){
            if((user.getUsername() != "" && user.getUsername() != null) && (user.getPassword() != "" && user.getPassword() != null)){
                String token = userService.handleUserSignin(user);
                if (token != null && !token.isEmpty() && !"fail".equals(token)) {
                    // Create a cookie
                    Cookie cookie = new Cookie("log", token);
                    cookie.setHttpOnly(true);           // Prevent access via JavaScript
                    cookie.setSecure(true);             // Send only over HTTPS
                    cookie.setPath("/");                // Available throughout the app
                    cookie.setMaxAge(7 * 24 * 60 * 60); // Valid for 7 days
//                    cookie.setDomain("yourdomain.com"); // Optional: Set your domain here
//                    cookie.setSameSite(Cookie.SameSite.STRICT);

                    response.addCookie(cookie);
                    response.setHeader("Set-Cookie", "log=" + token + "; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=" + (7 * 24 * 60 * 60));

                    Map<String, String> responseBody = new HashMap<>();
                    responseBody.put("message", "Login successful");
                    responseBody.put("username", user.getUsername());
                    return ResponseEntity.ok(responseBody);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return new ResponseEntity<>("Please fill full info",HttpStatus.BAD_REQUEST);
            }
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(HttpServletResponse response) {
            Cookie cookie = new Cookie("log", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setMaxAge(0);

            response.addCookie(cookie);

            return ResponseEntity.ok("Logged out successfully");
        }

        @GetMapping("/status")
        public ResponseEntity<?> checkLoginStatus(HttpServletRequest request) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("log".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        String username = jwtService.extractUserName(token);
                        UserDetails userDetails = context.getBean(UserDetailServiceImpl.class).loadUserByUsername(username);
                        if (jwtService.validateToken(token, userDetails)) {
                            return ResponseEntity.ok(Map.of("username", username));
                        }
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
    }
