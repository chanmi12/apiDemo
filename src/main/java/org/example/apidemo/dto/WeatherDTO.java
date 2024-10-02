package org.example.apidemo.dto;

public class WeatherDTO {
    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;
    private String temperature;
    private String humidity;
    private String precipitation;

    public String getBaseDate() {
        return baseDate;
    }
    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }
    public String getBaseTime() {
        return baseTime;
    }
    public void setBaseTime(String baseTime) {
        this.baseTime = baseTime;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getFcstDate() {
        return fcstDate;
    }
    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }
    public String getFcstTime() {
        return fcstTime;
    }
    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }
    public String getFcstValue() {
        return fcstValue;
    }
    public void setFcstValue(String fcstValue) {
        this.fcstValue = fcstValue;
    }
    public String getTemperature() {
        return temperature;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
    public String getHumidity() {
        return humidity;
    }
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
    public String getPrecipitation() {
        return precipitation;
    }
    public void setPrecipitation(String precipitation) {  // 강수 형태 setter 추가
        this.precipitation = precipitation;
    }
}
