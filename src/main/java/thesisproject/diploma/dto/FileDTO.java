package thesisproject.diploma.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * Created by aegemberdiev on 28.02.2019
 */

@Getter
@Setter
public class FileDTO {
    private String name;
    private String type;
    private byte[] contentBytes;
    private InputStream inputStream;

    public FileDTO() {
    }

    public FileDTO(String name, String type, byte[] contentBytes, InputStream inputStream) {
        this.name = name;
        this.type = type;
        this.contentBytes = contentBytes;
        this.inputStream = inputStream;
    }
}