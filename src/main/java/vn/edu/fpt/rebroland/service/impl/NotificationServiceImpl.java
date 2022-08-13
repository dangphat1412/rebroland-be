package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Notification;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.NotificationDTO;
import vn.edu.fpt.rebroland.repository.NotificationRepository;
import vn.edu.fpt.rebroland.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;

    private ModelMapper mapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ModelMapper mapper) {
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
    }

    @Override
    public NotificationDTO createContactNotification(NotificationDTO notificationDTO) {
//        notificationDTO.setType("Contact");

        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        notificationDTO.setDate(date);

        notificationDTO.setUnRead(true);

        Notification notification = mapToEntity(notificationDTO);
        Notification newDto = notificationRepository.save(notification);
        return mapToDTO(newDto);
    }

    @Override
    public List<NotificationDTO> getUnreadNotification(int userId) {
        List<Notification> lists = notificationRepository.getUnreadNotification(userId);
        return lists.stream().map(notification -> mapToDTO(notification)).collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> getUnreadNotificationPaging(int userId, int pageNumber) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Notification> lists = notificationRepository.getUnreadNotificationPaging(userId, pageable);
        List<Notification> listNotification = lists.getContent();
        return listNotification.stream().map(notification -> mapToDTO(notification)).collect(Collectors.toList());
    }

    @Override
    public NotificationDTO getDetailNotificationById(int id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setUnRead(false);
        notificationRepository.save(notification);
        return mapToDTO(notification);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return mapper.map(notification, NotificationDTO.class);
    }

    private Notification mapToEntity(NotificationDTO notificationDTO) {
        return mapper.map(notificationDTO, Notification.class);
    }
}
