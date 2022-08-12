package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.Coordinate;
import vn.edu.fpt.rebroland.entity.Post;
import vn.edu.fpt.rebroland.exception.ResourceNotFoundException;
import vn.edu.fpt.rebroland.payload.CoordinateDTO;
import vn.edu.fpt.rebroland.payload.RealEstateCoordinateDTO;
import vn.edu.fpt.rebroland.repository.CoordinateRepository;
import vn.edu.fpt.rebroland.repository.PostRepository;
import vn.edu.fpt.rebroland.service.CoordinateService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoordinateServiceImpl implements CoordinateService {
    private CoordinateRepository coordinateRepository;

    private ModelMapper modelMapper;

    private PostRepository postRepository;


    public CoordinateServiceImpl(CoordinateRepository coordinateRepository, ModelMapper modelMapper,
                                 PostRepository postRepository) {
        this.coordinateRepository = coordinateRepository;
        this.modelMapper = modelMapper;
        this.postRepository = postRepository;

    }

    private CoordinateDTO mapToDTO(Coordinate coordinate) {
        return modelMapper.map(coordinate, CoordinateDTO.class);
    }

    private Coordinate mapToEntity(CoordinateDTO coordinateDTO) {
        return modelMapper.map(coordinateDTO, Coordinate.class);
    }

    @Override
    public String createCoordinate(List<Coordinate> coordinateList, int postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        try {
            for (Coordinate listCoordinate : coordinateList) {
//             Coordinate coordinate = new Coordinate();
//                listCoordinate.setPost(post);
                listCoordinate.setPost(post);
                listCoordinate.setLatitude(listCoordinate.getLatitude());
                listCoordinate.setLongitude(listCoordinate.getLongitude());
                coordinateRepository.save(listCoordinate);
            }
            return "insert success Coordinate";
        } catch (Exception e) {
            return "insert fail Coordinate";
        }


    }


    @Override
    public List<RealEstateCoordinateDTO> getCoordinateByPostId(int postId) {
        List<Coordinate> coordinates = coordinateRepository.findByPostId(postId);
        RealEstateCoordinateDTO realEstateCoordinateDTO = new RealEstateCoordinateDTO();
        List<RealEstateCoordinateDTO> realEstateCoordinateDTOS = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            realEstateCoordinateDTO.setLatitude(coordinate.getLatitude());
            realEstateCoordinateDTO.setLongitude(coordinate.getLongitude());
            realEstateCoordinateDTOS.add(realEstateCoordinateDTO);
        }


        return realEstateCoordinateDTOS;
    }

    @Override
    public void deleteCoordinateByPostId(int postId) {
        try {
            coordinateRepository.deleteByPost(postId);
        } catch (Exception e) {

        }

    }

    @Override
    public String updateCoordinate(List<Coordinate> coordinateList, int postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "ID", postId));
        coordinateRepository.deleteByPost(postId);

        if (coordinateList == null) {
            return "update success Coordinate";
        } else {
            try {
                for (Coordinate listCoordinate : coordinateList) {
                    listCoordinate.setPost(post);
                    listCoordinate.setLatitude(listCoordinate.getLatitude());
                    listCoordinate.setLongitude(listCoordinate.getLongitude());
                    coordinateRepository.save(listCoordinate);
                }
                return "update success Coordinate";
            } catch (Exception e) {
                return "update fail Coordinate";
            }
        }

    }


}
