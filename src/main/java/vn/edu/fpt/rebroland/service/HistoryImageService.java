package vn.edu.fpt.rebroland.service;

import java.util.List;

public interface HistoryImageService {
    String createHistoryImage(List<String> links, int historyId, int typeId);
}
