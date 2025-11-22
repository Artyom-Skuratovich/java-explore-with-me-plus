package ru.practicum.stats.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.client.exceptions.StatsClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(String serverUrl, RestTemplateBuilder builder) {
        this.serverUrl = trimTrailingSlash(serverUrl);
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(this.serverUrl))
                .build();

        ((DefaultUriBuilderFactory) this.restTemplate.getUriTemplateHandler())
                .setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
    }

    public StatsClient(String serverUrl) {
        this.serverUrl = trimTrailingSlash(serverUrl);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(this.serverUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(factory);
    }

    public StatsClient(String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = trimTrailingSlash(serverUrl);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(this.serverUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        this.restTemplate = restTemplate;
        this.restTemplate.setUriTemplateHandler(factory);
    }

    public void hit(EndpointHit dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EndpointHit> request = new HttpEntity<>(dto, headers);

            restTemplate.exchange(
                    serverUrl + "/hit",
                    HttpMethod.POST,
                    request,
                    Void.class
            );

        } catch (RestClientResponseException e) {
            throw new StatsClientException(
                    e.getRawStatusCode(),
                    e.getResponseBodyAsString(),
                    "Failed to send hit",
                    e
            );
        }
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        try {
            String encStart = encode(start);
            String encEnd = encode(end);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(serverUrl + "/stats")
                    .queryParam("start", encStart)
                    .queryParam("end", encEnd)
                    .queryParam("unique", unique);

            if (uris != null && !uris.isEmpty()) {
                for (String uri : uris) {
                    builder.queryParam("uris", uri);
                }
            }

            String url = builder.build(true).toUriString();

            ResponseEntity<ViewStats[]> response =
                    restTemplate.getForEntity(url, ViewStats[].class);

            ViewStats[] body = response.getBody();
            return body == null ? Collections.emptyList() : Arrays.asList(body);

        } catch (RestClientResponseException e) {
            throw new StatsClientException(
                    e.getRawStatusCode(),
                    e.getResponseBodyAsString(),
                    "Failed to get stats",
                    e
            );
        }
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris) {
        return getStats(start, end, uris, false);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
