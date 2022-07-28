package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Apartment;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.ResidentialHouse;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ApartmentDTO;
import vn.edu.fpt.rebroland.repository.ApartmentRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.service.ApartmentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ApartmentServiceImpl implements ApartmentService {
    private ApartmentRepository apartmentRepository;
    private ModelMapper modelMapper;
    private PostRepository postRepository;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, ModelMapper modelMapper, PostRepository postRepository) {
        this.apartmentRepository = apartmentRepository;
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
    }

    @Override
    public ApartmentDTO createApartment(ApartmentDTO apartmentDTO, int postId) {
        Apartment apartment = mapToEntity(apartmentDTO);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        apartment.setPost(post);
        Apartment newApartment = apartmentRepository.save(apartment);
        return mapToDTO(newApartment);
    }

    @Override
    public ApartmentDTO getApartmentByPostId(int postId) {
        Apartment apartment = apartmentRepository.findByPostId(postId);
        return mapToDTO(apartment);
    }



    @Override
    public void deleteApartmentByPostId(int postId) {
        try {
//            Apartment apartment = apartmentRepository.findByPostId(postId);
//            apartmentRepository.delete(apartment);
            apartmentRepository.deleteByPostId(postId);
        } catch (Exception e) {

        }


    }

    @Override
    public ApartmentDTO updateApartment(ApartmentDTO apartmentDTO, int postId, int id) {
        Apartment apartment = apartmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Apartment", "ID", id));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        apartment.setPost(post);
        apartment.setBarcode(apartmentDTO.getBarcode());
        apartment.setNumberOfBathroom(apartmentDTO.getNumberOfBathroom());
        apartment.setNumberOfBedroom(apartmentDTO.getNumberOfBedroom());
        apartment.setFloorNumber(apartmentDTO.getFloorNumber());
        apartment.setRoomNumber(apartmentDTO.getRoomNumber());
        apartment.setBuildingName(apartmentDTO.getBuildingName());
        Apartment newApartment = apartmentRepository.save(apartment);
        return mapToDTO(newApartment);
    }


    private ApartmentDTO mapToDTO(Apartment apartment) {
        return modelMapper.map(apartment, ApartmentDTO.class);
    }

    private Apartment mapToEntity(ApartmentDTO apartmentDTO) {
        return modelMapper.map(apartmentDTO, Apartment.class);
    }


}
