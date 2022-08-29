package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.entity.Contact;
import vn.edu.fpt.rebroland.payload.ContactDTO;
import vn.edu.fpt.rebroland.payload.ContactResponse;

public interface ContactService {
    ContactDTO createContact(ContactDTO contactDTO, int userId, int postId, int userRequestId);
    ContactDTO createBrokerContact(ContactDTO contactDTO);

    void deleteContact(int contactId);

    ContactDTO getContactById(int contactId);

    ContactResponse getContactByBrokerId(int userId, String keyword, int pageNo, int pageSize);

    ContactResponse getContactByUserId(int userId, String keyword, int pageNo, int pageSize);

    void deleteContactByPostId(int postId);

    ContactDTO getContactByUserIdAndPostId(int userRequestId, int userId, int postId, int roleId);
    ContactDTO getContactByUserIdAndPostIdNull(int userRequestId, int userId, int roleId);
}
