package io.github.mateusz00.service;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.mateusz00.configuration.UserRole;
import io.github.mateusz00.dto.UserInfo;
import io.github.mateusz00.exception.BadRequestException;
import io.github.mateusz00.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ResourcesService
{
    private static final Tika TIKA = new Tika();
    private static final Set<String> ALLOWED_TYPES = Set.of("application/ogg", "image/webp", "image/jpeg", "audio/mpeg", "image/png");
    private static final long USER_MAX_FILE_SIZE = 1024L * 1024L;
    private final S3Service s3Service;

    public String uploadResource(MultipartFile file, UserInfo user)
    {
        if (file.getSize() > USER_MAX_FILE_SIZE && user.lacksRole(UserRole.ROLE_ADMIN))
        {
            throw new BadRequestException("File size exceeded limit of " + USER_MAX_FILE_SIZE + " bytes");
        }
        try
        {
            ensureIsAllowedType(file);
            return s3Service.uploadMediaResource(user.userId() + "/" + UUID.randomUUID(), file.getBytes());
        }
        catch (IOException e)
        {
            throw new BadRequestException("Error when reading resource");
        }
    }

    private void ensureIsAllowedType(MultipartFile file) throws IOException
    {
        String type = TIKA.detect(file.getBytes());
        if (!ALLOWED_TYPES.contains(type))
        {
            throw new BadRequestException("File type not allowed: " + type);
        }
    }
}
