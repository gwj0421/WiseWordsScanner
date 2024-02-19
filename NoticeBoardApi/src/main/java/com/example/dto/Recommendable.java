package com.example.dto;

import java.util.List;
import java.util.Map;

public interface Recommendable {
    Map<String, List<String>> getRecommendUserIds();
}
