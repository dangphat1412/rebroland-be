package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.*;
import vn.edu.fpt.rebroland.repository.BrokerInfoRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.RoleRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ScheduleTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduleTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private BrokerInfoRepository brokerInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateExpiredPost() {
        Date date = new Date();
        List<Post> listPost = postRepository.getExpiredPostByDate(date);
        for (Post post: listPost) {
            post.setStatus(new Status(5));
            postRepository.save(post);
        }
//        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0 05 19 * * ?")
    public void updateExpiredBroker() {
        Date date = new Date();
        List<BrokerInfo> list = brokerInfoRepository.getExpiredBrokerByDate(date);
        for (BrokerInfo brokerInfo: list) {
            int userId = brokerInfo.getUserId();
            User user = userRepository.findById(userId).get();
            user.setCurrentRole(2);
            Role role = roleRepository.findById(3).get();
            Set<Role> roles = user.getRoles();
            for (Role r : roles) {
                if(r.getId() == role.getId()){
                    roles.remove(r);
                }
            }
            user.setRoles(roles);
            userRepository.save(user);

//            log.info("The time is now");
        }
    }

}
