package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

@Entity
public class Utente {

	@Id
	@SequenceGenerator(
	    name = "utente_generator",
	    sequenceName = "utente_seq",
	    allocationSize = 1
	)
	@GeneratedValue(
	    strategy = GenerationType.SEQUENCE,
	    generator = "utente_generator"
	)
	private Long id;
	
	@Column(nullable = false)
	@Email
	private String email;
	
	@Column(nullable = false)
	@NotBlank
	private String nome;
	
	@Column(nullable = false)
	@NotBlank
	private String cognome;
	
	@Column(nullable = false)
	@Past
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataDiNascita;

	



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}



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
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}



	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}



	/**
	 * @return the cognome
	 */
	public String getCognome() {
		return cognome;
	}



	/**
	 * @param cognome the cognome to set
	 */
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}



	/**
	 * @return the dataDiNascita
	 */
	public LocalDate getDataDiNascita() {
		return dataDiNascita;
	}



	/**
	 * @param dataDiNascita the dataDiNascita to set
	 */
	public void setDataDiNascita(LocalDate dataDiNascita) {
		this.dataDiNascita = dataDiNascita;
	}






	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}



	@Override
	public int hashCode() {
		return Objects.hash(cognome, dataDiNascita, email, nome);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utente other = (Utente) obj;
		return Objects.equals(cognome, other.cognome) && Objects.equals(dataDiNascita, other.dataDiNascita)
				&& Objects.equals(email, other.email) && Objects.equals(nome, other.nome);
	}

	


	
	
}
