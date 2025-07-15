package it.uniroma3.siw.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@Controller
public class RecensioneController {

	@Autowired RecensioneRepository recensioneRepository;
	@Autowired LibroRepository libroRepository;
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
    		
    		
    		model.addAttribute("haGiaRecensito", haGiaRecensito);
    		model.addAttribute("newRecensione", new Recensione());
    		model.addAttribute("libro", libro);
    		model.addAttribute("recensioni", recensioni);
    		
    	}
    	
    	
    	return "/recensioni";
    }

    
    @PostMapping("/client/creaRecensione")
    public String newRecensione(@ModelAttribute("newRecensione") Recensione recensione, 
                               BindingResult bindingResult, 
                               @RequestParam("libroId") Long id, 
                               Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
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


    
    
	
}
