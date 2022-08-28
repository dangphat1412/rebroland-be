package vn.edu.fpt.rebroland.controller;


import vn.edu.fpt.rebroland.payload.StatusDTO;
import vn.edu.fpt.rebroland.service.StatusService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
@RequestMapping("/api/status")
public class StatusController {
    private StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public List<StatusDTO> getAllStatus() {
        return statusService.getAllStatus();
    }
}
