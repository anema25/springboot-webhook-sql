package com.example.hiring.client;

import com.example.hiring.model.GenerateWebhookRequest;
import com.example.hiring.model.GenerateWebhookResponse;
import com.example.hiring.model.SubmitSolutionRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebhookClient {
  private final WebClient webClient;
  private final String baseUrl;
  private final String generatePath;
  private final String submitFallbackPath;

  public WebhookClient(WebClient.Builder builder, org.springframework.core.env.Environment env) {
    this.baseUrl = env.getProperty("api.baseUrl");
    this.generatePath = env.getProperty("api.generatePath");
    this.submitFallbackPath = env.getProperty("api.submitFallbackPath");
    this.webClient = builder.baseUrl(baseUrl).build();
  }

  public Mono<GenerateWebhookResponse> generateWebhook(GenerateWebhookRequest req) {
    return webClient.post()
        .uri(generatePath)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(req)
        .retrieve()
        .bodyToMono(GenerateWebhookResponse.class);
  }

  public Mono<String> submitSolution(String webhookUrlOrPath, String jwt, SubmitSolutionRequest body) {
    boolean absolute = webhookUrlOrPath != null && webhookUrlOrPath.startsWith("http");
    WebClient.RequestBodySpec spec = (absolute ? WebClient.create() : webClient).post()
        .uri(absolute ? webhookUrlOrPath : webhookUrlOrPath);

    return spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class);
  }

  public String getSubmitFallbackPath() {
    return submitFallbackPath;
  }
}
