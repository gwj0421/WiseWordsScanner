package com.example.dto;

import java.util.Set;

public interface Recommendable {
    Set<String> getNotRecommendUserIds();
    Set<String> getRecommendUserIds();
}
