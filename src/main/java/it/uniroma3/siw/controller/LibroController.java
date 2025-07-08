package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.AutoreRepository;
import it.uniroma3.siw.repository.LibroRepository;
import it.uniroma3.siw.service.LibroService;

@Controller
public class LibroController {

	@Autowired LibroService libroService;
	@Autowired LibroRepository libroRepository;
	@Autowired AutoreRepository autoreRepository;
	
	
	
	
	@GetMapping(value = "/admin/aggiungiLibro")
	public String getFormAddBook(Model model) {
		
		List<Autore> autori = (List<Autore>) autoreRepository.findAll();
		
		System.out.println("La dimensione di autori è: " + autori.size());
		
		model.addAttribute("libro", new Libro());
		model.addAttribute("autori", autori);
		return "/admin/aggiungiLibro";
	}
	
	@PostMapping("/addLibro")
	public String aggiungiNuovoLibro(
	    @ModelAttribute("libro") Libro libro, 
	    BindingResult bindingResult,
	    @RequestParam("immagine") MultipartFile file,
	    @RequestParam("autore") Long id,
	    Model model
	) {
	    try {
	        if (file != null && !file.isEmpty()) {
	            libro.setImmagine(file.getBytes());
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        model.addAttribute("errore", "Errore nel caricamento dell'immagine");
	        return "/admin/aggiungiLibro";
	    }

	    Autore autore = this.autoreRepository.findById(id).orElse(null);

	    if (autore == null) {
	        model.addAttribute("errore", "Autore non trovato");
	        return "/admin/aggiungiLibro";
	    }

	    // Aggiungi l'autore al libro
	    List<Autore> autori = libro.getAutori();
	    if (autori == null) {
	        autori = new ArrayList<>();
	    }
	    autori.add(autore);
	    libro.setAutori(autori);

	    // Aggiungi il libro all'autore (relazione bidirezionale!)
	    List<Libro> libriAutore = autore.getLibri();
	    if (libriAutore == null) {
	        libriAutore = new ArrayList<>();
	    }
	    libriAutore.add(libro);
	    autore.setLibri(libriAutore);

	    // Salva entrambi
	    this.libroRepository.save(libro);
	    this.autoreRepository.save(autore);

	   

	    return "redirect:/catalogoLibri";
	}

	
	 // Restituisce immagine dell'auto
    @GetMapping("/libro/{id}/immagine")
    public ResponseEntity<byte[]> getImmagine(@PathVariable Long id) {
        Libro libro = libroRepository.findById(id).orElse(null);
        if (libro == null || libro.getImmagine() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(libro.getImmagine());
    }
    
    
    @GetMapping("/catalogoLibri")
    public String getLibri(Model model) {
		List<Libro> libri = (List<Libro>) libroRepository.findAll();

	    
	    Map<Long, Double> medieVoti = new HashMap<>();
	    Map<Long, Integer> countRecensioni = new HashMap<>();
	    for (Libro libro : libri) {
	    	System.out.println("DIMENSIONE AUTORI (1) **********************" + libro.getAutori().size());
	        Double media = libroService.getMediaVotiById(libro.getId()); 
	        Integer count = libroService.getNumRecensioni(libro.getId());
	        medieVoti.put(libro.getId(), media);
	        countRecensioni.put(libro.getId(), count);
	    }

	    model.addAttribute("libri", libri);
	    model.addAttribute("medieVoti", medieVoti);
	    model.addAttribute("numRecensioni", countRecensioni);

	    return "/catalogo";
    }
}
