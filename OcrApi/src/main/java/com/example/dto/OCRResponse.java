package com.example.dto;

import java.util.List;

public record OCRResponse(String version, String requestId, long timestamp, List<ImageInfo> images) {
    public record ImageInfo(String uid, String name, String inferResult, String message,
                            ValidationResult validationResult, ConvertedImageInfo convertedImageInfo,
                            List<Field> fields) {
        public record ValidationResult(String result) {
        }

        public record ConvertedImageInfo(int width, int height, int pageIndex, boolean longImage) {
        }

        public record Field(String valueType, BoundingPoly boundingPoly, String inferText, double inferConfidence,
                            String type, boolean lineBreak) {
            public record BoundingPoly(List<Vertex> vertices) {
                public record Vertex(double x, double y) {
                }
            }
        }
    }
}
