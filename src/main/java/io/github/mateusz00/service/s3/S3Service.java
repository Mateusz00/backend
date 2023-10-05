package io.github.mateusz00.service.s3;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Service;

import io.github.mateusz00.exception.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static io.github.mateusz00.configuration.AwsS3Configuration.S3_BUCKET;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3Service
{
    private final S3Client client;
    private final S3UrlProvider s3UrlProvider;

    public String uploadMediaResource(String key, byte[] content)
    {
        var request = PutObjectRequest.builder()
                .bucket(S3_BUCKET)
                .key(key)
                .build();
        try
        {
            client.putObject(request, RequestBody.fromByteBuffer(ByteBuffer.wrap(content)));
            return s3UrlProvider.getUrl(S3_BUCKET, key);
        }
        catch (SdkException e)
        {
            throw new InternalException("Failed upload to S3", e);
        }
    }
}
