package com.example.hiring.service;

import com.example.hiring.client.WebhookClient;
import com.example.hiring.model.GenerateWebhookRequest;
import com.example.hiring.model.GenerateWebhookResponse;
import com.example.hiring.model.SubmitSolutionRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
  private final WebhookClient client;
  private final SqlSolverService solver;
  private final String name;
  private final String regNo;
  private final String email;

  public StartupRunner(WebhookClient client, SqlSolverService solver, Environment env) {
    this.client = client;
    this.solver = solver;
    this.name = env.getProperty("app.candidate.name");
    this.regNo = env.getProperty("app.candidate.regNo");
    this.email = env.getProperty("app.candidate.email");
  }

  @Override
  public void run(String... args) {
    GenerateWebhookRequest req = new GenerateWebhookRequest(name, regNo, email);
    GenerateWebhookResponse resp = client.generateWebhook(req).block();
    if (resp == null) throw new RuntimeException("No response from generateWebhook");

    String webhook = (resp.getWebhook() != null && !resp.getWebhook().isBlank())
        ? resp.getWebhook()
        : client.getSubmitFallbackPath();

    String sql = solver.resolveFinalSql(regNo);
    if (sql == null || sql.isBlank()) throw new RuntimeException("SQL not found");

    SubmitSolutionRequest submit = new SubmitSolutionRequest(sql);
    String result = client.submitSolution(webhook, resp.getAccessToken(), submit).block();
    System.out.println("Submission result: " + result);
  }
}
