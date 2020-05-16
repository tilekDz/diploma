package thesisproject.diploma.pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportPattern {

    private String campusBlock;
    private Long roomNumber;
    private Date date;
}
