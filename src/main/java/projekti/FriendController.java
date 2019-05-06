
package projekti;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class FriendController {
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    FriendRequestRepository friendRequestRepository;
    
    @Autowired
    FriendRepository friendRepository;
    
    @Autowired
    FileObjectRepository fileObjectRepository;      
    
    @GetMapping("/friends")
    public String showFriends(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account myAccount = accountRepository.findByUsername(username);       
        model.addAttribute("friends", myAccount.getFriends());
        return "friends";
    }
    
    @GetMapping("/index/{username}")
    public String showFriendsProfile(Model model, @PathVariable String username) {
        Account account = accountRepository.findByUsername(username);        
        List<Message>messages=account.getMessages();        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name= auth.getName();
        Account currentAccount = accountRepository.findByUsername(name);
        model.addAttribute("account", account);
        
        model.addAttribute("messages", messages);       
        model.addAttribute("currentUser", currentAccount);
        return "index";
    }
    
    
    @PostMapping("/viewFriend/{username}")
    public String viewFriendsProfile() {
        return "redirect:/index/{username}";
    }
    
    
    
}
