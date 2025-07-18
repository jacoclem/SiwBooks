package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.repository.LibroRepository;

@Service
public class LibroService {

	@Autowired
	private LibroRepository libroRepository;
	
	public Libro getLibroById(Long id) {
		return libroRepository.findById(id).get();
	}
	
	public List<Libro> getLibroByTitolo(String titolo) {
		return libroRepository.findByTitoloContainingIgnoreCase(titolo);
	}
	
	public Libro save(Libro libro) {
		return libroRepository.save(libro);
	}
	
	public void deleteById(Long id) {
		libroRepository.deleteById(id);
	}
	
	public Double getMediaVotiById(Long id) {
	    Optional<Libro> libroOptional = libroRepository.findById(id);

	    if (libroOptional.isEmpty()) {
	        return 0.0;
	    }

	    Libro libro = libroOptional.get();
	    List<Recensione> recensioni = libro.getRecensioni();

	    if (recensioni == null || recensioni.isEmpty()) {
	        return 0.0;
	    }

	    double somma = recensioni.stream()
	        .mapToInt(Recensione::getVoto)
	        .sum();

	    int count = recensioni.size();

	    if (count == 0) {
	        return 0.0;
	    }

	    double media = somma / count;

	    // Arrotonda alla prima cifra decimale
	    return Math.round(media * 10.0) / 10.0;
	}


	
	
	public int getNumRecensioni(Long id) {
		Optional<Libro> libroOptional = libroRepository.findById(id);
		
		if (libroOptional.isEmpty()) {
			return 0;
		}
		
		Libro  libro = libroOptional.get();
		
		List<Recensione> recensioni = libro.getRecensioni();
		
		return recensioni.size();
		
	}
	

}
