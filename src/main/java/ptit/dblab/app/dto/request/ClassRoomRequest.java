package ptit.dblab.app.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClassRoomRequest {
    private String name;
    private String classCode;
    private String description;
    private List<UserClassRoomRequest> users;
}
