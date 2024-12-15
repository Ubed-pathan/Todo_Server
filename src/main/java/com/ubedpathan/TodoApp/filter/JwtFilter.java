package com.ubedpathan.TodoApp.filter;

import com.ubedpathan.TodoApp.service.JWTService;
import com.ubedpathan.TodoApp.service.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;
            String requestURI = request.getRequestURI();
            // this below code for if user has old cookie but he want to signin again then it can allow resignin
            if ("/todo/user/signin".equals(requestURI)) {
                filterChain.doFilter(request, response);
            }
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                token = authHeader.substring(7);
//                username = jwtService.extractUserName(token);
//            }

            // this below code for cookie
            if (request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("log".equals(cookie.getName())) {
                        token = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        break;
                    }
                }
            }

            if (token != null) {
                username = jwtService.extractUserName(token);
            }
        //SecurityContextHolder.getContext().getAuthentication() == null this is checking for user already authenticated
        // remember when user request then for each request on thread assign to that request until response to user. this whole code executed for each request mean user request to server ther utill user response to user only for this duration user data stored into security context after response send to user security context for perticular thread is cleared
        //if user makes one request, for that request user data stored into security context and when server response then security context clear for that request thread and if user make another request immediately then again assign new thread for that request
        // each thread has it's own security context mean thread are isolated mean one user data not reflect to another user data, if there are multiple user then there is no data  inconsistency
        // remember v.imp each thread clear it's own security context after per request response
        // because of above code i thought that what is the use of SecurityContextHolder.getContext().getAuthentication() == null this line so there is imp role os this line mean right now in my application server only one filter that is jwt filter but for big application there are multiple filter if one filter authenticate current user
        // then not need to re-authenticate user for that reason i write this line SecurityContextHolder.getContext().getAuthentication() == null to check any other filter authenticate current user with in thread for current request only for next request from user this process again start from scratch
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // this line used for code reusability mean i already writed code for UserDetailServiceImpl class  and in this class loadUserByUsername() method return same information what i want
            // i can directly take data from database it is also correct way but here i achieved reusability by below line
            UserDetails userDetails = context.getBean(UserDetailServiceImpl.class).loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                // this UsernamePasswordAuthenticationToken is only for to represent user data mean it not authenticate any data it assign user data to it's object only for to represent the uesr data
                // mean this below line and it's object it-self can't do anything only represent data but it use only for next process or for authentication data required in this UsernamePasswordAuthenticationToken object representation
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // this below line only for to add extra user information like ip-address mean from where user made request and session id
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                // this below line is only for to told spring security to enable and disable end points based on user roles but this is only upto user single request as i told above after response send to user thread security context cleared and for next request this process start from scratch (from starting)
                // this below line is only for to told spring security to enable and disable end points based on user roles this code also we need to write in spring security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

        }
        filterChain.doFilter(request, response);
    }
}
