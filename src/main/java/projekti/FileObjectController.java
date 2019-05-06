
package projekti;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
        model.addAttribute("account", account);
        model.addAttribute("images", images);
        model.addAttribute("currentUser", account);
        return "images";
    }
    
    @GetMapping("/images/{username}")
    public String showFriendsImages(Model model, @PathVariable String username) {
        Account account = accountRepository.findByUsername(username);
        List<FileObject>images = account.getImages();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Account currentAccount = accountRepository.findByUsername(currentUsername);
        model.addAttribute("account", account);
        model.addAttribute("images", images);
        model.addAttribute("currentUser", currentAccount);
        return "images";
    }
    
    
    @PostMapping("/images")
    public String postImage(@RequestParam("file") MultipartFile file, @RequestParam String description) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);
        List<FileObject>images = account.getImages();
        if (images.size() > 9) {
            return "redirect:/images";
        }
        FileObject fo = new FileObject();
        fo.setContent(file.getBytes());
        fo.setDescription(description);
        
        images.add(fo);
        fo.setAccount(account);
        accountRepository.save(account);
        fileObjectRepository.save(fo);
        return "redirect:/images";
    }
    
    @PostMapping("likeImage/{id}/{username}")
    public String likeImage(@PathVariable Long id) {
        FileObject fo= fileObjectRepository.getOne(id);        
        fo.setLikes(fo.getLikes()+1);
        fileObjectRepository.save(fo);   
        String recipientsUsername = fo.getAccount().getUsername();
        return "redirect:/images/" + recipientsUsername;
    }

    
    @PostMapping("deleteImage/{id}")
    public String deleteImage(@PathVariable Long id) {
        FileObject fo = fileObjectRepository.getOne(id);
        fileObjectRepository.delete(fo);
        return "redirect:/images";
    }           
    
    @GetMapping("images/{id}/content")
    @ResponseBody
    public byte[] get(@PathVariable Long id) {
        return fileObjectRepository.getOne(id).getContent();
    }
    
}
