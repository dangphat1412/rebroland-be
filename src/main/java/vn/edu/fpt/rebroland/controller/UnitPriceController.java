package vn.edu.fpt.rebroland.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.rebroland.payload.UnitPriceDTO;
import vn.edu.fpt.rebroland.service.UnitPriceService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/unitprices")
public class UnitPriceController {
    private UnitPriceService unitPriceService;

    public UnitPriceController(UnitPriceService unitPriceService) {
        this.unitPriceService = unitPriceService;
    }

    @GetMapping
    public List<UnitPriceDTO> getAllUnitPrices() {
        return unitPriceService.getAllUnitPrices();
    }
}
