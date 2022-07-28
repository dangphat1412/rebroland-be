package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.Report;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.payload.ReportDTO;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.ReportRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ReportService;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;


@Service
public class ReportServiceImpl implements ReportService {

    private ReportRepository reportRepository;

    private ModelMapper mapper;

    private PostRepository postRepository;

    private UserRepository userRepository;

    public ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, ModelMapper mapper, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.mapper = mapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    //Note: chưa check lặp
    @Override
    public ReportDTO createReport(ReportDTO reportPostDTO) {
        Integer postId = reportPostDTO.getPostId();
        Integer userReportedId = reportPostDTO.getUserReportedId();

        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        reportPostDTO.setReportDate(date);
        reportPostDTO.setStatus(false);

        if (postId != null) {
            Post post = postRepository.findPostByPostId(postId);
            if (post.getUser().getId() == reportPostDTO.getUserId()) {
                return null;
            }
        }

        if(userReportedId != null){
            if(reportPostDTO.getUserId() == userReportedId){
                return null;
            }
        }


        if((postId != null && userReportedId == null) || (postId == null && userReportedId != null)) {
            Report report = mapToEntity(reportPostDTO);
            Report newReport = reportRepository.save(report);
            return mapToDTO(newReport);
        }else{
            return null;
        }

    }

    //delete report by post id
    @Override
    public void deleteReportByPostId(int postId) {
        try {
            reportRepository.deleteReportPostByPostId(postId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public User getUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));;
        return user;
    }

    @Override
    public User getUserByToken(String token) {
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String phone = payload.getString("sub");
        User user = userRepository.findByPhone(phone).
                orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        return user;
    }

    private ReportDTO mapToDTO(Report report){
        ReportDTO reportPostDTO = mapper.map(report, ReportDTO.class);
        return reportPostDTO;
    }

    private Report mapToEntity(ReportDTO reportPostDTO){
        Report report = mapper.map(reportPostDTO, Report.class);
        return report;
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

}
