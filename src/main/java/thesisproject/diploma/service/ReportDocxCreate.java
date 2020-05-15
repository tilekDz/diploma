package thesisproject.diploma.service;


import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.service.HardwareService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
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

    XWPFDocument doc;

    public void createDocxFile(HttpServletResponse response, Long room, String campus, String date, String paperNum){
        try {
            doc = new XWPFDocument();
            ServletOutputStream out = response.getOutputStream();
            createHeader(room, campus, date, paperNum);
            createTable(room, campus);
            doc.write(out);
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createHeader(Long room, String campus, String date, String paperNum){
        XWPFTable table = doc.createTable(1, 1);
        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.CENTER);
        p1.setWordWrapped(true);
        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setText("КЕҢСЕ ЭМЕРЕКТЕРИНИН ЭСЕБИ");
        r1.setFontSize(20);
        table.setTableAlignment(TableRowAlign.CENTER);
        table.setWidth(7000);
        table.setCellMargins(5,5,5,5);

        XWPFParagraph p11 = doc.createParagraph();
        p11.setAlignment(ParagraphAlignment.LEFT);
        p11.setWordWrapped(true);

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
        table2.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3500));


        XWPFParagraph p6 = table2.getRow(0).getCell(1).getParagraphs().get(0);
        p6.setAlignment(ParagraphAlignment.CENTER);
        p6.setWordWrapped(true);
        XWPFRun r6 = p6.createRun();
        r6.setBold(true);
        r6.setText("Текшерилген күнү/мезгили: ");
        p6.createRun().setText(date);
        table2.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3500));


        XWPFParagraph p7 = table2.getRow(1).getCell(0).getParagraphs().get(0);
        p7.setAlignment(ParagraphAlignment.CENTER);
        p7.setWordWrapped(true);
        XWPFRun r7 = p7.createRun();
        r7.setBold(true);
        r7.setText("Чыгарылган күнү: ");
        p7.createRun().setText(new Date().getDay()+ "-"+ new Date().getMonth() +"-"+new Date().getYear());
        table2.getRow(1).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3500));


        XWPFParagraph p8 = table2.getRow(1).getCell(1).getParagraphs().get(0);
        p8.setAlignment(ParagraphAlignment.CENTER);
        p8.setWordWrapped(true);
        XWPFRun r8 = p8.createRun();
        r8.setBold(true);
        r8.setText("Баракчанын номери: ");
        p8.createRun().setText(paperNum);
        table2.getRow(1).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3500));

        XWPFParagraph p10 = doc.createParagraph();
        p10.setAlignment(ParagraphAlignment.LEFT);
        p10.setWordWrapped(true);
        XWPFRun r10 = p10.createRun();
        r10.setBold(true);
        r10.setText("Иш кабинети: ");
        p10.createRun().setText(campus +" "+room);
    }

    public void createTable(Long room, String campus){
        List<Hardware> hardwareList = hardwareService.getAllByRoomNumberAndCampusAndDeletedFalse(room, campus);
        if(!hardwareList.isEmpty() && hardwareList != null) {
            Map<String, List<Hardware>> hardwareMap = hardwareList.stream().collect(Collectors.groupingBy(Hardware::getName));

            XWPFTable table = doc.createTable(hardwareMap.size()+1, 3);

            List<String> list = new ArrayList<String>() {{
                add("Title");
                add("Type");
                add("Quantity");
            }};

            int i = 0;
            for (String s : list) {
                XWPFParagraph p = table.getRow(0).getCell(i).getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                p.setVerticalAlignment(TextAlignment.CENTER);
                XWPFRun r = p.createRun();
                r.setBold(true);
                r.setText(s);
                i++;
            }



            int n = 1;
            for (String tv : hardwareMap.keySet()) {
                XWPFParagraph p = table.getRow(n).getCell(0).getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                table.getRow(n).getCell(0).setText(tv == null ? "" : tv);

                XWPFParagraph p1 = table.getRow(n).getCell(1).getParagraphs().get(0);
                p1.setAlignment(ParagraphAlignment.CENTER);
                table.getRow(n).getCell(1).setText(hardwareMap.get(tv).get(0).getType() == null ? "" : hardwareMap.get(tv).get(0).getType());

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
                    cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3000));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
            }
        }

    }
}
