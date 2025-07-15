package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.uniroma3.siw.service.AutoreService;

@Controller
public class AutoreController {

	@Autowired AutoreRepository autoreRepository;
	@Autowired LibroRepository libroRepository;
	@Autowired AutoreService autoreService;
	
	
	@GetMapping( value =  "/catalogoAutori")
	public String showAutori(Model model) {
	
		model.addAttribute("autori", autoreRepository.findAll());
		return "/autori";
	}
	
	
	@GetMapping( value =  "/admin/aggiungiAutore")
	public String addAutoreForm(Model model) {
	
		model.addAttribute("autore", new Autore());
		return "/admin/aggiungiAutore";
	}
	
	
	@PostMapping("/addAutore")
	public String aggiungiNuovoLibro(@ModelAttribute("autore") Autore autore, BindingResult bindingResult, Model model){
		
		
		autoreRepository.save(autore);
		return "redirect:/catalogoAutori";
	}
	
	
	@GetMapping("/admin/editAutore/{id}")
	public String editAutore(Model model, @PathVariable Long id) {
		
		Optional<Autore> optionalAutore = this.autoreRepository.findById(id);
		
		if(optionalAutore.isPresent()) {
			Autore autore = optionalAutore.get();
			
			model.addAttribute("autore", autore);
			
		}
		
		return "/admin/editAutore";
	}
	
	
	@PostMapping("/admin/modificaAutore")
	public String editAutorePost(@ModelAttribute("autore") Autore autore) {
		
		Optional<Autore> optAutore = this.autoreRepository.findById(autore.getId());
		
		
        if (optAutore.isPresent()) {
            Autore existingAutore = optAutore.get();

            existingAutore.setCognome(autore.getCognome());
            existingAutore.setNome(autore.getNome());
            existingAutore.setDataDiMorte(autore.getDataDiMorte());
            existingAutore.setDataDiNascita(autore.getDataDiNascita());
            existingAutore.setNazionalita(autore.getNazionalita());
            
            



            this.autoreRepository.save(existingAutore);
            return "redirect:/catalogoAutori";
        } else {
            throw new IllegalArgumentException("ID autore non valido: " + autore.getId());
        }
		
	}
	
	
	/*
     * Funzioen che elimina un autore
     */
    @GetMapping("/admin/delete/{id}")
    public String deleteAutore(@PathVariable Long id) {
        Optional<Autore> optAutore = autoreRepository.findById(id);
        
        if (optAutore.isPresent()) {
            Autore autore = optAutore.get();

            for (Libro libro : autore.getLibri()) {
            	libro.getAutori().remove(autore);
                libroRepository.save(libro); 
            }

            autoreRepository.delete(autore);

            return "redirect:/catalogoAutori";
        } else {
            throw new IllegalArgumentException("ID autore non valido: " + id);
        }
    }
	
}
