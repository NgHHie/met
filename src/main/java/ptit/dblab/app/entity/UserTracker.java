package ptit.dblab.app.entity;

import ptit.dblab.shared.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserTracker extends BaseEntity {
    private String actionType;
    @Column(columnDefinition = "TEXT")
    private String detail;
    private String contestId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}