package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.NotificationDTO;

import java.util.List;

public interface NotificationService {
    NotificationDTO createContactNotification(NotificationDTO notificationDTO);

    List<NotificationDTO> getUnreadNotification(int userId);

    List<NotificationDTO> getUnreadNotificationPaging(int userId, int pageNumber);

    NotificationDTO getDetailNotificationById(int id);
}
