package org.example.apidemo.weather.repository;

import org.example.apidemo.weather.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WeatherStateRepository extends JpaRepository<Weather, Long> {
    Weather findTopByOrderByIdDesc();
}

