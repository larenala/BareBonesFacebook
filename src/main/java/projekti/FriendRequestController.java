
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FriendRequestController {
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    FriendRequestRepository friendRequestRepository;
    
    @Autowired
    FriendRepository friendRepository;
    
    @PostMapping("/addFriend/{username}")
    public String sendFriendRequest(@PathVariable String username) {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Account myAccount = accountRepository.findByUsername(name);
        
        Account friend = accountRepository.findByUsername(username); 
        
        List<Friend>myFriends=myAccount.getFriends();
        
        for(int i=0; i<myFriends.size(); i++) {
            if (myFriends.get(i).getUsername().equals(username)) {            
                return "redirect:/friends";
            }    
        }
                       
        List<FriendRequest>newList = friend.getFriendRequests();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = time.format(formatter);
        
        FriendRequest newRequest = new FriendRequest(name, myAccount.getPublicName(), formattedTime, friend);
        newList.add(newRequest);
        friend.setFriendRequests(newList);
        accountRepository.save(friend);
        friendRequestRepository.save(newRequest);
        return "redirect:/index";
    }
    
    @GetMapping("/requests")
    public String getFriendRequests(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Account myAccount = accountRepository.findByUsername(name);
        List<FriendRequest>myList=myAccount.getFriendRequests()
                .stream()
                .filter(a -> !(a.getSenderUsername().equals(name)))
                .collect(Collectors.toList());
        model.addAttribute("requests", myList);
        return "requests";
    }
    
    @PostMapping("/requests/{id}")
    public String acceptRequest(@PathVariable Long id) {
        FriendRequest request = friendRequestRepository.getOne(id);
      
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account thisAccount = accountRepository.findByUsername(username);        
        String senderUsername=request.getSenderUsername();        
        Account sender = accountRepository.findByUsername(senderUsername);
        Friend friend1 = new Friend(username, thisAccount.getPublicName(), sender);
        Friend friend2 = new Friend(senderUsername, sender.getPublicName(), thisAccount);
        friendRepository.save(friend1);
        friendRepository.save(friend2);
        friendRequestRepository.delete(request);
        return "redirect:/friends";
    }
    
    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable Long id) {
        FriendRequest request = friendRequestRepository.getOne(id);
        friendRequestRepository.delete(request);
        return "redirect:/requests";
    }
    
    
}
