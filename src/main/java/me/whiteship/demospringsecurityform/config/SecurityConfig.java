package me.whiteship.demospringsecurityform.config;

import me.whiteship.demospringsecurityform.account.AccountService;
import me.whiteship.demospringsecurityform.common.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 50)
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AccountService accountService;

//    public AccessDecisionManager accessDecisionManager() {
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
//
//        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
//        handler.setRoleHierarchy(roleHierarchy);
//
//        WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
//        webExpressionVoter.setExpressionHandler(handler);
//        List<AccessDecisionVoter<? extends Object>> voters = Arrays.asList(webExpressionVoter);
//        return new AffirmativeBased(voters);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .antMatcher("/**")
//                .authorizeRequests()
//                .mvcMatchers("/", "/info", "/account/**").permitAll()
//                .mvcMatchers("/admin").hasRole("ADMIN")
//                .mvcMatchers("/user").hasRole("USER")
//                .anyRequest().authenticated()
//                .accessDecisionManager(accessDecisionManager())
//        ;
//        http.formLogin();
//        http.httpBasic();
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .antMatcher("/account/**")
//                .authorizeRequests()
//                .anyRequest().permitAll();
//    }

    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        return handler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);

        http
                .antMatcher("/**")
                .authorizeRequests()
                .mvcMatchers("/", "/info", "/account/**", "/signup").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .mvcMatchers("/user").hasRole("USER")
                .anyRequest().authenticated()
//                .anyRequest().rememberMe()
//                .anyRequest().fullyAuthenticated()
                .expressionHandler(expressionHandler())
        ;
        http.formLogin()
//                .usernameParameter("my-username")
//                .passwordParameter("my-password")
                .loginPage("/login")
                .permitAll();
        http.httpBasic();
//        http.csrf().disable();

        http.logout()
//                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
        ;

        http.rememberMe()
//                .rememberMeParameter("remember")
//                .tokenValiditySeconds(10)
//                .useSecureCookie(true)
//                .alwaysRemember(true)
                .userDetailsService(accountService)
                .key("remember-me-sample");

        http.sessionManagement()
//                .sessionFixation().changeSessionId()
//                .invalidSessionUrl("/login")
//                .maximumSessions(1).maxSessionsPreventsLogin(true)
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;

//        http.anonymous().principal("anonymousUser");

        //TODO ExceptionTranslationFilter -> FilterSecurityInterceptor (AccessDecisionManager, AffirmativeBased)
        //TODO AuthenticationException -> AuthenticationEntryPoint
        //TODO AccessDeniedException -> AccessDeniedHandler

        http.exceptionHandling()
//                .accessDeniedPage("/access-denied")
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = principal.getUsername();
                    System.out.println(username + " is denied to access " + request.getRequestURI());
                    response.sendRedirect("/access-denied");
                })
        ;

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("keesun").password("{noop}123").roles("USER")
//                .and()
//                .withUser("admin").password("{noop}!@#").roles("ADMIN");
//    }


    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().mvcMatchers("/favicon.ico");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
