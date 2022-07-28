package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.entity.UnitPrice;
import vn.edu.fpt.rebroland.payload.DirectionDTO;
import vn.edu.fpt.rebroland.payload.UnitPriceDTO;
import vn.edu.fpt.rebroland.service.UnitPriceService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app")
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
