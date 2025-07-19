package it.uniroma3.siw.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.repository.LibroRepository;
import it.uniroma3.siw.repository.RecensioneRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class RecensioneController {

	@Autowired RecensioneRepository recensioneRepository;
	@Autowired LibroRepository libroRepository;
	@Autowired LibroService libroService;
	@Autowired CredentialsService credentialsService;
	
    @GetMapping("/recensioni/{id}/")
    public String showRecensioni(Model model, @PathVariable Long id, Principal principal) {
    	
    	if (principal != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Username: " + principal.getName());
            System.out.println("Authorities:");
            for (GrantedAuthority ga : auth.getAuthorities()) {
                System.out.println(ga.getAuthority());
            }
        }
    	
    	boolean haGiaRecensito = false;
    	
    	Optional<Libro> optLibro = this.libroRepository.findById(id);
    	
    	if(optLibro.isPresent()) {
    		Libro libro = optLibro.get();
    		List<Recensione> recensioni = this.recensioneRepository.findAllByLibro(libro);
    		
    		

    		if(principal != null) {
    			Credentials credentials = credentialsService.getCredentials(principal.getName());
    			
    			haGiaRecensito  = recensioni.stream() .anyMatch(r -> r.getUtenteCredentials() != null &&
                        r.getUtenteCredentials().getId().equals(credentials.getId()));
    			
    			
    			
    			
    			}
    		
    		List<Libro> altriLibri = (List<Libro>) this.libroRepository.findAll();
    		
    		altriLibri = altriLibri.stream().filter(l -> !l.getId().equals(id)).collect(Collectors.toList());
    		
    		Collections.shuffle(altriLibri);
    		
    		List<Libro> suggeriti = altriLibri.stream().limit(3).collect(Collectors.toList());
    		
    		Double media = libroService.getMediaVotiById(libro.getId()); 
    		
    		if(media == null) {
    			media = 0.0;
    		}
    		
    		
    		model.addAttribute("media", media);
    		model.addAttribute("suggeriti", suggeriti);
    		model.addAttribute("haGiaRecensito", haGiaRecensito);
    		model.addAttribute("newRecensione", new Recensione());
    		model.addAttribute("libro", libro);
    		model.addAttribute("recensioni", recensioni);
    		
    	}
    	
    	
    	return "/recensioni";
    }

    
    @PostMapping("/client/creaRecensione")
    public String newRecensione(@Valid @ModelAttribute("newRecensione") Recensione recensione, 
                               BindingResult bindingResult, 
                               @RequestParam("libroId") Long id, 
                               Principal principal, 
                               Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
        	
        	System.out.println("Errori di validazione:");
	    	bindingResult.getFieldErrors().forEach(err -> {
	    	    System.out.println("Campo: " + err.getField() + " - Messaggio: " + err.getDefaultMessage());
	    	});
        	
            Optional<Libro> optLibro = this.libroRepository.findById(id);

            if (optLibro.isPresent()) {
                Libro libro = optLibro.get();
                List<Recensione> recensioni = this.recensioneRepository.findAllByLibro(libro);

                boolean haGiaRecensito = false;
                if (principal != null) {
                    Credentials credentials = credentialsService.getCredentials(principal.getName());
                    haGiaRecensito = recensioni.stream()
                        .anyMatch(r -> r.getUtenteCredentials() != null &&
                                       r.getUtenteCredentials().getId().equals(credentials.getId()));
                }

                List<Libro> altriLibri = (List<Libro>) this.libroRepository.findAll();
                altriLibri.remove(libro);
                Collections.shuffle(altriLibri);
                List<Libro> suggeriti = altriLibri.stream().limit(3).collect(Collectors.toList());

                Double media = libroService.getMediaVotiById(libro.getId());
                if (media == null) media = 0.0;

                model.addAttribute("media", media);
                model.addAttribute("suggeriti", suggeriti);
                model.addAttribute("haGiaRecensito", haGiaRecensito);
                model.addAttribute("newRecensione", recensione); 
                model.addAttribute("libro", libro);
                model.addAttribute("recensioni", recensioni);
            }

            return "/recensioni"; 
        }


        Credentials credentials = credentialsService.getCredentials(principal.getName());

        Optional<Libro> optLibro = this.libroRepository.findById(id);

        if (optLibro.isPresent() && Credentials.CLIENT_ROLE.equals(credentials.getRuolo())) {
            Libro libro = optLibro.get();
            recensione.setLibro(libro);
            recensione.setUtenteCredentials(credentials);

            List<Recensione> recensioni = libro.getRecensioni();
            if (recensioni == null) {
                recensioni = new ArrayList<>();
                libro.setRecensioni(recensioni);
            }
            recensioni.add(recensione);

            this.libroRepository.save(libro);
            this.recensioneRepository.save(recensione);
        }

        return "redirect:/recensioni/" + id + "/";
    }



    @GetMapping("/admin/eliminaRecensione/{id}/")
    public String eliminaRecensione(@PathVariable Long id) {
        Optional<Recensione> recensioneOpt = recensioneRepository.findById(id);

        if (recensioneOpt.isPresent()) {
            Recensione recensione = recensioneOpt.get();
            recensioneRepository.delete(recensione);
        }

        return "redirect:/catalogoLibri";
    }
    
    
    @Transactional
    @GetMapping("/client/listaRecensioni")
    public String getRecensioniByUser(Principal principal, Model model) {
    	
    	
    	Credentials credentials = this.credentialsService.getCredentials(principal.getName());
    	
    	List<Recensione> recensioni = this.recensioneRepository.findAllByUtenteCredentials(credentials);
    	
    	model.addAttribute("recensioni", recensioni);
    	
    	return "/client/mieRecensioni";
    }
    
    @Transactional
    @GetMapping("/client/eliminaRecensione/{id}/")
    public String eliminaRecensioneUser(@PathVariable Long id, Principal principal) {
        Optional<Recensione> recensioneOpt = recensioneRepository.findById(id);
        Credentials credentials = this.credentialsService.getCredentials(principal.getName());
        if (recensioneOpt.isPresent()) {
            Recensione recensione = recensioneOpt.get();
            if(recensione.getUtenteCredentials().equals(credentials)) {
            	recensioneRepository.delete(recensione);
            }
            
        }

        return "redirect:/client/listaRecensioni";
    }
    

	
}
