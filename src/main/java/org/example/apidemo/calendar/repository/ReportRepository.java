package org.example.apidemo.calendar.repository;

import org.example.apidemo.calendar.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findByDateStartingWith(String datePrefix);
}

