package org.leiers.cloudflare;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CloudflareClient {
    private final String token;
    private final String baseUrl;

    public CloudflareClient(String token, String zoneId) {
        this.token = token;
        this.baseUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId;;
    }

    public void createCName(String subdomain, String content) {
        createRecord("CNAME", subdomain, content);
    }

    public List<DnsRecord> getAllRecords() {
        try {
            final HttpClient httpClient = HttpClient.newHttpClient();
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/dns_records"))
                    .header("Authorization", "Bearer " + token)
                    .build();

            final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return  DnsRecord.parseFromApiResponse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public Optional<DnsRecord> getRecord(String name) {
        return getAllRecords()
                .stream()
                .filter(record -> record.getName()
                        .equalsIgnoreCase(name + "." + record.getContent()))
                .findFirst();
    }

    public void removeRecord(String name) {
        final Optional<DnsRecord> optionalDnsRecord = getRecord(name);

        if (optionalDnsRecord.isEmpty())
            return;

        final String dnsId = optionalDnsRecord.get().getId();

        try {
            final HttpClient httpClient = HttpClient.newHttpClient();
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/dns_records/" + dnsId))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createRecord(String type, String name, String content) {
        final boolean proxied = true;
        final int ttl = 3600;

        try {
            final String jsonData = String.format("{\"type\":\"%s\",\"name\":\"%s\",\"content\":\"%s\",\"proxied\":%s,\"ttl\":%d}",
                    type, name, content, proxied, ttl);

            final HttpClient httpClient = HttpClient.newHttpClient();
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/dns_records"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                    .build();

            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
