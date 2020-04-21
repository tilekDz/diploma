package thesisproject.diploma.service;


import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.service.HardwareService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by asemenov on 29.01.2018.
 */
@Service
public class ReportDocxCreate {

    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");

    @Autowired
    private HardwareService hardwareService;

    XWPFDocument doc;

    public void createDocxFile(HttpServletResponse response, Long room, String campus){
        try {
            doc = new XWPFDocument();
            ServletOutputStream out = response.getOutputStream();
            createHeader(room, campus);
            createTable(room, campus);
            doc.write(out);
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createHeader(Long room, String campus){
        XWPFParagraph p1 = doc.createParagraph();
        p1.setAlignment(ParagraphAlignment.LEFT);
        p1.setWordWrapped(true);
        XWPFRun r1 = p1.createRun();
        r1.setBold(true);
        r1.setText("Report of inventory for auditory: ");
        p1.createRun().setText(room.toString());

        XWPFParagraph p2 = doc.createParagraph();
        p2.setAlignment(ParagraphAlignment.LEFT);
        p2.setWordWrapped(true);
        XWPFRun r2 = p2.createRun();
        r2.setBold(true);
        r2.setText("Report of inventory for campus:  ");
        p2.createRun().setText(campus);

        XWPFParagraph p6 = doc.createParagraph();
        p6.setAlignment(ParagraphAlignment.LEFT);
        p6.setWordWrapped(true);
        XWPFRun r6 = p6.createRun();
        r6.setBold(true);
        r6.setText("Create date of this report: ");
        p6.createRun().setText(new Date().toString());

        XWPFParagraph p7 = doc.createParagraph();
        p6.setAlignment(ParagraphAlignment.LEFT);
        p6.setWordWrapped(true);
        XWPFRun r7 = p7.createRun();
        r7.setBold(true);
    }

    public void createTable(Long room, String campus){
        List<Hardware> hardwareList = hardwareService.getAllByRoomNumberAndCampusAndDeletedFalse(room, campus);
        if(!hardwareList.isEmpty() && hardwareList != null) {
            XWPFTable table = doc.createTable(hardwareList.size()+1, 4);

            List<String> list = new ArrayList<String>() {{
                add("Title");
                add("Description");
                add("Type");
                add("Create Date");
            }};

            int i = 0;
            for (String s : list) {
                XWPFParagraph p = table.getRow(0).getCell(i).getParagraphs().get(0);
                p.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun r = p.createRun();
                r.setBold(true);
                r.setText(s);
                i++;
            }

            int n = 1;
            for (Hardware tv : hardwareList) {
                table.getRow(n).getCell(0).setText(tv.getName() == null ? "" : tv.getName());
                table.getRow(n).getCell(1).setText(tv.getDescription() == null ? "" : tv.getDescription());
                table.getRow(n).getCell(2).setText(tv.getType() == null ? "" : tv.getType());
                table.getRow(n).getCell(3).setText(tv.getCreatedDate() == null ? "" : format.format(tv.getCreatedDate()));
                n++;
            }
        }

    }
}
