package it.uniroma3.siw.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.service.UtenteService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UtenteController {

	@Autowired
	private UtenteService utenteService;
	
	@GetMapping("/registrationSuccessfull")
	public String confirmRegistration(Model model) {
        return "registrationSuccessfull";
	}
	
	@GetMapping("/utente/")
	public String showUserPage(Model model) {
		return "utente/index";
	}
	
}
