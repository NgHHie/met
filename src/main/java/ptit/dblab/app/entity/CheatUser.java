package ptit.dblab.app.entity;

import ptit.dblab.shared.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CheatUser extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String contestId;
}
