package ptit.dblab.app.service;

import ptit.dblab.app.dto.response.FileUploadResponse;
import ptit.dblab.app.enumerate.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${spring.base-url}")
    private String baseUrl;

    public FileUploadResponse uploadFile(MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return FileUploadResponse.builder()
                        .status(ErrorCode.FILE_UPLOAD_EMPTY.getCode())
                        .url(ErrorCode.FILE_UPLOAD_EMPTY.getDescription())
                        .build();
            }
            String dateFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File dir = new File(uploadDir + dateFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String timeStamp = String.valueOf(System.currentTimeMillis());
            String originalFilename = file.getOriginalFilename();
            String newFilename = timeStamp + "_" + originalFilename;

            // Lưu file
            Path path = Paths.get(dir.getAbsolutePath() + "/" + newFilename);
            Files.write(path, file.getBytes());

            // Trả về URL để truy cập file
            String fileDownloadUri = baseUrl
                    + "/media/files/" + dateFolder + "/" + newFilename;
            long fileSize = Files.size(path);
            String fileType = Files.probeContentType(path);
            return FileUploadResponse.builder()
                    .status(ErrorCode.SUCCESS.getCode())
                    .url(fileDownloadUri)
                    .fileName(originalFilename)
                    .fileName(fileType)
                    .size(fileSize)
                    .build();

        } catch (IOException e) {
            log.error("File upload error", e);
            return FileUploadResponse.builder()
                    .status(ErrorCode.FILE_UPLOAD_ERROR.getCode())
                    .url(ErrorCode.FILE_UPLOAD_ERROR.getDescription())
                    .build();
        }
    }


    public byte[] getFile(String folder, String fileName) {
        try {
            Path path = Paths.get(uploadDir + folder + "/" + fileName);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.error("Cannot get file", e);
            return null;
        }
    }

    public void deleteFile(String folder, String fileName) {
        try {
            Path path = Paths.get(uploadDir + folder + "/" + fileName);
            Files.delete(path);
        } catch (Exception e) {
            log.error("Cannot delete file with folder = {} and file name = {}", folder, fileName, e);
        }
    }

    public String getMimeType(String folder, String fileName) {
        try {
            Path path = Paths.get(uploadDir + folder + "/" + fileName);
            return Files.probeContentType(path);
        } catch (Exception e) {
            log.error("Cannot get mime type", e);
        }
        return null;
    }
}
