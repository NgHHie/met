package ptit.dblab.app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import ptit.dblab.app.enumerate.QuestionStatus;
import ptit.dblab.shared.enumerate.TypeQuestion;
import ptit.dblab.shared.common.entity.BaseEntity;
import ptit.dblab.app.enumerate.LevelQuestion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Question extends BaseEntity {
    private String questionCode;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String image;
    private float point;

    @Enumerated(EnumType.STRING)
    private TypeQuestion type;

    private String prefixCode;
    private boolean enable;

    @Column(name = "is_synchorus", nullable = false)
    private Boolean isSynchorus = false;

    @Enumerated(EnumType.STRING)
    private LevelQuestion level;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isShare = false;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @JsonManagedReference
    private List<QuestionDetail> questionDetails;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @Column(name = "is_deleted", nullable = true)
    private Boolean isDeleted = false;
}
