package io.github.mateusz00.configuration;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Configuration
public class AwsS3Configuration
{
    public static final String S3_BUCKET = "media";

    @Bean
    @Profile("local")
    S3Client s3ClientLocal() throws URISyntaxException
    {
        var s3 = S3Client.builder() // NOSONAR
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .endpointOverride(new URI("http://localhost:8081")) // NOSONAR
                .region(Region.EU_CENTRAL_1)
                .build();
        try
        {
            s3.createBucket(CreateBucketRequest.builder() // Make sure bucket is created
                    .bucket(S3_BUCKET)
                    .build());
        }
        catch (Exception e) { /* IGNORE */}
        return s3;
    }
}
