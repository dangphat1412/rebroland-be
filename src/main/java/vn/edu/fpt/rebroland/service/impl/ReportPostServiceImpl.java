package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Report;
import vn.edu.fpt.rebroland.payload.ReportPostDTO;
import vn.edu.fpt.rebroland.repository.ReportPostRepository;
import vn.edu.fpt.rebroland.service.ReportPostService;


@Service
public class ReportPostServiceImpl implements ReportPostService {

    private ReportPostRepository reportPostRepository;

    private ModelMapper mapper;

    public ReportPostServiceImpl(ReportPostRepository reportPostRepository, ModelMapper mapper) {
        this.reportPostRepository = reportPostRepository;
        this.mapper = mapper;
    }

    @Override
    public ReportPostDTO createReportPost(ReportPostDTO reportPostDTO) {
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        reportPostDTO.setDate(date);

        Report report = mapToEntity(reportPostDTO);
        Report newReport = reportPostRepository.save(report);
        return mapToDTO(newReport);
    }

    private ReportPostDTO mapToDTO(Report report){
        ReportPostDTO reportPostDTO = mapper.map(report, ReportPostDTO.class);
        return reportPostDTO;
    }

    private Report mapToEntity(ReportPostDTO reportPostDTO){
        Report report = mapper.map(reportPostDTO, Report.class);
        return report;
    }
}
