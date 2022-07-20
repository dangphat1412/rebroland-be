package vn.edu.fpt.rebroland;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modalMapper() {
        return new ModelMapper();
    }

}
