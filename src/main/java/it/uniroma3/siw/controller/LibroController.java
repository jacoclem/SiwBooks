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
import it.uniroma3.siw.service.RecensioneService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class LibroController {

	@Autowired LibroService libroService;
	@Autowired LibroRepository libroRepository;
	@Autowired AutoreRepository autoreRepository;
	@Autowired RecensioneService recensioneService;
	
	
	
	/*
	 * Funzione chet che restituisce il form per aggiungere un libro, aggiunge come parametro anche tutti gli autori
	 */
	@GetMapping(value = "/admin/aggiungiLibro")
	public String getFormAddBook(Model model) {
		
		List<Autore> autori = (List<Autore>) autoreRepository.findAll();
		
		
		model.addAttribute("libro", new Libro());
		model.addAttribute("autori", autori);
		return "/admin/aggiungiLibro";
	}
	
	
	/*
	 * Funzione Post che aggiunge un libro al catalogo
	 */
	@Transactional
	@PostMapping("/admin/addLibro")
	public String aggiungiNuovoLibro(
	    @Valid @ModelAttribute("libro") Libro libro, 
	    BindingResult bindingResult,
	    @RequestParam("fileImmagine") MultipartFile file,
	    @RequestParam("autore") Long id,
	    Model model
	) {
	    if (bindingResult.hasErrors()) {
	    	
	    	System.out.println("Errori di validazione:");
	    	bindingResult.getFieldErrors().forEach(err -> {
	    	    System.out.println("Campo: " + err.getField() + " - Messaggio: " + err.getDefaultMessage());
	    	});
	        List<Autore> autori = (List<Autore>) autoreRepository.findAll();
	        model.addAttribute("autori", autori);
	        return "/admin/aggiungiLibro";
	    }

	    
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

	    
	    List<Autore> autori = libro.getAutori();
	    if (autori == null) {
	        autori = new ArrayList<>();
	    }
	    autori.add(autore);
	    libro.setAutori(autori);

	    List<Libro> libriAutore = autore.getLibri();
	    if (libriAutore == null) {
	        libriAutore = new ArrayList<>();
	    }
	    libriAutore.add(libro);
	    autore.setLibri(libriAutore);

	    
	    this.libroRepository.save(libro);
	    this.autoreRepository.save(autore);

	    System.out.println("Ci arrivo 4");
	    
	    return "redirect:/catalogoLibri";
	}


	
	/*
	 * Funzione che restituisce l'immagine del libro
	 */
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
    
    
    /*
     * Funzione get che restituisce la pagina del catalogo dei libri
     */
    @GetMapping("/catalogoLibri")
    public String getLibri(Model model) {
		List<Libro> libri = (List<Libro>) libroRepository.findAll();

	    
	    Map<Long, Double> medieVoti = new HashMap<>();
	    Map<Long, Integer> countRecensioni = new HashMap<>();
	    for (Libro libro : libri) {
	        Double media = libroService.getMediaVotiById(libro.getId()); 
	        if(media==null) {
	        	media = 0.0;
	        }
	        Integer count = libroService.getNumRecensioni(libro.getId());
	        medieVoti.put(libro.getId(), media);
	        countRecensioni.put(libro.getId(), count);
	    }

	    model.addAttribute("libri", libri);
	    model.addAttribute("medieVoti", medieVoti);
	    model.addAttribute("numRecensioni", countRecensioni);

	    return "/catalogo";
    }
    
    
    /*
     * Funzione che restituisce i libri che contengono una o più parole della ricerca
     */
    @Transactional
    @PostMapping("/search")
    public String showBookForTitle(Model model,
                                   @RequestParam("ricerca") String search,
                                   @RequestParam("starMin") int starMin,
                                   @RequestParam("starMax") int starMax) {

        List<Libro> libriTrovati = libroService.getLibroByTitolo(search);

        List<Libro> libriFiltrati = new ArrayList<>();
        Map<Long, Double> medieVoti = new HashMap<>();
        Map<Long, Integer> countRecensioni = new HashMap<>();

        for (Libro libro : libriTrovati) {
            Double media = libroService.getMediaVotiById(libro.getId());
            Integer count = libroService.getNumRecensioni(libro.getId());

            if (media == null) media = 0.0; // Protezione da null
            if (media >= starMin && media <= starMax) {
                libriFiltrati.add(libro);
                medieVoti.put(libro.getId(), media);
                countRecensioni.put(libro.getId(), count);
            }
        }

        model.addAttribute("libri", libriFiltrati);
        model.addAttribute("medieVoti", medieVoti);
        model.addAttribute("numRecensioni", countRecensioni);
        model.addAttribute("ricerca", search);
        model.addAttribute("starMin", starMin);
        model.addAttribute("starMax", starMax);

        return "catalogo"; // Rimuovi lo slash iniziale se usi Thymeleaf
    }

    
    
    /*
     * Funzioen che elimina un libro
     */
    @Transactional
    @GetMapping("/admin/eliminaLibro/{id}")
    public String deleteLibro(@PathVariable Long id) {
        Optional<Libro> optLibro = libroRepository.findById(id);
        
        if (optLibro.isPresent()) {
            Libro libro = optLibro.get();

            // Rimuovi il libro dalla lista dei libri di ogni autore
            for (Autore autore : libro.getAutori()) {
                autore.getLibri().remove(libro);
                autoreRepository.save(autore); // aggiorna autore per persistere il cambiamento
            }
            
            this.recensioneService.eliminaRecensioniDaLibro(libro);

            // Ora puoi eliminare il libro in sicurezza
            libroRepository.delete(libro);

            return "redirect:/catalogoLibri";
        } else {
            throw new IllegalArgumentException("ID libro non valido: " + id);
        }
    }
    
    

    
    
    
    
    /*
     * Funzione get per la modifica un libro
     */
    @GetMapping("/admin/modificaLibro/{id}")
    public String modificaAutoQueryParam(@PathVariable Long id, Model model) {
        Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID libro non valido: " + id));
        model.addAttribute("libro", libro);
        System.out.println(libro);
        return "/admin/editLibro";
    }


    
    /*
     * Funzione post che modifica un libro
     */
    @PostMapping("/admin/modificaLibro")
    public String modificalibro(
            @ModelAttribute("libro") Libro libro,
            @RequestParam("file") MultipartFile file) {

        Optional<Libro> optLibro = libroRepository.findById(libro.getId());
        if (optLibro.isPresent()) {
            Libro existingLibro = optLibro.get();

            existingLibro.setTitolo(libro.getTitolo());
            existingLibro.setAnno(libro.getAnno());
            existingLibro.setDescrizione(libro.getDescrizione());

            if (file != null && !file.isEmpty()) {
                try {
                    existingLibro.setImmagine(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    // eventualmente aggiungi messaggio di errore nel model
                }
            }

            libroRepository.save(existingLibro);
            return "redirect:/catalogoLibri";
        } else {
            throw new IllegalArgumentException("ID libro non valido: " + libro.getId());
        }
    }
    
    
    /*
     * Funzione get che restituisce la lista di autori da aggiungere ad un libro
     */
    @GetMapping("admin/listaAutori/{id}")
    public String getAutoriDisponibili(@PathVariable Long id, Model model) {
    	
    	Optional<Libro> libroOptional = this.libroRepository.findById(id);
    	
    	if(!libroOptional.isPresent())
    		return "/";
    	
    	Libro libro = libroOptional.get();
    	
    	List<Autore> autoriDelLibro = libro.getAutori();
    	
    	List<Autore> autori = (List<Autore>) this.autoreRepository.findAll();
    	
    	model.addAttribute("autoriDelLibro", autoriDelLibro);
    	
    	model.addAttribute("autori", autori);
    	
    	model.addAttribute("libroId", id);
    	model.addAttribute("libroTitolo", libro.getTitolo());
    	
    	return "/admin/listaAutoriDaAggiungere";
    }
    
    /*
     * Funzione post che rimuove un autore dalla lista di autori di un libro
     */
    @Transactional
    @PostMapping("admin/rimuoviAutore")
    public String removeAutore(@RequestParam("autoreId") Long autoreId, @RequestParam("libroId") Long libroId) {
        Optional<Libro> optionalLibro = libroRepository.findById(libroId);
        Optional<Autore> optionalAutore = autoreRepository.findById(autoreId);

        if (optionalLibro.isPresent() && optionalAutore.isPresent()) {
            Libro libro = optionalLibro.get();
            Autore autore = optionalAutore.get();

            if (libro.getAutori().contains(autore)) {
                libro.getAutori().remove(autore);
            }

            if (autore.getLibri().contains(libro)) {
                autore.getLibri().remove(libro);
            }

            libroRepository.save(libro);
            autoreRepository.save(autore); // salva il lato inverso
        }

        return "redirect:/admin/listaAutori/" + libroId;
    }

    
    /*
     * Funzione post che aggiunge un autore ad un libro
     */
    @Transactional
    @PostMapping("admin/aggiungiAutore")
    public String aggiungiAutore(@RequestParam("autoreId") Long autoreId, @RequestParam("libroId") Long libroId) {
        Optional<Libro> optionalLibro = libroRepository.findById(libroId);
        Optional<Autore> optionalAutore = autoreRepository.findById(autoreId);

        if (optionalLibro.isPresent() && optionalAutore.isPresent()) {
            Libro libro = optionalLibro.get();
            Autore autore = optionalAutore.get();

            if (!libro.getAutori().contains(autore)) {
                libro.getAutori().add(autore);
            }

            if (!autore.getLibri().contains(libro)) {
                autore.getLibri().add(libro);
            }

            libroRepository.save(libro);
            autoreRepository.save(autore); // necessario per mantenere in sync il lato inverso
        }

        return "redirect:/admin/listaAutori/" + libroId;
    }


    
    /*
     * Funzione get che restituisce tutti i libri di un autore
     */
    @Transactional
    @GetMapping("/libriAutore/{id}/")
    public String getLibriAutore(@PathVariable Long id, Model model) {
        Optional<Autore> optionalAutore = this.autoreRepository.findById(id);

        if (optionalAutore.isEmpty()) {
            return "redirect:/catalogoAutori"; // oppure una pagina di errore personalizzata
        }

        Autore autore = optionalAutore.get();
        List<Libro> libri = autore.getLibri();

        // Calcolo media voti e numero recensioni per ogni libro
        Map<Long, Double> medieVoti = new HashMap<>();
        Map<Long, Integer> countRecensioni = new HashMap<>();
        for (Libro libro : libri) {
            Double media = libroService.getMediaVotiById(libro.getId());
            Integer count = libroService.getNumRecensioni(libro.getId());
            medieVoti.put(libro.getId(), media);
            countRecensioni.put(libro.getId(), count);
        }

        model.addAttribute("libri", libri);
        model.addAttribute("medieVoti", medieVoti);
        model.addAttribute("numRecensioni", countRecensioni);

        return "/catalogo"; // senza slash iniziale
    }


    
    
    
}
