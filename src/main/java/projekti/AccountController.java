
package projekti;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {
    private String searchString;
    
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
        
    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerNewAccount(@RequestParam String username, @RequestParam String name,
            @RequestParam String password, @RequestParam String publicName) {
        if (accountRepository.findByUsername(username) != null) {
            return "redirect:/register";
        }
        passwordEncoder = new BCryptPasswordEncoder();
        Account a = new Account(username, name, passwordEncoder.encode(password), publicName, new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
        accountRepository.save(a);
        return "redirect:/login";   
    }
    
    
    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("account", accountRepository.findByUsername(username));
        return "profile";
    }
    
 
    
    @GetMapping("/index")
    public String getIndex(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("name", accountRepository.findByUsername(username).getName());
        return "index";
    }
    
    @GetMapping("/search")
    public String showSearchResults(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account myAccount = accountRepository.findByUsername(username);
        List<Account>lista = accountRepository.findAll()
                .stream()
                .filter(a -> (!a.getUsername().equals( myAccount.getUsername())))
                .collect(Collectors.toList());
        List<Account>tulokset= new ArrayList<>();
        if (lista.size()>0) {
            for (int i=0; i<lista.size(); i++) {
                String name = lista.get(i).getPublicName();
                if (lista.get(i).getPublicName().contains(searchString)) {
                    
                    tulokset.add(lista.get(i));
                }
            }
        }
        
        model.addAttribute("results", tulokset);
        return "results";
    } 
    
    @PostMapping("/search")
    public String searchForAccounts(@RequestParam String searchString) {
        this.searchString=searchString;
        return "redirect:/search";      
    }
    
}
