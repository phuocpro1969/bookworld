package pq.jdev.b001.bookstore.users.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import pq.jdev.b001.bookstore.users.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userService;

	@Autowired
	CustomSuccessHandler successHandler;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/**", "/registration**", "/forgot-password**", "/reset-password**", "/publishersList**")
				.permitAll().antMatchers("/js/**", "/css/**", "/img/**","/images/**", "/webjars/**", "/user**").permitAll()
				.antMatchers("/accountUser").access("hasRole('EMPLOYEE')")
				.antMatchers("/bookview", "/bookview/success", "/bookview/error").permitAll()
				.antMatchers("/user**").access("hasRole('EMPLOYEE') and hasRole('ADMIN')")
				.antMatchers("/admin**", "/accountAdmin").access("hasRole('ADMIN')")
				.antMatchers("/uploads/**", "/bookview/createform", "/bookview/editform", "/bookview/information")
				.hasAnyRole("EMPLOYEE", "ADMIN").anyRequest().authenticated().and().formLogin().loginPage("/login")
				.successHandler(successHandler).permitAll().and().logout().invalidateHttpSession(true)
				.clearAuthentication(true).logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/?logout").permitAll().deleteCookies("JSESSIONID").and().exceptionHandling()
				.accessDeniedPage("/403");
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService);
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);

		mailSender.setUsername("phuocpro1969@gmail.com");
		mailSender.setPassword("03096969");
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.smtp.starttls.enable", "true");
		return mailSender;
	}

}