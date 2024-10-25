package org.example.apidemo.weather.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WeatherStateService {

    // 하루 시간대별 날씨 데이터를 가져오는 메소드
    public static List<String> getDailyWeather(int x, int y) {
        Map<String, String[]> weatherDataMap = new TreeMap<>();

        // 여러 시간대의 데이터를 요청할 base_time 목록
        String[] baseTimes = {"0000", "0600", "1200", "1800"};

        for (String baseTime : baseTimes) {
            fetchWeatherDataForBaseTime(x, y, baseTime, weatherDataMap);
        }

        // 현재 시간에 가장 가까운 데이터 선택
        List<String> formattedWeatherData = new ArrayList<>();
        String nearestTime = findNearestTime(weatherDataMap.keySet(), weatherDataMap);

        if (nearestTime != null) {
            String[] currentWeatherData = weatherDataMap.get(nearestTime);

            String formattedData = String.format("날짜: %s   시간: %s  날씨: %s  기온: %s°C",
                    currentWeatherData[0],
                    currentWeatherData[1],
                    currentWeatherData[2] != null ? currentWeatherData[2] : "데이터 없음",
                    currentWeatherData[3] != null ? currentWeatherData[3] : "데이터 없음");
            formattedWeatherData.add(formattedData);
        } else {
            formattedWeatherData.add("가까운 시간에 대한 날씨 데이터가 없습니다.");
        }

        return formattedWeatherData; // 포맷팅된 데이터 반환
    }

    // 가장 가까운 시간대를 찾는 메소드
    private static String findNearestTime(Iterable<String> timeSet, Map<String, String[]> weatherDataMap) {
        LocalTime now = LocalTime.now();
        String nearestTime = null;
        int minDifference = Integer.MAX_VALUE;

        for (String time : timeSet) {
            LocalTime dataTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
            int difference = Math.abs(now.toSecondOfDay() - dataTime.toSecondOfDay());

            if (difference < minDifference) {
                minDifference = difference;
                nearestTime = time;
            }
        }

        // 가장 가까운 시간대 데이터가 없을 경우, 이전 또는 다음 시간대 찾기
        if (nearestTime == null && !timeSet.iterator().hasNext()) {
            // 데이터가 아예 없는 경우
            return null;
        }

        for (String time : timeSet) {
            if (weatherDataMap.get(time) == null) {
                // 이전 시간대 탐색
                LocalTime dataTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
                if (dataTime.isBefore(now)) {
                    nearestTime = time; // 이전 시간대가 가장 가까움
                    break;
                }
            }
        }

        // 만약 찾지 못했으면 다음 시간대 탐색
        if (nearestTime == null) {
            for (String time : timeSet) {
                LocalTime dataTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
                if (dataTime.isAfter(now)) {
                    nearestTime = time; // 다음 시간대가 가장 가까움
                    break;
                }
            }
        }

        return nearestTime;
    }

    // 특정 base_time에 대한 날씨 데이터를 가져오는 메소드
    private static void fetchWeatherDataForBaseTime(int x, int y, String baseTime, Map<String, String[]> weatherDataMap) {
        HttpURLConnection con = null;

        try {
            // 현재 날짜의 데이터를 요청
            LocalDate today = LocalDate.now();

            URL url = new URL(
                    "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"
                            + "?ServiceKey=" + "69YYtu1XspY1rFpEKlo8VJ5mQhO8ab%2BldbDFTsuz7QuxRAucG6e%2BpiDonS0dCWh%2B7V8Mw7cOTXHaFC%2Fs5%2BOuzQ%3D%3D"
                            + "&pageNo=1"
                            + "&numOfRows=1000"
                            + "&base_date=" + today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                            + "&base_time=" + baseTime
                            + "&nx=" + x
                            + "&ny=" + y
            );

            // API 호출 및 결과 파싱
            con = (HttpURLConnection) url.openConnection();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(con.getInputStream());

            boolean ok = false; // <resultCode>00</resultCode> 성공 여부

            Element e;
            NodeList ns = doc.getElementsByTagName("header");
            if (ns.getLength() > 0) {
                e = (Element) ns.item(0);
                if ("00".equals(e.getElementsByTagName("resultCode").item(0).getTextContent())) {
                    ok = true; // 성공 여부
                }
            }

            if (ok) {
                String fd, ft; // 예보 날짜와 시간
                String pty = null; // 강수형태
                String cat; // category
                String val; // 예보 값

                ns = doc.getElementsByTagName("item");
                for (int i = 0; i < ns.getLength(); i++) {
                    e = (Element) ns.item(i);

                    fd = e.getElementsByTagName("fcstDate").item(0).getTextContent(); // 예보 날짜
                    ft = e.getElementsByTagName("fcstTime").item(0).getTextContent(); // 예보 시각

                    String[] weatherData = weatherDataMap.getOrDefault(ft, new String[]{fd, ft, null, null, null});

                    // 각 카테고리별 값 처리
                    cat = e.getElementsByTagName("category").item(0).getTextContent();
                    val = e.getElementsByTagName("fcstValue").item(0).getTextContent();

                    if ("PTY".equals(cat)) weatherData[2] = parsePty(val); // 강수형태
                    else if ("T1H".equals(cat)) weatherData[3] = val; // 기온
                    else if ("REH".equals(cat)) weatherData[4] = val; // 습도

                    weatherDataMap.put(ft, weatherData); // 시간대별 데이터 저장
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) con.disconnect(); // 연결 해제
        }
    }

    // 강수형태를 해석하는 메소드
    private static String parsePty(String pty) {
        switch (pty) {
            case "0": return "맑음";
            case "1": return "비";
            case "2": return "비/눈";
            case "3": return "눈";
            case "5": return "빗방울";
            case "6": return "빗방울눈날림";
            case "7": return "눈날림";
            default: return null;
        }
    }
}

