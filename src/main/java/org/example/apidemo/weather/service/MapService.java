package org.example.apidemo.weather.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class MapService {

    private static final String KAKAO_API_KEY = "35b5aae5d5d0992430ed3ca77bb369bf"; // 여기에 카카오 API 키를 입력하세요.

    public Map<String, String> getMapAddress(String address) {
        Map<String, String> coordinates = new HashMap<>();
        try {
            // 주소를 URL 인코딩
            String encodedAddress = URLEncoder.encode(address, "UTF-8");

            // Kakao API URL 설정
            String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json?query=" + encodedAddress;

            // HTTP GET 요청 설정
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "KakaoAK " + KAKAO_API_KEY);

            // 응답 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            // 응답 JSON 파싱
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getJSONArray("documents").length() > 0) {
                JSONObject firstDoc = jsonResponse.getJSONArray("documents").getJSONObject(0);

                // x, y 좌표 추출
                String x = firstDoc.getString("x");
                String y = firstDoc.getString("y");

                coordinates.put("x", x);
                coordinates.put("y", y);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return coordinates; // x, y 좌표를 반환
    }
}
