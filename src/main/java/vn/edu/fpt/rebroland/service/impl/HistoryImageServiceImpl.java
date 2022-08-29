package vn.edu.fpt.rebroland.service.impl;

import vn.edu.fpt.rebroland.entity.HistoryImage;
import vn.edu.fpt.rebroland.repository.HistoryImageRepository;
import vn.edu.fpt.rebroland.service.HistoryImageService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryImageServiceImpl implements HistoryImageService {

    private ModelMapper modelMapper;
    private HistoryImageRepository historyImageRepository;

    public HistoryImageServiceImpl(ModelMapper modelMapper, HistoryImageRepository historyImageRepository) {
        this.modelMapper = modelMapper;
        this.historyImageRepository = historyImageRepository;
    }

    @Override
    public String createHistoryImage(List<String> links, int historyId, int typeId) {
        try {
            for (String link : links) {
                HistoryImage historyImage = new HistoryImage();
                try {
                    historyImage.setImage(link);
                    historyImage.setHistoryId(historyId);
                    historyImage.setTypeId(typeId);
                    historyImageRepository.save(historyImage);
                } catch (Exception ex) {
                    return null;
                }
            }
            return "Insert successfully Image !";
        } catch (Exception e) {
            return "Insert failed Image !";
        }
    }


}
