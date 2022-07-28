package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.payload.LongevityDTO;
import vn.edu.fpt.rebroland.service.LongevityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/longevity")
public class LongevityController {

    private LongevityService longevityService;

    public LongevityController(LongevityService longevityService) {
        this.longevityService = longevityService;
    }

    @GetMapping
    public List<LongevityDTO> getAllDirections(){
        return  longevityService.getAllLongevity();
    }
}
