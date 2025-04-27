package ptit.dblab.app.sequenceTable;


import ptit.dblab.shared.common.BaseSequence;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class UserCodeSequence extends BaseSequence {
}
