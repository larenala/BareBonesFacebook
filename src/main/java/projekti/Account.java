
package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long>{
    private String username;
    private String name;
    private String password;
    private String publicName;
    
    @OneToMany(mappedBy="account")
    private List<Friend>friends;
    
    @OneToMany(mappedBy="account")
    private List<FileObject>images;
    
    @OneToMany(mappedBy="account")
    private List<Message>messages;
    
    @OneToMany(mappedBy="account")
    private List<FriendRequest>friendRequests;
}

