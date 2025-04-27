package ptit.dblab.app.controller;

import ptit.dblab.app.dto.response.FileUploadResponse;
import ptit.dblab.app.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileUploadService.uploadFile(file));
    }


    @GetMapping("/files/{dateFolder}/{filename:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String dateFolder, @PathVariable String filename){
        byte[] file = fileUploadService.getFile(dateFolder, filename);
        String mimeType = fileUploadService.getMimeType(dateFolder, filename);
        if(file != null && mimeType != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, mimeType)
                    .body(file);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}

