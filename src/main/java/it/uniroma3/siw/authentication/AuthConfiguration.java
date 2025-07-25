package it.uniroma3.siw.authentication;



import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.CLIENT_ROLE;

@Configuration
@EnableWebSecurity

public class AuthConfiguration{
	
	@Autowired
	private DataSource dataSource;
	
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
		auth.jdbcAuthentication().dataSource(dataSource).authoritiesByUsernameQuery("SELECT username, ruolo from credentials WHERE username = ?")
		.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username = ?");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
		//httpSecurity.csrf().and().cors().disable()
		httpSecurity.csrf().disable().cors().disable()
		.authorizeHttpRequests()
		//.requestMatchers("/**").permitAll()
		
		//CHIUNQUE PUO' ACCEDERE
		.requestMatchers(HttpMethod.GET, "/", "/index", "/registrazione", "/login", "/css/**", "/img/**","/libro/**", "/js/**", "/vendor/**", "/favicon.ico", "/catalogoLibri", "/catalogoAutori", "/libriAutore/**", "/recensioni/**").permitAll()
		.requestMatchers(HttpMethod.POST,"/registrazione", "/login", "/search").permitAll()
		
		
		.requestMatchers(HttpMethod.GET,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
		.requestMatchers(HttpMethod.POST,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
		
		.requestMatchers(HttpMethod.GET,"/client/**").hasAnyAuthority(CLIENT_ROLE)
		.requestMatchers(HttpMethod.POST,"/client/**").hasAnyAuthority(CLIENT_ROLE)
		
		
		// tutti gli utenti autenticati possono accere alle pagine rimanenti 
		.anyRequest().authenticated()
		// LOGIN: qui definiamo il login
		.and().formLogin()
		.loginPage("/login")
		.permitAll()
		.defaultSuccessUrl("/success", true)
		.failureUrl("/login?error=true")
		// LOGOUT: qui definiamo il logout
		.and()
		.logout()
		// il logout è attivato con una richiesta GET a "/logout"
		.logoutUrl("/logout")
		// in caso di successo, si viene reindirizzati alla home
		.logoutSuccessUrl("/")
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.clearAuthentication(true).permitAll();
		return httpSecurity.build();
		}
	}