package vn.edu.fpt.rebroland.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.rebroland.payload.ReportPostDTO;
import vn.edu.fpt.rebroland.service.ReportPostService;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/report_post")
public class ReportPostController {

    private ReportPostService reportPostService;

    public ReportPostController(ReportPostService reportPostService) {
        this.reportPostService = reportPostService;
    }

    @PostMapping
    public ResponseEntity<?> createReportPost(@Valid @RequestBody ReportPostDTO reportPostDTO){
        ReportPostDTO dto = reportPostService.createReportPost(reportPostDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
