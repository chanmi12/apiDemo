package org.example.apidemo.service;

import org.example.apidemo.dto.WeatherDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final String apiKey = "3Bg1H7pYosMLj7URD00rtt4W3S4FMhnmE4z2n4JU6NHNuZ6BMjL8laFqxOEHx%2BsbN1HvYmf5NrWNZm17xmW9BA%3D%3D"; // 유효한 API 키

    public List<String> getTodayWeather(String baseTime, String nx, String ny) {
        List<WeatherDTO> weatherData = new ArrayList<>();
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 오늘 날짜

        try {
            String urlString = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"
                    + "?ServiceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=1000"
                    + "&dataType=JSON"
                    + "&base_date=" + baseDate
                    + "&base_time=" + baseTime
                    + "&nx=" + nx
                    + "&ny=" + ny;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    String jsonResponseString = response.toString();
                    JSONObject jsonResponse = new JSONObject(jsonResponseString);
                    JSONObject items = jsonResponse.getJSONObject("response")
                            .getJSONObject("body")
                            .getJSONObject("items");

                    JSONArray itemArray = items.getJSONArray("item");

                    Map<String, WeatherDTO> weatherMap = new HashMap<>();

                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject item = itemArray.getJSONObject(i);
                        WeatherDTO weather = new WeatherDTO();
                        weather.setBaseDate(item.getString("baseDate"));
                        weather.setBaseTime(item.getString("baseTime"));
                        weather.setCategory(item.getString("category"));
                        weather.setFcstDate(item.getString("fcstDate"));
                        weather.setFcstTime(item.getString("fcstTime"));
                        weather.setFcstValue(item.getString("fcstValue"));

                        if ("T1H".equals(weather.getCategory())) { // 기온 코드
                            weather.setTemperature(weather.getFcstValue() + "°C");
                        } else if ("REH".equals(weather.getCategory())) { // 습도 코드
                            weather.setHumidity(weather.getFcstValue() + "%");
                        }

                        String key = weather.getFcstDate() + "_" + weather.getFcstTime();
                        if (!weatherMap.containsKey(key)) {
                            weatherMap.put(key, weather);
                        } else {
                            WeatherDTO existingWeather = weatherMap.get(key);
                            if (weather.getTemperature() != null && !weather.getTemperature().equals("데이터 없음")) {
                                existingWeather.setTemperature(weather.getTemperature());
                            }
                            if (weather.getHumidity() != null && !weather.getHumidity().equals("데이터 없음")) {
                                existingWeather.setHumidity(weather.getHumidity());
                            }
                        }
                    }

                    weatherData.addAll(weatherMap.values());

                    weatherData.sort(Comparator.comparing(WeatherDTO::getFcstTime));

                    List<String> formattedWeatherData = new ArrayList<>();
                    for (WeatherDTO weather : weatherData) {
                        String formattedData = String.format("날짜: %s   시간: %s  날씨: %s  기온: %s  습도: %s",
                                weather.getFcstDate(),
                                weather.getFcstTime(),
                                weather.getCategory() != null ? parsePty(weather.getCategory()) : "데이터 없음",
                                weather.getTemperature() != null ? weather.getTemperature() : "데이터 없음",
                                weather.getHumidity() != null ? weather.getHumidity() : "데이터 없음");
                        formattedWeatherData.add(formattedData);
                    }

                    return formattedWeatherData; // 포맷팅된 데이터 반환

                } catch (JSONException e) {
                    System.err.println("JSON parsing error: " + e.getMessage());
                }
            } else {
                System.out.println("GET request failed: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static String parsePty(String pty) {
        switch (pty) {
            case "0": return "맑음";
            case "1": return "비";
            case "2": return "비/눈";
            case "3": return "눈";
            case "5": return "빗방울";
            case "6": return "빗방울눈날림";
            case "7": return "눈날림";
            default: return "데이터 없음";
        }
    }
}
