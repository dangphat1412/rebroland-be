package vn.edu.fpt.rebroland.controller;

import vn.edu.fpt.rebroland.payload.PropertyTypeDTO;
import vn.edu.fpt.rebroland.service.PropertyTypeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://rebroland-frontend.vercel.app/")
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
