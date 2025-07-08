package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Libro {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String titolo;
	
	@Column(nullable = false)
	private int anno;

	@Column(nullable = false)
	private String descrizione;
	
	@Lob
	private byte[] immagine;
	
	@ManyToMany(mappedBy = "libri")
	private List<Autore> autori;
	
	@OneToMany(mappedBy = "libro")
	private List<Recensione> recensioni;

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
	 * @return the immagine
	 */
	public byte[] getImmagine() {
		return immagine;
	}

	/**
	 * @param immagine the immagine to set
	 */
	public void setImmagine(byte[] immagine) {
		this.immagine = immagine;
	}

	/**
	 * @return the anno
	 */
	public int getAnno() {
		return anno;
	}

	/**
	 * @param anno the anno to set
	 */
	public void setAnno(int anno) {
		this.anno = anno;
	}

	/**
	 * @return the copertina
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param copertina the copertina to set
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * @return the autori
	 */
	public List<Autore> getAutori() {
		return autori;
	}

	/**
	 * @param autori the autori to set
	 */
	public void setAutori(List<Autore> autori) {
		this.autori = autori;
	}

	/**
	 * @return the recensioni
	 */
	public List<Recensione> getRecensioni() {
		return recensioni;
	}

	/**
	 * @param recensioni the recensioni to set
	 */
	public void setRecensioni(List<Recensione> recensioni) {
		this.recensioni = recensioni;
	}

	@Override
	public int hashCode() {
		return Objects.hash(anno, autori, titolo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Libro other = (Libro) obj;
		return anno == other.anno && Objects.equals(autori, other.autori) && Objects.equals(titolo, other.titolo);
	}
	
	
	
}
