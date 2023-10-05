package io.github.mateusz00.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.mateusz00.api.ResourcesApi;
import io.github.mateusz00.api.model.FileDownloadResult;
import io.github.mateusz00.service.ResourcesService;
import io.github.mateusz00.service.UserProvider;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ResourcesApiController implements ResourcesApi
{
    private final ResourcesService resourcesService;
    private final UserProvider userProvider;

    @Override
    public ResponseEntity<FileDownloadResult> uploadResource(MultipartFile file)
    {
        String url = resourcesService.uploadResource(file, userProvider.getUser());
        return ResponseEntity.ok(new FileDownloadResult().url(url));
    }
}
