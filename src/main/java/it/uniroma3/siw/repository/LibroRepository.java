package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;

public interface LibroRepository extends CrudRepository<Libro, Long>{

	public List<Libro> findByTitolo(String titolo);
	
	//public List<Libro> findByAutori(Autore autori); potrebbe dare problemi in quanto sono una lista di autori
	
	public List<Libro> findByAnno(int anno);
}
