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
	
	public boolean existsByUsername(String username) {
		return credentialsRepository.existsByUsername(username);
	}
	
	public boolean passwordIsValid(String psw) {
	    if (psw == null || psw.length() < 8) {
	        return false;
	    }

	    boolean hasLetter = false;
	    boolean hasDigit = false;
	    boolean hasSpecialChar = false;

	    for (char c : psw.toCharArray()) {
	        if (Character.isLetter(c)) {
	            hasLetter = true;
	        } else if (Character.isDigit(c)) {
	            hasDigit = true;
	        } else if (!Character.isLetterOrDigit(c)) {
	            hasSpecialChar = true;
	        }
	    }

	    return hasLetter && hasDigit && hasSpecialChar;
	}

	
	
}
