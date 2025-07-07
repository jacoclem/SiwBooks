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
            if (credentials.getRuolo().equals(Credentials.CLIENT_ROLE)) {
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
        return "registrazione"; // Pagina di registrazione
    }

    // Mostra la pagina di login
    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenziali errate, riprova.");
        }
        return "login"; // Pagina di login
    }

    // Pagina di successo dopo login
    @GetMapping("/success")
    public String successPage(Model model) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        model.addAttribute("username", userDetails.getUsername());
        return "index";
    }
    
    
    @PostMapping("/registrazione")
    public String registerClient(@Valid @ModelAttribute("utente") Utente utente,
                                 BindingResult utenteBindingResult,
                                 @Valid @ModelAttribute("credentials") Credentials credentials,
                                 BindingResult credentialsBindingResult,
                                 Model model) {

        // Controllo che la password e la conferma siano uguali
        if (!credentials.getPassword().equals(credentials.getPasswordConfirm())) {
            credentialsBindingResult.rejectValue("password", "error.credentials", "- Le password non coincidono");
        }

        // Se non ci sono errori, salvo le credenziali
        if (!utenteBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            credentials.setUtente(utente);
            credentials.setRuolo(Credentials.CLIENT_ROLE);
            credentialsService.saveCredentials(credentials);
            return "redirect:/login"; // o redirect se preferisci
        }

        // Se ci sono errori, torno alla pagina di registrazione
        return "registrazione";
    }


    
    
    
}
