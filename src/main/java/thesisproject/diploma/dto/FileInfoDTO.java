package thesisproject.diploma.dto;

import lombok.Data;
import thesisproject.diploma.entity.FileInfo;

/**
 * Created by aegemberdiev on 30.04.2019
 */
@Data
public class FileInfoDTO {
    private FileInfo fileInfo;
    private FileInfo pdfFileInfo;

    public FileInfoDTO() {
    }

    public FileInfoDTO(FileInfo fileInfo, FileInfo pdfFileInfo) {
        this.fileInfo = fileInfo;
        this.pdfFileInfo = pdfFileInfo;
    }
}