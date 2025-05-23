package it.uniroma3.siw.model;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
public class Credentials {

	public static final String DEFAULT_ROLE = "DEFAULT";
	public static final String ADMIN_ROLE = "ADMIN";
	public static final String USER_ROLE = "USER";
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	
	@Column(nullable = false)
	private String ruolo;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Utente utente;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * @return the ruolo
	 */
	public String getRuolo() {
		return ruolo;
	}

	/**
	 * @param ruolo the ruolo to set
	 */
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}

	/**
	 * @return the utente
	 */
	public Utente getUtente() {
		return utente;
	}

	/**
	 * @param utente the utente to set
	 */
	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	/**
	 * @return the defaultRole
	 */
	public static String getDefaultRole() {
		return DEFAULT_ROLE;
	}

	/**
	 * @return the adminRole
	 */
	public static String getAdminRole() {
		return ADMIN_ROLE;
	}

	/**
	 * @return the clientRole
	 */
	public static String getClientRole() {
		return USER_ROLE;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ruolo, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Credentials other = (Credentials) obj;
		return Objects.equals(ruolo, other.ruolo) && Objects.equals(username, other.username);
	}
	
	

}
