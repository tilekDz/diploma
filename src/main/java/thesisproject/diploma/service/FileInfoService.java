package thesisproject.diploma.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thesisproject.diploma.dto.FileDTO;
import thesisproject.diploma.dto.FileInfoDTO;
import thesisproject.diploma.entity.FileData;
import thesisproject.diploma.entity.FileInfo;
import thesisproject.diploma.repository.FileInfoRepository;

import java.io.ByteArrayOutputStream;

/**
 * Created by aegemberdiev on 27.02.2019
 */
@Service
public class FileInfoService {

    private static final String PDFEXTENSION = "application/pdf";

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Transactional
    public FileInfo saveFileInfo(FileInfo fileInfo, FileDTO fileDTO) {


        if (fileInfo == null) {
            fileInfo = new FileInfo();
            fileInfo.setFileType(fileDTO.getType());
            fileInfo.setName(fileDTO.getName());
            FileData data = new FileData(fileDTO.getContentBytes());
            fileInfo.setFileData(data);
        } else {
            fileInfo.setFileType(fileDTO.getType());
            fileInfo.setName(fileDTO.getName());
            if (fileInfo.getFileData() != null) {
                fileInfo.getFileData().setContent(fileDTO.getContentBytes());
            } else {
                FileData data = new FileData(fileDTO.getContentBytes());
                fileInfo.setFileData(data);
            }
        }

        return fileInfoRepository.save(fileInfo);
    }

    public FileInfoDTO prepareFileInfoDTO(FileInfo fileInfo, FileDTO fileDTO) {

        FileInfoDTO fileInfoDTO = new FileInfoDTO();

        fileInfo = saveFileInfo(fileInfo, fileDTO);

        fileInfoDTO.setFileInfo(fileInfo);

        return fileInfoDTO;
    }
}