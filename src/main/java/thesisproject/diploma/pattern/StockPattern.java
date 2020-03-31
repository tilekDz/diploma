package thesisproject.diploma.pattern;

import lombok.Data;

@Data
public class StockPattern {

    private Integer pageSize;
    private Integer page;
    private String name;
    private String description;
    private String type;
}
