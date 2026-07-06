package dev.kosmos.kos.project;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Map;

@Component
public class ProjectHealthClient {

    private final RestClient restClient;

    public ProjectHealthClient() {
        this.restClient = RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().set("Accept", "application/json");
                    return execution.execute(request, body);
                })
                .build();
    }

    public Map<String, Object> probe(String serviceEndpoint) {
        long start = System.currentTimeMillis();
        try {
            var body = restClient.get()
                    .uri(serviceEndpoint + "/api/status")
                    .retrieve()
                    .body(Map.class);
            long latency = System.currentTimeMillis() - start;
            return Map.of(
                    "status", "ok",
                    "latencyMs", latency,
                    "detail", body != null ? body : Map.of()
            );
        } catch (RestClientException e) {
            long latency = System.currentTimeMillis() - start;
            return Map.of(
                    "status", "error",
                    "latencyMs", latency,
                    "detail", e.getMessage() != null ? e.getMessage() : "unreachable"
            );
        }
    }
}
