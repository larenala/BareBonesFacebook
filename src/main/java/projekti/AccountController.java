
package projekti;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {
    private String searchString;
    
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    MessageRepository messageRepository;
        
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
        Account a = new Account(username, name, passwordEncoder.encode(password), publicName, 
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());
        accountRepository.save(a);
        return "redirect:/login";   
    }
    
    
    
    @GetMapping("/index")
    public String getProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);
        model.addAttribute("account", account);
        List<Message>messages=account.getMessages();      
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", account);
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
                if (lista.get(i).getPublicName().toLowerCase().contains(searchString)) {
                    
                    tulokset.add(lista.get(i));
                }
            }
        }        
        model.addAttribute("results", tulokset);
        return "results";
    } 
    
    @PostMapping("/search")
    public String searchForAccounts(@RequestParam String searchString) {
        this.searchString=searchString.toLowerCase().trim();
        return "redirect:/search";      
    }
    
}
