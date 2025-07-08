package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.AutoreRepository;
import it.uniroma3.siw.service.AutoreService;

@Controller
public class AutoreController {

	@Autowired AutoreRepository autoreRepository;
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
	
}
