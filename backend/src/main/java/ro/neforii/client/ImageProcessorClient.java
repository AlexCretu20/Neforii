package ro.neforii.client;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.net.URI;
import java.util.Collections;

@Component
public class ImageProcessorClient { // apeleaza c# service pentru a aplica filtre pe imagini

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ImageProcessorClient(
            RestTemplate restTemplate,
            @Value("${image.processor.base-url:http://3.121.100.69/api/image}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public byte[] applyFilter(MultipartFile image, int filterId) throws IOException {
        final String filename = filenameOrDefault(image);
        final String partContentType = safeContentType(image.getContentType());

        ByteArrayResource fileResource = new ByteArrayResource(image.getBytes()) {
            @Override public String getFilename() { return filename; }
        };

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.parseMediaType(partContentType));
        partHeaders.setContentDispositionFormData("image", filename);

        HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(fileResource, partHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", filePart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/applyFilter")
                .queryParam("filterId", filterId)
                .build(true)
                .toUri();

        ResponseEntity<byte[]> resp = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, byte[].class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("Filter service error: HTTP " + resp.getStatusCodeValue());
        }
        return resp.getBody();
    }

    private String filenameOrDefault(MultipartFile f) {
        String n = f.getOriginalFilename();
        return (n == null || n.isBlank()) ? "upload" : n;
    }

    private String safeContentType(String ct) {
        return (ct == null || ct.isBlank()) ? MediaType.APPLICATION_OCTET_STREAM_VALUE : ct;
    }
}