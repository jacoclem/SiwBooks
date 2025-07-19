package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class RecensioneService {

	@Autowired RecensioneRepository recensioneRepository;
	
	public void eliminaRecensioniDaLibro(Libro libro) {
		
		List<Recensione> recensioni = this.recensioneRepository.findAllByLibro(libro);
		
		for(Recensione r : recensioni) {
			this.recensioneRepository.delete(r);
		}
		
	}
	

	
}
