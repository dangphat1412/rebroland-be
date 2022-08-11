package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.Status;
import vn.edu.fpt.rebroland.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduleTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PostRepository postRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateExpiredPost() {
        Date date = new Date();
        List<Post> listPost = postRepository.getExpiredPostByDate(date);
        for (Post post: listPost) {
            post.setStatus(new Status(5));
        }
//        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
