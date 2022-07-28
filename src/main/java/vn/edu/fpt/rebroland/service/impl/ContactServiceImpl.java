package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Contact;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.User;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.*;
import vn.edu.fpt.rebroland.repository.ContactRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.UserRepository;
import vn.edu.fpt.rebroland.service.ContactService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {
    private ContactRepository contactRepository;

    private ModelMapper modelMapper;

    private UserRepository userRepository;

    private PostRepository postRepository;

    public ContactServiceImpl(ContactRepository contactRepository, ModelMapper modelMapper,
                              UserRepository userRepository, PostRepository postRepository) {
        this.contactRepository = contactRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public ContactDTO createContact(ContactDTO contactDTO, int userId, int postId) {
        Contact contact = mapToEntity(contactDTO);
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));
        if (postId == 0) {
            contact.setPost(null);
        } else {
            Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
            contact.setPost(post);
        }
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);
        contact.setStartDate(date);
        contact.setUser(user);
        contact.setUnread(true);
        if (user.getRoles().size() == 2) {
            contact.setUserRole(3);
        } else {
            contact.setUserRole(2);
        }

        Contact newContact = contactRepository.save(contact);
        return mapToDTO(newContact);
    }

    @Override
    public void deleteContact(int contactId) {
        try {
            contactRepository.deleteById(contactId);
        } catch (Exception e) {

        }
    }

    @Override
    public ContactDTO getContactById(int contactId) {
        Contact contact = contactRepository.findById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact", "ID", contactId));
        return mapToDTO(contact);
    }

    @Override
    public ContactResponse getContactByUserId(int userId, int pageNo, int pageSize) {
        String sortByStartDate = "start_date";
        String sortDir = "desc";
        Sort sortStartDate = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortByStartDate).ascending() : Sort.by(sortByStartDate).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortStartDate);
        Page<Contact> contacts = contactRepository.getContactByUserId(pageable, userId);
        List<Contact> contactList = contacts.getContent();
        List<ContactDTO> contactDTOList = contactList.stream().map(contact -> mapToDTO(contact)).collect(Collectors.toList());
        int i = 0;
        for (Contact contact : contactList) {
            if (contact.getPost() == null) {
                contactDTOList.get(i).setShortPost(null);
            } else {
                ShortPostDTO shortPostDTO = new ShortPostDTO();
                setDataToSearchDTO(shortPostDTO, contact.getPost());
                contactDTOList.get(i).setShortPost(shortPostDTO);
            }
            i++;
        }
        List<Contact> contacts1 = contactRepository.getContactsByUserId(userId);



        ContactResponse contactResponse = new ContactResponse();
        contactResponse.setTotalResult(contacts1.size());
        contactResponse.setContacts(contactDTOList);
        contactResponse.setPageNo(pageNo + 1);
        contactResponse.setTotalPages(contacts.getTotalPages());
        return contactResponse;
    }

    private ContactDTO mapToDTO(Contact contact) {
        return modelMapper.map(contact, ContactDTO.class);
    }

    private Contact mapToEntity(ContactDTO contactDTO) {
        return modelMapper.map(contactDTO, Contact.class);
    }

    public void setDataToSearchDTO(ShortPostDTO searchDTO, Post postDTO) {

        searchDTO.setPostId(postDTO.getPostId());
//        searchDTO.setArea(postDTO.getArea());
        searchDTO.setTitle(postDTO.getTitle());
//        searchDTO.setDescription(postDTO.getDescription());
//        searchDTO.setAddress(postDTO.getAddress());

//        java.util.Date date = postDTO.getStartDate();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        searchDTO.setStartDate(postDTO.getStartDate());

//        if (postDTO.getPrice() != null) {
//            searchDTO.setPrice(postDTO.getPrice());
//        } else {
//            searchDTO.setPrice(0);
//        }

//        searchDTO.setDistrict(postDTO.getDistrict());
//        searchDTO.setWard(postDTO.getWard());
//        searchDTO.setProvince(postDTO.getProvince());
//        searchDTO.setAddress(postDTO.getAddress());
        searchDTO.setThumbnail(postDTO.getThumbnail());
        searchDTO.setOriginalPost(postDTO.getOriginalPost());

    }
}
