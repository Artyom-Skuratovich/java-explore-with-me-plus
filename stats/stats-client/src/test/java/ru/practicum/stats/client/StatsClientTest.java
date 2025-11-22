package ru.practicum.stats.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.ViewStats;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class StatsClientTest {

    @Test
    void hit_sendsPost() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        StatsClient testClient = new StatsClient("http://localhost:9090", restTemplate);

        server.expect(once(), requestTo("http://localhost:9090/hit"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess());

        testClient.hit(null);

        server.verify();
    }

    @Test
    void getStats_parsesResponse() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        StatsClient testClient = new StatsClient("http://localhost:9090", restTemplate);

        String json = "[{\"app\":\"app\",\"uri\":\"/uri\",\"hits\":5}]";

        server.expect(once(), requestTo(
                        "http://localhost:9090/stats?start=2025-01-01+00%3A00%3A00&end=2025-01-02+00%3A00%3A00&unique=false"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<ViewStats> stats =
                testClient.getStats("2025-01-01 00:00:00", "2025-01-02 00:00:00", null, false);

        assertEquals(1, stats.size());
        assertEquals("app", stats.get(0).getApp());
        assertEquals("/uri", stats.get(0).getUri());
        assertEquals(5L, stats.get(0).getHits());

        server.verify();
    }
}
