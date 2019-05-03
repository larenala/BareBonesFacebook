
package projekti;

import javax.persistence.Entity;

import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileObject extends AbstractPersistable <Long> {
    @Lob
    private byte[] content;
    @ManyToOne
    private Account account;
    private int likes;   
}
