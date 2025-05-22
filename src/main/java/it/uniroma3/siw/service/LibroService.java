package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.LibroRepository;

@Service
public class LibroService {

	private LibroRepository libroRepository;
	
	public Libro getLibroById(Long id) {
		return libroRepository.findById(id).get();
	}
	
	public List<Libro> getLibroByTitolo(String titolo) {
		return libroRepository.findByTitolo(titolo);
	}
	
	public Libro save(Libro libro) {
		return libroRepository.save(libro);
	}
	
	public void deleteById(Long id) {
		libroRepository.deleteById(id);
	}
}
