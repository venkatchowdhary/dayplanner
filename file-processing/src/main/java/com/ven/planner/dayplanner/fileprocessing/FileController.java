package com.ven.planner.dayplanner.fileprocessing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static java.lang.String.join;


@Slf4j
@RestController
public class FileController {


    @Value("${upload.dir}")
    private String UPLOAD_DIR;

    @PostMapping("/upload")
    public boolean upload(@RequestParam("file") MultipartFile file) throws IOException {
        file.transferTo(new File(join( File.separator, UPLOAD_DIR, file.getOriginalFilename())));
        return true;
    }

    @PostMapping("/multiple-upload")
    public boolean upload(@RequestParam("file") MultipartFile[] files) throws IOException {
        Arrays.stream(files).forEach(file -> {
            try {
                file.transferTo(new File(join( File.separator, UPLOAD_DIR, file.getOriginalFilename())));
            } catch (IOException e) {
              log.info("Unable to upload file: {}", file.getOriginalFilename());
            }
        });
        return true;
    }


    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable("filename") String filename) throws IOException {
        byte[] fileData = Files.readAllBytes(new File(String.join(File.separator, UPLOAD_DIR, filename)).toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(fileData, headers, HttpStatus.OK);
    }
}
