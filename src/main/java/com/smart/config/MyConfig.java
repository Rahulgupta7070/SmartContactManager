package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {//UserRepository ,CustomUserDetai , UserDetailsService,homeController ye sab padhna hai or ye use huaa hai 

    // ✅ CHANGE: Custom success handler ko inject kiya
    //private final LoginSuccessHandler loginSuccessHandler;

    // ✅ CHANGE: Constructor banaya injection ke liye
   // public MyConfig(LoginSuccessHandler loginSuccessHandler) {
       // this.loginSuccessHandler = loginSuccessHandler;
   // }

    @Bean
    public UserDetailsService getUserDetailService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(getUserDetailService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager bean for authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Main Security Configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/signin") // agar tum custom login page banana chahte ho
            		///.loginPage("/login")  // custom page ka URL
            		    .loginProcessingUrl("/dologin") // optional - form submit action
                       // .successHandler(loginSuccessHandler) // ✅ CHANGE: Custom success handler add kiya
                        .defaultSuccessUrl("/user/index")
                       // .failureUrl("/login-fail")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
