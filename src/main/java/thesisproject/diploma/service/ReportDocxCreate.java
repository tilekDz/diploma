package thesisproject.diploma.service;


import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisproject.diploma.dto.FileDTO;
import thesisproject.diploma.dto.FileInfoDTO;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Report;
import thesisproject.diploma.service.HardwareService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by asemenov on 29.01.2018.
 */
@Service
public class ReportDocxCreate {

    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private FileInfoService fileInfoService;

    XWPFDocument doc;

    public void createDocxFile(HttpServletResponse response, Long room, String campus, String date, String paperNum){
        try {
            doc = new XWPFDocument();
            ServletOutputStream out = response.getOutputStream();
            createHeader(room, campus, date, paperNum);
            createTable(room, campus);
            createFooter();
            doc.write(out);

            Report report = new Report();

            File file = new File("word_report.docx");
            FileOutputStream downFile = new FileOutputStream(file);
            doc.write(downFile);

            File qrFile = new File(file.getName());
            FileDTO fileDTO = new FileDTO(qrFile.getName(), "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    Files.readAllBytes(qrFile.toPath()), new FileInputStream(qrFile));
            FileInfoDTO fileInfoDTO = fileInfoService.prepareFileInfoDTO(report.getFileTemplate(), fileDTO);
            report.setFileTemplate(fileInfoDTO.getFileInfo());
            report.setCampusBlock(campus);
            report.setRoomNumber(room);
            report.setCreatedDate(new Date());
            reportService.save(report);

            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createHeader(Long room, String campus, String date, String paperNum){
        XWPFHeaderFooterPolicy headerFooterPolicy = doc.getHeaderFooterPolicy();
        if (headerFooterPolicy == null) headerFooterPolicy = doc.createHeaderFooterPolicy();
        XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

        XWPFParagraph p1 = doc.createParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);
        p1.setWordWrapped(true);
        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setText("КЕҢСЕ ЭМЕРЕКТЕРИНИН ЭСЕБИ");
        r1.setFontSize(20);

        XWPFTable table2 = doc.createTable(2, 2);
        table2.setCellMargins(5,5,5,5);
        table2.setTableAlignment(TableRowAlign.CENTER);
        XWPFParagraph p2 = table2.getRow(0).getCell(0).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);
        p2.setWordWrapped(true);
        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setText("Документтин коду: ");
        p2.createRun().setText("S-IAAU-FR-065-TR");
        table2.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6000));


        XWPFParagraph p6 = table2.getRow(0).getCell(1).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);
        p6.setWordWrapped(true);
        XWPFRun r6 = p6.createRun();
        r6.setBold(true);
        r6.setText("Текшерилген күнү/мезгили: ");
        p6.createRun().setText(date);
        table2.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6000));


        XWPFParagraph p7 = table2.getRow(1).getCell(0).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);
        p7.setWordWrapped(true);
        XWPFRun r7 = p7.createRun();
        r7.setBold(true);
        Date dateCreated = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String strDate= formatter.format(dateCreated);
        r7.setText("Чыгарылган күнү: ");
        p7.createRun().setText(strDate);
        table2.getRow(1).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6000));


        XWPFParagraph p8 = table2.getRow(1).getCell(1).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);
        p8.setWordWrapped(true);
        XWPFRun r8 = p8.createRun();
        r8.setBold(true);
        r8.setText("Баракчанын номери: ");
        p8.createRun().setText(paperNum);
        table2.getRow(1).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6000));

        XWPFParagraph p11 = doc.createParagraph();
        p11.setAlignment(ParagraphAlignment.LEFT);
        p11.setWordWrapped(true);

        XWPFParagraph p10 = doc.createParagraph();
        p10.setAlignment(ParagraphAlignment.LEFT);
        p10.setWordWrapped(true);
        XWPFRun r10 = p10.createRun();
        r10.setBold(true);
        r10.setText("Иш кабинети: ");
        p10.createRun().setText(campus +" "+room);

        XWPFParagraph p12 = doc.createParagraph();
        p12.setAlignment(ParagraphAlignment.LEFT);
        p12.setWordWrapped(true);


        XWPFParagraph paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        // create footer start
        XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        run = paragraph.createRun();
        run.setText("Эгер тапшырылган жабдуулар жоголуп кетсе, бузулуп калса кабыл алуучу же болбосо бөлүм башчы милдеттүү түрдө ...... акт түзүп, теңникалык башкы катчылыкка жазуу түрүндө кайрылышы абзел.");
    }

    public void createTable(Long room, String campus){
        List<Hardware> hardwareList = hardwareService.getAllByRoomNumberAndCampusAndDeletedFalse(room, campus);
        if(!hardwareList.isEmpty() && hardwareList != null) {
            Map<String, List<Hardware>> hardwareMap = hardwareList.stream().collect(Collectors.groupingBy(Hardware::getName));

            XWPFTable table = doc.createTable(hardwareMap.size()+1, 3);
            table.setTableAlignment(TableRowAlign.CENTER);
            List<String> list = new ArrayList<String>() {{
                add("ЖАБДУУЛАРДЫН КОДУ");
                add("ЖАБДУУЛАРДЫН АТАЛЫШЫ");
                add("ДААНА");
            }};

            int i = 0;
            for (String s : list) {
                XWPFParagraph p = table.getRow(0).getCell(i).getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                p.setVerticalAlignment(TextAlignment.CENTER);
                XWPFRun r = p.createRun();
                r.setBold(true);
                r.setText(s.toUpperCase());
                i++;
            }



            int n = 1;
            for (String tv : hardwareMap.keySet()) {
                XWPFParagraph p = table.getRow(n).getCell(0).getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                table.getRow(n).getCell(0).setText(hardwareMap.get(tv).get(0).getCode() == null ? "" : hardwareMap.get(tv).get(0).getCode()+"00"+n);

                XWPFParagraph p1 = table.getRow(n).getCell(1).getParagraphs().get(0);
                p1.setAlignment(ParagraphAlignment.CENTER);
                table.getRow(n).getCell(1).setText(tv == null ? "" : tv.toUpperCase());

                XWPFParagraph p2 = table.getRow(n).getCell(2).getParagraphs().get(0);
                p2.setAlignment(ParagraphAlignment.CENTER);
                table.getRow(n).getCell(2).setText(String.valueOf(hardwareMap.get(tv).size()));

                n++;
            }

            for(int x = 0;x < table.getNumberOfRows(); x++){
                XWPFTableRow row = table.getRow(x);
                int numberOfCell = row.getTableCells().size();
                for(int y = 0; y < numberOfCell ; y++){
                    XWPFTableCell cell = row.getCell(y);
                    cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2800));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
            }
        }

    }

    private void createFooter(){
        XWPFParagraph bottom1 = doc.createParagraph();
        bottom1.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph bottom2 = doc.createParagraph();
        bottom2.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph bottom3 = doc.createParagraph();
        bottom3.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph bottom4 = doc.createParagraph();
        bottom4.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph bottom5 = doc.createParagraph();
        bottom5.setAlignment(ParagraphAlignment.CENTER);

        XWPFParagraph bottom = doc.createParagraph();
        bottom.setAlignment(ParagraphAlignment.CENTER);
        bottom.setWordWrapped(true);
        XWPFRun runB = bottom.createRun();
        runB.setBold(true);
        runB.setColor("000000");
        runB.setText("Тапшырган: ..............................                                        Кабыл алган: ...................................  ");

    }
}
