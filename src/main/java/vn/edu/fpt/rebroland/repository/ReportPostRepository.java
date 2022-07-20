package vn.edu.fpt.rebroland.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.rebroland.entity.Report;

public interface ReportPostRepository extends JpaRepository<Report, Integer> {
}
