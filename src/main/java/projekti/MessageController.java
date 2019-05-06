
package projekti;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    MessageRepository messageRepository;
    
    
    @PostMapping("messages/{username}")
    public String postMessage(@RequestParam String message, @PathVariable String username) {
        Account recipient = accountRepository.findByUsername(username);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account myAccount = accountRepository.findByUsername(auth.getName());
        Message newMessage =new Message(myAccount.getPublicName(), LocalDateTime.now(), message, 0, recipient);      
        List<Message>messages=recipient.getMessages();
        messages.add(newMessage);
        recipient.setMessages(messages);
        messageRepository.save(newMessage);
        accountRepository.save(recipient);
        return "redirect:/index/{username}";
    }
    
    @PostMapping("likeMessage/{id}/{username}")
    public String likeMessage(@PathVariable Long id) {
        Message message= messageRepository.getOne(id);
        message.setLikes(message.getLikes()+1);
        messageRepository.save(message);
        String recipientsUsername = message.getAccount().getUsername();
        return "redirect:/index/" + recipientsUsername;
    }
}
