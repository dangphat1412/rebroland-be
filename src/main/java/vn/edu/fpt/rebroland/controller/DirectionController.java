package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.payload.DirectionDTO;
import vn.edu.fpt.rebroland.service.DirectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/directions")
public class DirectionController {
    private DirectionService directionService;

    public DirectionController(DirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping
    public List<DirectionDTO> getAllDirections(){
        return  directionService.getAllDirections();
    }
    @GetMapping("/{id}")
    public ResponseEntity<DirectionDTO> getPostById(@PathVariable(name = "id") Integer id) {
        return new ResponseEntity<>(directionService.getDirectionById(id), HttpStatus.OK);
    }
}
