package thesisproject.diploma.pattern;

import lombok.Data;

import java.util.Date;

@Data
public class HardwarePattern {

    private String name;
    private String description;
    private String type;
    private String campusBlock;
    private Long roomNumber;
    private Date date;
}
