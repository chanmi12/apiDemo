package org.example.apidemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.apidemo.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Weather API", description = "API for retrieving weather information")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Operation(summary = "Get today's weather", description = "Fetches the weather forecast for today based on the provided coordinates (x, y).")
    @GetMapping("/weather/today")
    public List<String> getTodayWeather(
            @Parameter(description = "The x coordinate for the location", required = true)
            @RequestParam("x") String nx,
            @Parameter(description = "The y coordinate for the location", required = true)
            @RequestParam("y") String ny
    ) {
        return weatherService.getTodayWeather(nx, ny);
    }
}
