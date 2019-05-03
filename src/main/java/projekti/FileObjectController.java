
package projekti;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FileObjectController {
                
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    FileObjectRepository fileObjectRepository;
    
    @GetMapping("/images")
    public String getAlbum(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);
        List<FileObject>images = account.getImages();
        model.addAttribute("images", images);
        return "images";
    }
    
    @PostMapping("/images")
    public String postImage() {
        return null;
    }
}
