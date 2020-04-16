package thesisproject.diploma.pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HardwarePattern {

    private String name;
    private String description;
    private String type;
    private String campusBlock;
    private Long roomNumber;
    private Date date;
}
