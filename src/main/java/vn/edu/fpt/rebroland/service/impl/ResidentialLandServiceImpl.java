package vn.edu.fpt.rebroland.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.entity.ResidentialLand;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.ResidentialLandDTO;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.repository.ResidentialLandRepository;
import vn.edu.fpt.rebroland.service.ResidentialLandService;

@Service
public class ResidentialLandServiceImpl implements ResidentialLandService {
    private ResidentialLandRepository residentialLandRepository;


    private ModelMapper mapper;

    private PostRepository postRepository;

    public ResidentialLandServiceImpl(ResidentialLandRepository residentialLandRepository, ModelMapper mapper, PostRepository postRepository) {
        this.residentialLandRepository = residentialLandRepository;
        this.mapper = mapper;
        this.postRepository = postRepository;
    }

    @Override
    public ResidentialLandDTO createResidentialLand(ResidentialLandDTO residentialLandDTO, int postId) {
        ResidentialLand residentialLand = mapToEntity(residentialLandDTO);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        residentialLand.setPost(post);
        ResidentialLand newResidentialLand = residentialLandRepository.save(residentialLand);
        return mapToDTO(newResidentialLand);
    }

    @Override
    public ResidentialLandDTO getResidentialLandByPostId(int postId) {
        ResidentialLand residentialLand = residentialLandRepository.findByPostId(postId);
        return mapToDTO(residentialLand);
    }



    @Override
    public void deleteResidentialLandByPostId(int postId) {
        try {
            ResidentialLand residentialLand = residentialLandRepository.findByPostId(postId);
            residentialLandRepository.delete(residentialLand);
        } catch (Exception e) {

        }

    }

    @Override
    public ResidentialLandDTO updateResidentialLand(ResidentialLandDTO residentialLandDTO, int postId, int id) {
        ResidentialLand residentialLand = residentialLandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ResidentialLand", "ID", id));
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        residentialLand.setPost(post);
        residentialLand.setBarcode(residentialLandDTO.getBarcode());
        residentialLand.setPlotNumber(residentialLandDTO.getPlotNumber());
        residentialLand.setFrontispiece(residentialLandDTO.getFrontispiece());
        ResidentialLand newResidentialLand = residentialLandRepository.save(residentialLand);
        return mapToDTO(newResidentialLand);
    }

    private ResidentialLandDTO mapToDTO(ResidentialLand residentialLand) {
        return mapper.map(residentialLand, ResidentialLandDTO.class);
    }

    private ResidentialLand mapToEntity(ResidentialLandDTO residentialLandDTO) {
        return mapper.map(residentialLandDTO, ResidentialLand.class);
    }

}
