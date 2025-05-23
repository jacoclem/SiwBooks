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
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    CredentialsService credentialsService;

    // Mostra la home (per utenti autenticati o non autenticati)
    @GetMapping(value = "/")
    public String showHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index"; // Pagina pubblica
        } else {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            if (credentials.getRuolo().equals(Credentials.ADMIN_ROLE)) {
                return "index"; // Admin
            }
            if (credentials.getRuolo().equals(Credentials.USER_ROLE)) {
                return "index"; // Utente
            }
        }
        return "index"; // Ritorna alla home
    }

    // Mostra la pagina di registrazione
    @GetMapping("/registrazione")
    public String showRegisterPage(Model model) {
        model.addAttribute("utente", new Utente());
        model.addAttribute("credentials", new Credentials());
        return "formRegistrazione"; // Pagina di registrazione
    }

    // Mostra la pagina di login
    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenziali errate, riprova.");
        }
        return "formLogin"; // Pagina di login
    }

    // Pagina di successo dopo login
    @GetMapping("/success")
    public String successPage() {
        return "index"; // La pagina di successo, può essere la home o una dashboard
    }
    
    
    @PostMapping(value = {"/registrazione"})
	public String registerClient(@Valid @ModelAttribute("utente") Utente utente,
	                             BindingResult utenteBindingResult,
	                             @Valid @ModelAttribute("credentials") Credentials credentials,
	                             @RequestParam("confermaPassword") String confermaPassword,
	                             BindingResult credentialsBindingResult,
	                             Model model) {
    	
	    
	    // Stampa i dati che arrivano nel controller
	    System.out.println("Utente: " + utente);
	    System.out.println("Credentials: " + credentials);
	    System.out.println("Conferma Password:" + confermaPassword);

	    // Log degli errori
	    System.out.println("Errori utente: " + utenteBindingResult.getAllErrors());
	    System.out.println("Errori credenziali: " + credentialsBindingResult.getAllErrors());
	    
	    System.out.println(credentialsBindingResult.hasErrors());
	    System.out.println(utenteBindingResult.hasErrors());

	    if (!credentials.getPassword().equals(confermaPassword)) {
	        credentialsBindingResult.rejectValue("passwordConfirm", "error.credentials", "Le password non coincidono");
	    }

	    if (!utenteBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
	        credentials.setUtente(utente);
	        credentialsService.saveCredentials(credentials);
	        model.addAttribute("utente", utente);
	        return "registrationSuccessfull";
	    }

	    
	    if (credentialsBindingResult.hasFieldErrors("passwordConfirm")) {
	        model.addAttribute("errorsPasswordConfirm", credentialsBindingResult.getFieldErrors("passwordConfirm")
	            .stream()
	            .map(err -> err.getDefaultMessage())
	            .toList());
	    }

	    return "formRegistrazione";
	}

    
    
    
}
