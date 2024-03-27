package com.example.dto;

import com.example.error.InvalidFileException;
import lombok.Getter;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

@Getter
public class OCRRequest {
    private MessageForm message;
    private FilePart file;

    public OCRRequest(MessageForm message, FilePart file) {
        this.message = message;
        this.file = file;
    }

    public static MultiValueMap<String, Object> makeBody(FilePart file, String version) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("message", MessageForm.createRequest(file,version));
        body.add("file", file);
        return body;
    }

    @Getter
    public static class MessageForm {
        private List<ImageForm> images;
        private String requestId;
        private String version;
        private long timestamp;

        public MessageForm(List<ImageForm> images, String requestId, String version, long timestamp) {
            this.images = images;
            this.requestId = requestId;
            this.version = version;
            this.timestamp = timestamp;
        }

        public static MessageForm createRequest(FilePart filePart, String version) {
            if (filePart == null) {
                throw new InvalidFileException("Error : invalid file");
            }
            return new MessageForm(List.of(ImageForm.makeImageForm(filePart.filename()))
                    , UUID.randomUUID().toString(), version, System.currentTimeMillis());
        }
    }


    @Getter
    public static class ImageForm {
        private String format;
        private String name;

        private ImageForm(String format, String name) {
            this.format = format;
            this.name = name;
        }

        public static ImageForm makeImageForm(String fileName) {
            int lastIndex = fileName.lastIndexOf('.');
            if (fileName.isEmpty() || lastIndex == -1 || lastIndex == fileName.length() - 1) {
                throw new InvalidFileException("Error : invalid filename");
            }
            return new ImageForm(fileName.substring(lastIndex + 1), fileName.substring(0, lastIndex));
        }
    }


}
