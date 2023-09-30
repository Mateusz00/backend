package io.github.mateusz00.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.mateusz00.api.ResourcesApi;
import io.github.mateusz00.api.model.FileDownloadResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ResourcesApiController implements ResourcesApi
{
    @Override
    public ResponseEntity<FileDownloadResult> uploadResource(String type, MultipartFile file)
    {
        return null; // TODO
    }
}
