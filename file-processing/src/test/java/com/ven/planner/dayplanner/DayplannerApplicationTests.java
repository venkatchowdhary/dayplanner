package com.ven.planner.dayplanner;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static java.lang.String.join;

@Slf4j
@SpringBootTest
class DayplannerApplicationTests {

    private static final String BASE_URL = "http://localhost:8080";

    @Value("${download.dir}")
    private String downloadDir;

    @Value("${resource.dir}")
    private String resourceDir;

    @Autowired
    RestTemplate restTemplate;

    @Test
    void testUpload() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(join("/", resourceDir, "ven.jpg")));
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(body, headers);

        try {
            restTemplate.postForEntity(join("/", BASE_URL, "upload"), httpEntity, Boolean.class);
        } catch (Exception e) {
            log.info("Exception while uploading file", e);
        }
    }

    @Test
    void testMultipleUpload() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(join("/", resourceDir, "ven.jpg")));
        body.add("file", new FileSystemResource(join("/", resourceDir, "SSC.jpg")));
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(body, headers);

        try {
            restTemplate.postForEntity(join("/", BASE_URL, "multiple-upload"),
                    httpEntity,
                    Boolean.class);
        } catch (Exception e) {
            log.info("Exception while uploading file", e);
        }
    }


    @Test
    void testDownload() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity entity = new HttpEntity(headers);

        String fileName = "ven.jpg";
        ResponseEntity<byte[]> response = restTemplate.exchange(join("/", BASE_URL, "download", fileName),
                HttpMethod.GET,
                entity,
                byte[].class);

        Files.write(Paths.get(join("/",downloadDir, fileName)), response.getBody());
    }
}