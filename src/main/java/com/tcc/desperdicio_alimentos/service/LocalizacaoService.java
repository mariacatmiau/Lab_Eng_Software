package com.tcc.desperdicio_alimentos.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalizacaoService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final RestClient restClient;
    private final Map<String, Optional<Coordenada>> cacheCoordenadas = new ConcurrentHashMap<>();

    public LocalizacaoService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(HttpHeaders.USER_AGENT, "DoaDoa/1.0 (market-distance)")
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "pt-BR")
                .build();
    }

    public Optional<Coordenada> buscarCoordenadas(String endereco) {
        String consulta = endereco == null ? "" : endereco.trim();
        if (consulta.isBlank()) {
            return Optional.empty();
        }

        String chave = consulta.toUpperCase(Locale.ROOT);
        return cacheCoordenadas.computeIfAbsent(chave, ignored -> consultarServico(consulta));
    }

    public Double calcularDistanciaKm(double latitudeOrigem, double longitudeOrigem, double latitudeDestino, double longitudeDestino) {
        double lat1 = Math.toRadians(latitudeOrigem);
        double lon1 = Math.toRadians(longitudeOrigem);
        double lat2 = Math.toRadians(latitudeDestino);
        double lon2 = Math.toRadians(longitudeDestino);

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS_KM * c * 10.0) / 10.0;
    }

    private Optional<Coordenada> consultarServico(String endereco) {
        try {
            JsonNode[] resposta = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", endereco)
                            .queryParam("format", "jsonv2")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(JsonNode[].class);

            if (resposta == null || resposta.length == 0) {
                return Optional.empty();
            }

            JsonNode primeiro = resposta[0];
            double latitude = Double.parseDouble(primeiro.path("lat").asText());
            double longitude = Double.parseDouble(primeiro.path("lon").asText());
            return Optional.of(new Coordenada(latitude, longitude));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public record Coordenada(double latitude, double longitude) {
    }
}