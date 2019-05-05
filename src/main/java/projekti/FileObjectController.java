
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
        model.addAttribute("images", images);
        return "images";
    }
    
    @PostMapping("/images")
    public String postImage(@RequestParam("file") MultipartFile file, @RequestParam String description) throws IOException {
        FileObject fo = new FileObject();
        fo.setContent(file.getBytes());
        fo.setDescription(description);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username);
        List<FileObject>images = account.getImages();
        images.add(fo);
        fo.setAccount(account);
        accountRepository.save(account);
        fileObjectRepository.save(fo);
        return "redirect:/images";
    }
    
    @GetMapping("images/{id}/content")
    @ResponseBody
    public byte[] get(@PathVariable Long id) {
        return fileObjectRepository.getOne(id).getContent();
    }
    
}
