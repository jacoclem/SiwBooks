package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;

@Entity
public class Recensione {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Size(max = 80, message = "Il titolo può contenere al massimo 80 caratteri")
	@Column(nullable = false)
	private String titolo;
	
	@Column(nullable = false)
	private int voto;
	
	@Size(max = 245, message = "Il testo può contenere al massimo 245 caratteri")
	@Column(nullable = false)
	private String testo;
	
	@ManyToOne(optional = true)
	private Libro libro;
	
	@ManyToOne(optional = true)
	private Credentials utenteCredentials;
	

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
	 * @return the titolo
	 */
	public String getTitolo() {
		return titolo;
	}

	/**
	 * @param titolo the titolo to set
	 */
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	/**
	 * @return the voto
	 */
	public int getVoto() {
		return voto;
	}

	/**
	 * @param voto the voto to set
	 */
	public void setVoto(int voto) {
		this.voto = voto;
	}

	/**
	 * @return the testo
	 */
	public String getTesto() {
		return testo;
	}

	/**
	 * @param testo the testo to set
	 */
	public void setTesto(String testo) {
		this.testo = testo;
	}

	/**
	 * @return the libro
	 */
	public Libro getLibro() {
		return libro;
	}

	/**
	 * @param libro the libro to set
	 */
	public void setLibro(Libro libro) {
		this.libro = libro;
	}

	/**
	 * @return the utenteCredentials
	 */
	public Credentials getUtenteCredentials() {
		return utenteCredentials;
	}

	/**
	 * @param utenteCredentials the utenteCredentials to set
	 */
	public void setUtenteCredentials(Credentials utenteCredentials) {
		this.utenteCredentials = utenteCredentials;
	}

	@Override
	public int hashCode() {
		return Objects.hash(testo, titolo, voto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recensione other = (Recensione) obj;
		return Objects.equals(testo, other.testo) && Objects.equals(titolo, other.titolo) && voto == other.voto;
	}

	
	
	
	
}
