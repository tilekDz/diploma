package thesisproject.diploma.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.poi.hpsf.Section;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import sun.management.counter.Units;
import thesisproject.diploma.commons.CyrilicToAsciiConvertUtil;
import thesisproject.diploma.dto.FileDTO;
import thesisproject.diploma.dto.FileInfoDTO;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.entity.UserDiploma;
import thesisproject.diploma.repository.HardwareRepository;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.List;


@Service
public class HardwareService {

    @Autowired
    private StockService stockService;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Autowired
    private FileInfoService fileInfoService;

    public Hardware save(Hardware hardware){
        return hardwareRepository.save(hardware);
    }

    public Hardware findById(Long id){
        return hardwareRepository.getOne(id);
    }

    public Page<Hardware> getAllHardwares(Specification specification, Pageable pageable) {
        return hardwareRepository.findAll(specification, pageable);
    }

    public void deleteHardware(Long id){
        Hardware hardware = findById(id);
        if(hardware!=null){
            hardware.setIsDeleted(true);
            save(hardware);
        }
    }

    public List<Hardware> getAllByRoomNumberAndDeletedFalse(Long number){
        return hardwareRepository.findAllByRoomNumberAndIsDeletedFalse(number);
    }

    public List<Hardware> getAllByRoomNumberAndCampusAndDeletedFalse(Long number, String campus){
        return hardwareRepository.findAllByRoomNumberAndCampusBlockAndIsDeletedFalse(number, campus);
    }

    public void addToHardwareFromStock(Long stockId, String name, String description, String type, Long roomNumber, String campusBlock) throws Exception {
        Hardware hardware = new Hardware(name, description, campusBlock, type, roomNumber, false);
        save(hardware);
        File qrFile = createQRCode(hardware);
        FileDTO fileDTO = new FileDTO(qrFile.getName(), "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                Files.readAllBytes(qrFile.toPath()), new FileInputStream(qrFile));
        FileInfoDTO fileInfoDTO = fileInfoService.prepareFileInfoDTO(hardware.getFileTemplate(), fileDTO);
        hardware.setFileTemplate(fileInfoDTO.getFileInfo());

        Stock stock = stockService.getById(stockId);
        stock.setQuantity(stock.getQuantity()-1);
        stockService.save(stock);
        save(hardware);
    }

    private File createQRCode(Hardware hardware) throws Exception {
        String qrCodeText = "ID: "+ hardware.getId().toString() + "\n"+
                "NAME: " + hardware.getName() + "\n"+
                "CAMPUS: " + hardware.getCampusBlock() + "\n" +
                "ROOM: " + hardware.getRoomNumber().toString() + "\n" +
                "DATE: " + hardware.getCreatedDate().toString();
        String filePath = "JD.png";
        int size = 125;
        String fileType = "png";
        File qrFile = new File(filePath);
        createQRImage(qrFile, qrCodeText, size, fileType);


        WordprocessingMLPackage wordMLPackage =
                WordprocessingMLPackage.createPackage();

        File file = new File(filePath);
        byte[] bytes = convertImageToByteArray(file);
        addImageToPackage(wordMLPackage, bytes);

        File downFile = new java.io.File("word_image.docx");
        wordMLPackage.save(downFile);

        return downFile;
    }
    private void createQRImage(File qrFile, String qrCodeText, int size, String fileType)
            throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
    }


    /**
     *  Docx4j contains a utility method to create an image part from an array of
     *  bytes and then adds it to the given package. In order to be able to add this
     *  image to a paragraph, we have to convert it into an inline object. For this
     *  there is also a method, which takes a filename hint, an alt-text, two ids
     *  and an indication on whether it should be embedded or linked to.
     *  One id is for the drawing object non-visual properties of the document, and
     *  the second id is for the non visual drawing properties of the picture itself.
     *  Finally we add this inline object to the paragraph and the paragraph to the
     *  main document of the package.
     *
     *  @param wordMLPackage The package we want to add the image to
     *  @param bytes         The bytes of the image
     *  @throws Exception    Sadly the createImageInline method throws an Exception
     *                       (and not a more specific exception type)
     */
    private static void addImageToPackage(WordprocessingMLPackage wordMLPackage,
                                          byte[] bytes) throws Exception {
        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

        int docPrId = 1;
        int cNvPrId = 2;
        Inline inline = imagePart.createImageInline("Filename hint",
                "Alternative text", docPrId, cNvPrId, false);

        P paragraph = addInlineImageToParagraph(inline);

        wordMLPackage.getMainDocumentPart().addObject(paragraph);
    }

    /**
     *  We create an object factory and use it to create a paragraph and a run.
     *  Then we add the run to the paragraph. Next we create a drawing and
     *  add it to the run. Finally we add the inline object to the drawing and
     *  return the paragraph.
     *
     * @param   inline The inline object containing the image.
     * @return  the paragraph containing the image
     */
    private static P addInlineImageToParagraph(Inline inline) {
        // Now add the in-line image to a paragraph
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return paragraph;
    }

    /**
     * Convert the image from the file into an array of bytes.
     *
     * @param file  the image file to be converted
     * @return      the byte array containing the bytes from the image
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static byte[] convertImageToByteArray(File file)
            throws FileNotFoundException, IOException {
        InputStream is = new FileInputStream(file );
        long length = file.length();
        // You cannot create an array using a long, it needs to be an int.
        if (length > Integer.MAX_VALUE) {
            System.out.println("File too large!!");
        }
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        // Ensure all the bytes have been read
        if (offset < bytes.length) {
            System.out.println("Could not completely read file "
                    +file.getName());
        }
        is.close();
        return bytes;
    }
}
