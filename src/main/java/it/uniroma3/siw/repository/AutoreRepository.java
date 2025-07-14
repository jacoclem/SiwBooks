package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;

@Repository
public interface AutoreRepository extends CrudRepository<Autore, Long>{
	
}
