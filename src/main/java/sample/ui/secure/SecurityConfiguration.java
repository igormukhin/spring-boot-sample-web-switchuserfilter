package sample.ui.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * Created by igor.mukhin on 05.10.2015.
 */
@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties security;

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() {
        try {
            return super.userDetailsServiceBean();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(userDetailsServiceBean());
        filter.setUsernameParameter("username");
        filter.setSwitchUserUrl("/switch_user");
        filter.setExitUserUrl("/switch_user_exit");
        filter.setTargetUrl("/");

        //filter.setSuccessHandler(authenticationSuccessHandler);
        //filter.setFailureHandler(authenticationFailureHandler());

        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/switch_user").hasRole("ADMIN")
                .antMatchers("/switch_user_exit").hasRole("PREVIOUS_ADMINISTRATOR")
                .anyRequest().fullyAuthenticated()
                .and().formLogin().loginPage("/login").failureUrl("/login?error").permitAll()
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .and().csrf().disable()
                .addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("admin").roles("ADMIN", "USER")
                .and().withUser("user").password("user").roles("USER");
    }
}
