package org.example.apidemo.weather.dto;

public class WeatherStateDTO {
    private String date;
    private String time;
    private String precipitation;
    private String temperature;

    public void WeatherDTO(String date, String time, String precipitation, String temperature) {
        this.date = date;
        this.time = time;
        this.precipitation = precipitation;
        this.temperature = temperature;
    }

    // Getter and Setter
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
