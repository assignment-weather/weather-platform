package com.weather.weather.controller;


import com.weather.weather.model.response.WeatherForecastResponse;
import com.weather.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@AllArgsConstructor
@Validated
@Tag(name = "Weather Forecast API", description = "Provides 3-day weather forecasts for a specified city.")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(
            summary = "Get 3-day weather forecast",
            description = "Fetches high/low temperatures and advisory messages for the next 3 days."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the forecast"),
            @ApiResponse(responseCode = "400", description = "Bad Request - City parameter is missing"),
    })
    @GetMapping
    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(
            @Parameter(description = "Name of the city.", required = true, example = "london")
            @RequestParam @NotBlank String city) {

        return ResponseEntity.ok(weatherService.getForecast(city));
    }
}
