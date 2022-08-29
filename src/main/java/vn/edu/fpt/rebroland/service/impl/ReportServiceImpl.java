package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.*;
import vn.edu.fpt.rebroland.service.NotificationService;
import vn.edu.fpt.rebroland.service.ReportService;
import com.pusher.rest.Pusher;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReportServiceImpl implements ReportService {

    private ReportRepository reportRepository;

    private ModelMapper mapper;

    private PostRepository postRepository;

    private UserRepository userRepository;

    private ReportDetailRepository detailRepository;

    private NotificationService notificationService;

    private EvidenceRepository evidenceRepository;

    public ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, ModelMapper mapper, UserRepository userRepository,
                             ReportDetailRepository detailRepository, EvidenceRepository evidenceRepository, NotificationService notificationService ) {
        this.reportRepository = reportRepository;
        this.mapper = mapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.detailRepository = detailRepository;
        this.evidenceRepository = evidenceRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public HttpStatus createReport(ReportDTO reportPostDTO) {
        Integer postId = reportPostDTO.getPostId();
        Integer userReportedId = reportPostDTO.getUserReportedId();

//        User userReport = userRepository.findById(reportPostDTO.getUserReportId()).orElseThrow(
//                () -> new UsernameNotFoundException("User not found with id: " + userReportedId));

        User userReport = userRepository.getUserById(reportPostDTO.getUserReportId());
        if(userReport == null){
            return HttpStatus.BAD_REQUEST;
        }

        if((postId != null) && (userReportedId != null)){
            return HttpStatus.BAD_REQUEST;
        }

        ReportDetailDTO detailDTO = new ReportDetailDTO();
        long millis = System.currentTimeMillis();
        Date sqlDate = new Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        Date date = new Date(c.getTimeInMillis());
        detailDTO.setStartDate(date);
        detailDTO.setContent(reportPostDTO.getContent());
        detailDTO.setUser(mapper.map(userReport, UserDTO.class));
        //report post
        if ((postId != null) && (userReportedId == null)) {
            Post post = postRepository.findPostById(postId);
            if (post.getUser().getId() == reportPostDTO.getUserReportId()) {
                return HttpStatus.BAD_REQUEST;
            }

            Report reportedPost = reportRepository.getReportedPost(postId);
            Report report = new Report();
            if (reportedPost == null) {
                //create new report and report detail
                report.setPost(post);
                //1: chưa xử lý, 2: đã xử lý, 3: bỏ qua
                report.setStatus(1);
                Report newReport = reportRepository.save(report);

                detailDTO.setReportId(newReport.getReportId());
                ReportDetail reportDetail = detailRepository.save(mapper.map(detailDTO, ReportDetail.class));

                createEvidence(reportPostDTO.getImages(), reportDetail.getDetailId());
                return HttpStatus.CREATED;
            } else {
                //insert report detail with reportedPostId
                detailDTO.setReportId(reportedPost.getReportId());
                ReportDetail reportDetail = detailRepository.save(mapper.map(detailDTO, ReportDetail.class));

                createEvidence(reportPostDTO.getImages(), reportDetail.getDetailId());
                return HttpStatus.CREATED;
            }
        }

        //report user
        if ((postId == null && userReportedId != null)) {
            User user = userRepository.findById(userReportedId).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with id: " + userReportedId));

            if (userReportedId != null) {
                if (reportPostDTO.getUserReportId() == userReportedId) {
                    return HttpStatus.BAD_REQUEST;
                }
            }
            Report reportedPost = reportRepository.getReportedUser(userReportedId);
            Report report = new Report();
            if (reportedPost == null) {
                report.setUser(user);
                report.setStatus(1);
                Report newReport = reportRepository.save(report);
                detailDTO.setReportId(newReport.getReportId());
                ReportDetail reportDetail = detailRepository.save(mapper.map(detailDTO, ReportDetail.class));
                createEvidence(reportPostDTO.getImages(), reportDetail.getDetailId());
                return HttpStatus.CREATED;
            } else {
                detailDTO.setReportId(reportedPost.getReportId());
                ReportDetail reportDetail = detailRepository.save(mapper.map(detailDTO, ReportDetail.class));
                createEvidence(reportPostDTO.getImages(), reportDetail.getDetailId());
                return HttpStatus.CREATED;
            }
        }
        return HttpStatus.BAD_REQUEST;
    }


    public void createEvidence(List<String> links, int detailId) {
        if(links == null){
            return;
        }else{
            ReportDetail reportDetail = detailRepository.findById(detailId).orElseThrow(() -> new ResourceNotFoundException("Report Detail", "ID", detailId));
            for (String link : links) {
                Evidence evidence = new Evidence();
                try {
                    evidence.setImage(link);
                    evidence.setReportDetail(reportDetail);
                    evidenceRepository.save(evidence);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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

    @Override
    public SearchResponse getListReportPost(int pageNumber, int pageSize, String keyword, String option) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int sortOption = Integer.parseInt(option);
        Page<Report> pageReport = null;
        switch (sortOption){
            case 0:
                pageReport = reportRepository.getListReportPostNotProcess(pageable, keyword);
                break;
            case 1:
                pageReport = reportRepository.getListReportPostProcessed(pageable, keyword);
                break;
            case 2:
                pageReport = reportRepository.getListReportPost(pageable, keyword);
                break;
        }

        List<Report> listReport = pageReport.getContent();
        List<ReportDTO> listDto = listReport.stream().map(report -> mapToDTO(report)).collect(Collectors.toList());

        SearchResponse searchResponse = new SearchResponse();
        List<SearchDTO> listSearchDto = new ArrayList<>();
        for (ReportDTO reportDTO: listDto) {
            PostDTO postDTO = reportDTO.getPost();
            SearchDTO dto = new SearchDTO();
            setDataToSearchDTO(dto, postDTO);
            dto.setNumberOfUserReport(reportRepository.getNumberOfUserReportPost(dto.getPostId(), reportDTO.getStatus(), reportDTO.getReportId()));
            dto.setReportStatus(reportDTO.getStatus());
            dto.setReportId(reportDTO.getReportId());
            dto.setComment(reportDTO.getComment());
            listSearchDto.add(dto);
        }
        searchResponse.setPosts(listSearchDto);
        searchResponse.setPageNo(pageNumber + 1);
        searchResponse.setTotalPages(pageReport.getTotalPages());
        searchResponse.setTotalResult(pageReport.getTotalElements());
        return searchResponse;
    }

    @Override
    public ReportDetailResponse getListDetailReport(int reportId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ReportDetail> pageReport = detailRepository.getListDetailReport(reportId, pageable);
//        Page<Report> pageReport = null;
        List<ReportDetail> listReport = pageReport.getContent();
        List<ReportDetailDTO> listDto = listReport.stream().map(report -> mapper.map(report, ReportDetailDTO.class)).collect(Collectors.toList());
        ReportDetailResponse reportResponse = new ReportDetailResponse();
        reportResponse.setList(listDto);
        reportResponse.setPageNo(pageNumber + 1);
        reportResponse.setTotalPages(pageReport.getTotalPages());
        reportResponse.setTotalResult(pageReport.getTotalElements());
        return reportResponse;
    }

    @Override
    public ReportDetailResponse getListDetailReportUser(int userId, int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public ReportResponse getListReportUser(int pageNumber, int pageSize, String keyword, String option) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<User> pageReport = userRepository.getListReportedUser(pageable, keyword);
        int sortOption = Integer.parseInt(option);
        Page<Report> pageReport = null;;
        switch (sortOption){
            case 0:
                pageReport = reportRepository.getListReportUserNotProcess(pageable, keyword);
                break;
            case 1:
                pageReport = reportRepository.getListReportUserProcessed(pageable, keyword);
                break;
            case 2:
                pageReport = reportRepository.getListReportUser(pageable, keyword);
                break;
        }

        List<Report> listReport = pageReport.getContent();
        List<ReportDTO> listDto = listReport.stream().map(report -> mapper.map(report, ReportDTO.class)).collect(Collectors.toList());

        ReportResponse searchResponse = new ReportResponse();

        for (ReportDTO reportDTO: listDto) {
            UserDTO userDTO = reportDTO.getUser();
            reportDTO.setNumberOfUserReport(reportRepository.getNumberOfUserReportUser(userDTO.getId(), reportDTO.getStatus(), reportDTO.getReportId()));

        }
        searchResponse.setUsers(listDto);
        searchResponse.setPageNo(pageNumber + 1);
        searchResponse.setTotalPages(pageReport.getTotalPages());
        searchResponse.setTotalResult(pageReport.getTotalElements());
        return searchResponse;
    }

    @Override
    public boolean acceptReportPost(int reportId, String comment) {
        Report reportedPost = reportRepository.getReportById(reportId);
        if(reportedPost != null && reportedPost.getStatus() == 1){
            reportedPost.setStatus(2);
            reportedPost.setComment(comment);
            reportRepository.save(reportedPost);
            Post post = reportedPost.getPost();
            post.setBlock(true);

            String s = "Bài viết của bạn mã số " + post.getPostId() + " đã bị chặn vì có người tố cáo !";
            Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
            pusher.setCluster("ap1");
            pusher.setEncrypted(true);
            pusher.trigger("my-channel-" + post.getUser().getId(), "my-event", Collections.singletonMap("message", s));
            saveNotificationAndUpdateUser(s, post.getUser(), "DropOriginalPostStatus");

            postRepository.save(post);

            long millis = System.currentTimeMillis();
            Date sqlDate = new Date(millis);
            Calendar c = Calendar.getInstance();
            c.setTime(sqlDate);
            c.add(Calendar.HOUR, 7);
            Date date = new Date(c.getTimeInMillis());
//            TextMessageDTO messageDTO = new TextMessageDTO();
            List<Post> listPost = postRepository.getPostOfOriginalPost(post.getPostId());
            for(Post p: listPost){
                if(!p.isBlock()){
                    p.setBlock(true);
                    p.setBlockDate(date);
                    postRepository.save(p);
                    String message1 = "Chúng tôi đã ẩn bài viết của bạn. Lý do là: " + comment +
                            ". Nếu có thắc mắc xin liên hệ email rebroland@gmail.com";
//                    messageDTO.setMessage(message1);
//                    template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);
                    pusher.setCluster("ap1");
                    pusher.setEncrypted(true);
                    pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message1));

                    saveNotificationAndUpdateUser(message1, p.getUser(), "DropDerivativePostStatus");
                }
            }

//            List<Integer> listUserReportId = detailRepository.getDetailReportByReportId(reportedPost.getReportId());
//            for (int userReportId: listUserReportId) {
////                messageDTO.setMessage("Tố cáo của bạn được chấp nhận. Cảm ơn bạn vì đã tố cáo !");
////                template.convertAndSend("/topic/message/" + userReportId, messageDTO);
//                pusher.setCluster("ap1");
//                pusher.setEncrypted(true);
//                pusher.trigger("my-channel-" + userReportId, "my-event", Collections.singletonMap("message", "Tố cáo của bạn được chấp nhận. Cảm ơn bạn vì đã tố cáo !"));
//
//                User user = userRepository.findById(userReportId)
//                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userReportId));
//                saveNotificationAndUpdateUser("Tố cáo của bạn được chấp nhận. Cảm ơn bạn vì đã tố cáo !", user);
//            }

            return true;
        }else{
            return false;
        }
    }

    private void saveNotificationAndUpdateUser(String message, User user, String type){
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(user.getId());
        notificationDTO.setContent(message);
        notificationDTO.setPhone(user.getPhone());
        notificationDTO.setType(type);
        notificationService.createContactNotification(notificationDTO);

        int numberUnread = user.getUnreadNotification();
        numberUnread++;
        user.setUnreadNotification(numberUnread);
        userRepository.save(user);
    }

    @Override
    public boolean rejectReportPost(int reportId) {
        Report reportedPost = reportRepository.getReportById(reportId);
        if(reportedPost != null && reportedPost.getStatus() == 1){
            reportedPost.setStatus(3);
            reportRepository.save(reportedPost);

//            TextMessageDTO messageDTO = new TextMessageDTO();
//            List<Integer> listUserReportId = detailRepository.getDetailReportByReportId(reportedPost.getReportId());
//            for (int userReportId: listUserReportId) {
////                messageDTO.setMessage("Chúng tôi đã hủy báo cáo của bạn. Nếu có thắc mắc xin liên hệ SĐT: 0834117442");
////                template.convertAndSend("/topic/message/" + userReportId, messageDTO);
//                Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
//                pusher.setCluster("ap1");
//                pusher.setEncrypted(true);
//                pusher.trigger("my-channel-" + userReportId, "my-event", Collections.singletonMap("message", "Chúng tôi đã hủy báo cáo của bạn. Nếu có thắc mắc xin liên hệ SĐT: 0834117442"));
//
//                User user = userRepository.findById(userReportId)
//                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userReportId));
//                saveNotificationAndUpdateUser("Chúng tôi đã hủy báo cáo của bạn. Nếu có thắc mắc xin liên hệ SĐT: 0834117442", user);
//            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean acceptReportUser(int reportId, String comment) {
        Report report = reportRepository.getReportById(reportId);
        long millis = System.currentTimeMillis();
        Date sqlDate = new Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.add(Calendar.HOUR, 7);
        Date date = new Date(c.getTimeInMillis());
        if(report != null && report.getStatus() == 1){
            report.setStatus(2);
            report.setComment(comment);
            reportRepository.save(report);
            User user = report.getUser();

            user.setBlock(true);
            user.setBlockDate(date);
            userRepository.save(user);

            Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
            pusher.setCluster("ap1");
            pusher.setEncrypted(true);
            pusher.trigger("my-channel-" + user.getId(), "my-event", Collections.singletonMap("block", true));

            //block bài viết của user
            List<Post> listPost = postRepository.getAllPostUnBlockByUserId(user.getId());
            List<Post> listDerivative = new ArrayList<>();

            for(Post post: listPost){
                post.setBlock(true);
                post.setBlockDate(date);
                postRepository.save(post);

//                TextMessageDTO messageDTO = new TextMessageDTO();
//                String message = "Chúng tôi đã ẩn bài viết của bạn. Nếu có thắc mắc xin liên hệ số 0397975445.";
////                messageDTO.setMessage(message);
////                template.convertAndSend("/topic/message/" + post.getUser().getId(), messageDTO);
//                Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
//                pusher.setCluster("ap1");
//                pusher.setEncrypted(true);
//                pusher.trigger("my-channel-" + post.getUser().getId(), "my-event", Collections.singletonMap("message", message));
//
//                saveNotificationAndUpdateUser(message, post.getUser());

                listDerivative = postRepository.getPostOfOriginalPost(post.getPostId());
                for(Post p: listDerivative){
                    if(!p.isBlock()){
                        p.setBlock(true);
                        p.setBlockDate(date);
                        postRepository.save(p);
                        String message1 = "Chúng tôi đã ẩn bài viết của bạn. Nếu có thắc mắc xin liên hệ email rebroland@gmail.com";
//                        messageDTO.setMessage(message1);
//                        template.convertAndSend("/topic/message/" + p.getUser().getId(), messageDTO);

                        pusher.setCluster("ap1");
                        pusher.setEncrypted(true);
                        pusher.trigger("my-channel-" + p.getUser().getId(), "my-event", Collections.singletonMap("message", message1));

                        saveNotificationAndUpdateUser(message1, p.getUser(), "DropDerivativePostStatus");
                    }
                }
            }

//            TextMessageDTO messageDTO = new TextMessageDTO();
//            List<Integer> listUserReportId = detailRepository.getDetailReportByReportId(report.getReportId());
//            for (int userReportId: listUserReportId) {
////                messageDTO.setMessage("Tố cáo của bạn được chấp nhận. Cảm ơn bạn vì đã tố cáo !");
////                template.convertAndSend("/topic/message/" + userReportId, messageDTO);
////                Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
//                pusher.setCluster("ap1");
//                pusher.setEncrypted(true);
//                pusher.trigger("my-channel-" + userReportId, "my-event", Collections.singletonMap("message", "Tố cáo của bạn được chấp nhận. Cảm ơn bạn vì đã tố cáo !"));
//
//                User userReport = userRepository.findById(userReportId)
//                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userReportId));
//                saveNotificationAndUpdateUser("Tố cáo của bạn được chấp nhận. Cảm ơn bạn vì đã tố cáo !", userReport);
//            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean rejectReportUser(int reportId) {
        Report reportedPost = reportRepository.getReportById(reportId);
        if(reportedPost != null && reportedPost.getStatus() == 1){
            reportedPost.setStatus(3);
            reportRepository.save(reportedPost);

//            TextMessageDTO messageDTO = new TextMessageDTO();
//            List<Integer> listUserReportId = detailRepository.getDetailReportByReportId(reportedPost.getReportId());
//            for (int userReportId: listUserReportId) {
////                messageDTO.setMessage("Chúng tôi đã hủy báo cáo của bạn. Nếu có thắc mắc xin liên hệ SĐT: 0834117442");
////                template.convertAndSend("/topic/message/" + userReportId, messageDTO);
//                Pusher pusher = new Pusher("1465234", "242a962515021986a8d8", "61b1284a169f5231d7d3");
//                pusher.setCluster("ap1");
//                pusher.setEncrypted(true);
//                pusher.trigger("my-channel-" + userReportId, "my-event", Collections.singletonMap("message", "Chúng tôi đã hủy báo cáo của bạn. Nếu có thắc mắc xin liên hệ SĐT: 0834117442"));
//
//                User user = userRepository.findById(userReportId)
//                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userReportId));
//                saveNotificationAndUpdateUser("Chúng tôi đã hủy báo cáo của bạn. Nếu có thắc mắc xin liên hệ SĐT: 0834117442", user);
//            }
            return true;
        }else{
            return false;
        }
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

    public void setDataToSearchDTO(SearchDTO searchDTO, PostDTO postDTO) {

        searchDTO.setPostId(postDTO.getPostId());
        searchDTO.setArea(postDTO.getArea());
        searchDTO.setTitle(postDTO.getTitle());
        searchDTO.setDescription(postDTO.getDescription());
        searchDTO.setAddress(postDTO.getAddress());

        Date date = postDTO.getStartDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        searchDTO.setStartDate(simpleDateFormat.format(date));

        if(postDTO.getPrice() !=  null){
            searchDTO.setPrice(postDTO.getPrice());
        }else{
            searchDTO.setPrice(0);
        }

        searchDTO.setDistrict(postDTO.getDistrict());
        searchDTO.setWard(postDTO.getWard());
        searchDTO.setProvince(postDTO.getProvince());
        searchDTO.setAddress(postDTO.getAddress());
        searchDTO.setStatus(postDTO.getStatus());
        searchDTO.setUnitPrice(postDTO.getUnitPrice());
        searchDTO.setThumbnail(postDTO.getThumbnail());
        searchDTO.setOriginalPost(postDTO.getOriginalPost());
        searchDTO.setAllowDerivative(postDTO.isAllowDerivative());
        searchDTO.setUser(postDTO.getUser());
    }

}
