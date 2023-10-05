package io.github.mateusz00.service.s3;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@RequiredArgsConstructor
public class S3UrlProvider
{
    private final S3Client client;

    public String getUrl(String s3Bucket, String key)
    {
        return client.utilities()
                .getUrl(builder -> builder.bucket(s3Bucket).key(key))
                .toString();
    }
}
