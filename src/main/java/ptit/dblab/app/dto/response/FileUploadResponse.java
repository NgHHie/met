package ptit.dblab.app.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
public class FileUploadResponse {
    private int status;
    private String url;
    private long size;
    private String fileName;
    private String typeFile;
}
