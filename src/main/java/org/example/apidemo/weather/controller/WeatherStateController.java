package org.example.apidemo.weather.controller;

import org.example.apidemo.weather.service.MapService;
import org.example.apidemo.weather.service.WeatherStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class WeatherStateController {

    @Autowired
    private WeatherStateService weatherService;

    @Autowired
    private MapService mapService;

    @GetMapping("/weather/display")
    public String displayWeatherByFixedAddress(Model model) {
        String address = "포항"; // 고정된 주소

        // 주소를 좌표로 변환
        Map<String, String> xy = mapService.getMapAddress(address);
        int x = (int) Math.round(Double.parseDouble(xy.get("x")));
        int y = (int) Math.round(Double.parseDouble(xy.get("y")));

        // 좌표를 기반으로 날씨 데이터를 가져옴
        List<String> dailyWeatherData = weatherService.getDailyWeather(y, x);

        // 모델에 날씨 데이터 및 주소 추가
        model.addAttribute("weatherData", dailyWeatherData);
        model.addAttribute("address", address);

        return "header"; // "header.html" 템플릿 반환
    }
}

