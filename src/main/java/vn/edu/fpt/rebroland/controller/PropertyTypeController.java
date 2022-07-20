package vn.edu.fpt.rebroland.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.rebroland.payload.PropertyTypeDTO;
import vn.edu.fpt.rebroland.service.PropertyTypeService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/propertytypes")
public class PropertyTypeController {
    private PropertyTypeService propertyTypeService;

    public PropertyTypeController(PropertyTypeService propertyTypeService) {
        this.propertyTypeService = propertyTypeService;
    }

    @GetMapping
    public List<PropertyTypeDTO> getAllPropertyTypes() {

        return propertyTypeService.getAllPropertyType();

    }


}