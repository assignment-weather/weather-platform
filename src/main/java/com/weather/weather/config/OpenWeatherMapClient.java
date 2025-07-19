package com.weather.weather.config;

import com.weather.weather.exceptions.CityNotFoundException;
import com.weather.weather.model.response.OpenWeatherApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
@Slf4j
@EnableConfigurationProperties(OpenWeatherMapProperties.class)
public class OpenWeatherMapClient {
    private final RestClient restClient;
    private final OpenWeatherMapProperties properties;

    public OpenWeatherMapClient(OpenWeatherMapProperties properties) {
        this.properties = properties;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());

        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public OpenWeatherApiResponse fetchWeather(String city) {
        log.info("Fetching weather data for city '{}'", city);

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", city)
                        .queryParam("appid", this.properties.getApiKey())
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new CityNotFoundException("The city '" + city + "' could not be found.");
                    }
                    String responseBody = "";
                    try {
                        responseBody = new String(response.getBody().readAllBytes());
                    } catch (IOException e) {
                        log.error("Could not read error response body", e);
                    }
                    log.error("Client error from OpenWeatherMap API: {} - {}", response.getStatusCode(), responseBody);
                    throw new HttpClientErrorException(response.getStatusCode(), "Client error from weather service: " + responseBody);
                })
                .body(OpenWeatherApiResponse.class);
    }
}
