package me.whiteship.demospringsecurityform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 100)
//@EnableWebSecurity
public class AnotherSecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .anyRequest().authenticated();
//        http.formLogin();
//        http.httpBasic();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/account/**")
                .authorizeRequests()
                .anyRequest().permitAll();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("keesun").password("{noop}123").roles("USER")
//                .and()
//                .withUser("admin").password("{noop}!@#").roles("ADMIN");
//    }
}
