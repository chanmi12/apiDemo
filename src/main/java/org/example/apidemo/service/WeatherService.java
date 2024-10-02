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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherService {
    private final String apiKey = "3Bg1H7pYosMLj7URD00rtt4W3S4FMhnmE4z2n4JU6NHNuZ6BMjL8laFqxOEHx%2BsbN1HvYmf5NrWNZm17xmW9BA%3D%3D";
    private final String[] baseTimes = {"0000", "0600", "1200", "1500"};

    public String getNearestBaseTime() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");

        return baseTimes[0];
    }

    public List<String> getTodayWeather(String nx, String ny) {
        List<WeatherDTO> weatherData = new ArrayList<>();
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = getNearestBaseTime();

        try {
            String urlString = String.format(
                    "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst" +
                            "?ServiceKey=%s&pageNo=1&numOfRows=1000&dataType=JSON&base_date=%s&base_time=%s&nx=%s&ny=%s",
                    apiKey, baseDate, baseTime, nx, ny);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                return parseWeatherData(response.toString());

            } else {
                System.out.println("GET 요청 실패: 응답 코드 " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<String> parseWeatherData(String jsonResponseString) {
        List<WeatherDTO> weatherData = new ArrayList<>();
        Map<String, WeatherDTO> weatherMap = new HashMap<>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonResponseString);
            JSONArray itemArray = jsonResponse.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);
                String category = item.getString("category");

                if (!category.equals("T1H") && !category.equals("REH") && !category.equals("PTY")) {
                    continue;
                }

                String key = item.getString("fcstDate") + "_" + item.getString("fcstTime");
                WeatherDTO weather = weatherMap.getOrDefault(key, new WeatherDTO());
                weather.setFcstDate(item.getString("fcstDate"));
                weather.setFcstTime(item.getString("fcstTime"));

                switch (category) {
                    case "T1H":
                        weather.setTemperature(item.getString("fcstValue") + "°C");
                        break;
                    case "REH":
                        weather.setHumidity(item.getString("fcstValue") + "%");
                        break;
                    case "PTY":
                        weather.setPrecipitation(parsePty(item.getString("fcstValue")));
                        break;
                }

                weatherMap.put(key, weather);
            }

            weatherData.addAll(weatherMap.values());
            weatherData.sort(Comparator.comparing(WeatherDTO::getFcstTime));

            return formatWeatherData(weatherData);

        } catch (JSONException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<String> formatWeatherData(List<WeatherDTO> weatherData) {
        List<String> formattedWeatherData = new ArrayList<>();
        for (WeatherDTO weather : weatherData) {
            String formattedData = String.format("날짜: %s   시간: %s  날씨: %s  기온: %s  습도: %s",
                    weather.getFcstDate() != null ? weather.getFcstDate() : "데이터 없음",
                    weather.getFcstTime() != null ? weather.getFcstTime() : "데이터 없음",
                    weather.getPrecipitation() != null ? weather.getPrecipitation() : "데이터 없음",
                    weather.getTemperature() != null ? weather.getTemperature() : "데이터 없음",
                    weather.getHumidity() != null ? weather.getHumidity() : "데이터 없음");
            formattedWeatherData.add(formattedData);
        }
        return formattedWeatherData;
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
