package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.ResidentialHouse;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ResidentialHouseDTO;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.ResidentialHouseRepository;
import vn.edu.fpt.rebroland.service.ResidentialHouseService;

@Service
public class ResidentialHouseServiceImpl implements ResidentialHouseService {
    private ResidentialHouseRepository residentialHouseRepository;
    private ModelMapper modelMapper;
    private PostRepository postRepository;

    public ResidentialHouseServiceImpl(ResidentialHouseRepository residentialHouseRepository, ModelMapper modelMapper, PostRepository postRepository) {
        this.residentialHouseRepository = residentialHouseRepository;
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;
    }

    @Override
    public ResidentialHouseDTO createResidentialHouse(ResidentialHouseDTO residentialHouseDTO, int postId) {
        ResidentialHouse residentialHouse = mapToEntity(residentialHouseDTO);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        residentialHouse.setPost(post);
        ResidentialHouse newResidentialHouse = residentialHouseRepository.save(residentialHouse);
        return mapToDTO(newResidentialHouse);
    }

    @Override
    public ResidentialHouseDTO getResidentialHouseByPostId(int postId) {
        try {
            ResidentialHouse residentialHouse = residentialHouseRepository.findByPostId(postId);
            return mapToDTO(residentialHouse);
        } catch (Exception e) {
            return null;
        }


    }



    @Override
    public void deleteResidentialHouseByPostId(int postId) {
        try {
            ResidentialHouse residentialHouse = residentialHouseRepository.findByPostId(postId);
            residentialHouseRepository.delete(residentialHouse);
        } catch (Exception e) {

        }


    }

    @Override
    public ResidentialHouseDTO updateResidentialHouse(ResidentialHouseDTO residentialHouseDTO, int postId, int id) {
        ResidentialHouse residentialHouse = residentialHouseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("House", "ID", id));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        residentialHouse.setPost(post);
        residentialHouse.setBarcode(residentialHouseDTO.getBarcode());
        residentialHouse.setPlotNumber(residentialHouseDTO.getPlotNumber());
        residentialHouse.setNumberOfBathroom(residentialHouseDTO.getNumberOfBathroom());
        residentialHouse.setNumberOfBedroom(residentialHouseDTO.getNumberOfBedroom());
        residentialHouse.setNumberOfFloor(residentialHouseDTO.getNumberOfFloor());
        residentialHouse.setFrontispiece(residentialHouseDTO.getFrontispiece());
        ResidentialHouse newResidentialHouse = residentialHouseRepository.save(residentialHouse);
        return mapToDTO(newResidentialHouse);
    }

    private ResidentialHouseDTO mapToDTO(ResidentialHouse residentialHouse) {
        return modelMapper.map(residentialHouse, ResidentialHouseDTO.class);
    }

    private ResidentialHouse mapToEntity(ResidentialHouseDTO residentialHouseDTO) {
        return modelMapper.map(residentialHouseDTO, ResidentialHouse.class);
    }
}
