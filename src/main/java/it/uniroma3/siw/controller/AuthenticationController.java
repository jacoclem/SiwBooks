package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.controller.validator.CredentialsValidation;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired CredentialsService credentialsService;
	@Autowired CredentialsValidation credentialsValidation;
	
	@GetMapping(value="/")
	public String showHome(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "index";
		}else {		
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			if (credentials.getRuolo().equals(Credentials.ADMIN_ROLE)) {
				return "index";
			}
			if(credentials.getRuolo().equals(Credentials.USER_ROLE)) {
				return "index";
			}
		}
        return "index";
	}
	
	@GetMapping("/registrazione")
	public String showRegisterPage(Model model) {
		model.addAttribute("utente", new Utente());
		model.addAttribute("credentials", new Credentials());
		return "formRegistrazione";
	}
	
	@PostMapping("/registrazione")
	public String registerClient(@Valid @ModelAttribute("cliente") Utente utente, BindingResult clienteBindingResult,@Valid  @ModelAttribute("credentials") Credentials credentials, BindingResult credentialsBindingResult, Model model) {
		
		this.credentialsValidation.validate(credentials, credentialsBindingResult);
		//se il cliente e le credenziali hanno contenuti che rispettano i valid allora completo la registrazione del cliente
		if(!clienteBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
			credentials.setUtente(utente);
			credentialsService.saveCredentials(credentials);
			model.addAttribute("utente", utente);
			return "utente/index";
		}
		return "registrazione";
	}
	
}
