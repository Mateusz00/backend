package io.github.mateusz00.configuration;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import io.github.mateusz00.service.s3.S3UrlProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Configuration
@Slf4j
public class AwsS3Configuration
{
    public static final String S3_BUCKET = "media";
    @Value("${local.s3.host:#{null}}")
    private String localS3Host;
    @Value("${local.s3.port:#{null}}")
    private String localS3Port;

    @Bean
    @Profile("local")
    @SneakyThrows
    S3Client s3ClientLocal() throws URISyntaxException
    {
        var s3 = S3Client.builder() // NOSONAR
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .endpointOverride(new URI("http://" + localS3Host + ":" + localS3Port))
                .region(Region.EU_CENTRAL_1)
                .build();
        try
        {
            log.info("Attempting to create s3 bucket: " + S3_BUCKET);
            s3.createBucket(CreateBucketRequest.builder() // Make sure bucket is created
                    .bucket(S3_BUCKET)
                    .build());
            log.info("Created s3 bucket: " + S3_BUCKET);
        }
        catch (Exception e) { /* IGNORE */}
        return s3;
    }

    @Bean
    @Profile("local")
    @Primary
    S3UrlProvider s3LocalUrlProvider(S3Client s3Client)
    {
        return new S3UrlProvider(s3Client) {
            @SneakyThrows
            @Override
            public String getUrl(String s3Bucket, String key)
            {
                var endpoint = new URI("http://localhost:8081"); // NOSONAR
                return s3Client.utilities()
                        .getUrl(builder -> builder
                                .endpoint(endpoint)
                                .bucket(s3Bucket)
                                .key(key))
                        .toString();
            }
        };
    }
}
