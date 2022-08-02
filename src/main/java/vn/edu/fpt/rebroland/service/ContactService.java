package vn.edu.fpt.rebroland.service;

import vn.edu.fpt.rebroland.payload.ContactDTO;
import vn.edu.fpt.rebroland.payload.ContactResponse;

public interface ContactService {
    ContactDTO createContact(ContactDTO contactDTO, int userId, int postId);

    void deleteContact(int contactId);

    ContactDTO getContactById(int contactId);

    ContactResponse getContactByUserId(int userId, int pageNo, int pageSize);

    void deleteContactByPostId(int postId);
}
