package org.example.apidemo.calendar.controller;

import org.example.apidemo.calendar.dto.ReportDTO;
import org.example.apidemo.calendar.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    // HTML 페이지를 반환하는 엔드포인트
    @GetMapping("/report")
    public String showReportPage(Model model) {
        return "calendar";  // report.html 페이지로 이동
    }

    // JSON 데이터를 반환하는 엔드포인트
    @RestController
    public static class ReportApiController {

        @Autowired
        private ReportService reportService;

        // 특정 월의 리포트를 가져오는 API
        @GetMapping("/api/report")
        public List<ReportDTO> getReportsForMonth(@RequestParam(required = false) String yearMonth) {
            if (yearMonth == null || yearMonth.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                yearMonth = sdf.format(new Date());
            }
            return reportService.getReportsByMonth(yearMonth);
        }
    }
}
