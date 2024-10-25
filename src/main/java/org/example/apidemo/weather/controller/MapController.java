package org.example.apidemo.weather.controller;

import org.example.apidemo.weather.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MapController {

    @Autowired
    private MapService mapService;


    @GetMapping("/map")
    public ResponseEntity<Map<String,String>> getDailyWeather(@RequestParam String address) {
        Map<String,String> xy = mapService.getMapAddress(address);

        return ResponseEntity.ok(xy);
    }
}