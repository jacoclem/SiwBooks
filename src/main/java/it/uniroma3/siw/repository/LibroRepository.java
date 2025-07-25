package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;

@Repository
public interface LibroRepository extends CrudRepository<Libro, Long>{

	List<Libro> findByTitoloContainingIgnoreCase(String titolo);
	
	//public List<Libro> findByAutori(Autore autori); potrebbe dare problemi in quanto sono una lista di autori
	
	public List<Libro> findByAnno(int anno);
	
		
}
