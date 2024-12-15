    package com.ubedpathan.TodoApp.config;

    import com.ubedpathan.TodoApp.filter.JwtFilter;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.core.userdetails.User;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.NoOpPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.provisioning.InMemoryUserDetailsManager;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import java.util.List;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {


        // flow of security
        // 1. when user request for authentication or login then request delegate or send to UsernamePasswordAuthenticationFilter internally by default (in our application i used jwt token so after jwt filter request send to UsernamePasswordAuthenticationFilter)
        // 2. UsernamePasswordAuthenticationFilter send request to AuthenticationManager but it is interface and AuthenticationManager has only one method authenticate  so request sent to implmented class Authentication provider
        // 3. Authentication provider required userservicedetail and password encoder type but Authentication provider also a interface so we create it's objct or bean by Authentication provider implemented class and then send request to like DaoAuthenticationProvider  with userservicedetail and password encoder type and provider manager fetch data from UserDetailsService but it is interface so we want to implement this interface to in our UserServicedetailimpl.class
        // 4. UserServicedetailimpl class fetch data from database and send to Daoauthenticationprovider (Daoauthenticationprovider implements Authentication provider)
        // 5. DaoAuthenticationManager verify user password send at time of login and encode it by bcryptencoder and then match with password stored in database if match then user authenticate

        // this above flow for default security, but we want to customize security mean when user come for login then we want to authenticate it so my security code flow is
        // 1. when login request come from user then we want to run authentication manager and above default security flow so i created authenticationmanager bean or object this code at very last some lines
        // because i want to run authentication manager as per my demand and i take full control on authentication manager by creating it's bean by authentication manager's implemented class
        // 2. authentication manager send request to authentication provider
        // 3. authentication provider takes userdetailservice class implementation and password encoder type
        // 4. authentication provider send request to it's implemented class like DaoAuthenticationProvider and it performs all operation

        // remember for default flow of security request first come to UsernamePasswordAuthenticationFilter

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private JwtFilter jwtFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//            http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());

            // Order of rules matters: define permitAll() paths first
            return http.cors(cors -> cors.configurationSource(corsConfigurationSource())).authorizeHttpRequests(request -> request
                            .requestMatchers("/user/**").permitAll()
                            .requestMatchers( "/home/**","/completed/**").authenticated()
                            .anyRequest().authenticated())
//                    .httpBasic(Customizer.withDefaults())
                    // by default user authentication handle by UsernamePasswordAuthenticationFilter.class this class but here we providing our own jwt filter so it work after jwtFilter
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for testing
                    .build();
        }

        @Value("${FRONTEND_URL}")
        private String frontendUrl;
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of(frontendUrl));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
            configuration.setExposedHeaders(List.of("Authorization"));
            configuration.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

//        @Bean
//        public UserDetailsService userDetailsService() {
//
//            // Create in-memory users for authentication
//            UserDetails user1 = User
//                    .withUsername("kiran")
//                    .password("{noop}k@123") // Use {noop} for NoOpPasswordEncoder
//                    .roles("USER")
//                    .build();
//
//            UserDetails user2 = User
//                    .withUsername("harsh")
//                    .password("{noop}h@123")
//                    .roles("ADMIN")
//                    .build();
//
//            return new InMemoryUserDetailsManager(user1, user2);
//        }

        // vvv.imp note use of @Autowired and @Bean annotation
        // @Autowired use when we want to create bean but our method return type is void mean method not return anything but we want object or bean then, we can take parameter (argument) of authenticationconfigurationmanager and creat bean by using UserDetailService implementation class and password encoder type
        // this above line meaning is in my journal application security i created public void globalConfig(AuthenticationManagerBuilder) method but this method is void so i take AuthenticationManagerBuilder as parameter and create one bean
        // note when i run my application server then first spring boot search for @Configuration Annotation wrote (writed) class and then search for @Autowired and @Bean method and varible and inject beans of that methods return type mean
        // spring boot provide first priority to our code then it's internal implementation so when we use this bean or object then spring boot use our own bean first instead of internal bean or object or implementation

        // @Bean this annotation used when our method return something and we want to make bean or object of it but note it make bean of return type of method like @Bean public AuthenticationProvider authenticationProvider() here spring boot make bean or object of return type mean AuthenticationProvider AuthenticationProvider
        // but here we used DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); object to setpasswordencoder and setUserDetailsService but spring boot creates object of return type like AuthenticationProvider




        @Bean
        public AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
            provider.setUserDetailsService(userDetailsService); // Inject the bean directly
            return provider;
        }

        // this AuthenticationManager bean is created because when user request come then it send to UsernamePasswordAuthenticationFilter.class and this class send request to Authentication manager
        // but Authentication run as per default spring security flow but i want to call AuthenticationManager as per my demand mean when user send request for login then i want run authentication manager
        // but AuthenticationManager is interface mean we want to create it's bean by it's implemented class AuthenticationConfiguration but as i know here i used @Bean annotation so it make bean or object of
        // method return type like AuthenticationManager and from now i have fully control on authentication manager mean i call AuthenticationManager as per my demand and i called AuthenticationManager as per my demand or requirement at the time of user login
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();

        }
    }
