package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {
	@Autowired
    protected PasswordEncoder passwordEncoder;
	
	@Autowired
	private CredentialsRepository credentialsRepository;
	
	public Credentials getCredentials(String username) {
		return credentialsRepository.findByUsername(username).get();
	}
	
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRuolo(Credentials.USER_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}
	
	
	
}
