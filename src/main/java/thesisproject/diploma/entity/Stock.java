package thesisproject.diploma.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Entity(name = "STOCK_DIPLOMA")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "HARDWARE_NAME")
    private String name;

    @Column(name = "HARDWARE_DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "HARDWARE_QUANTITY")
    private long quantity;
}
