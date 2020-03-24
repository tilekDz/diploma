package thesisproject.diploma.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "STOCK_DIPLOMA")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HARDWARE_NAME")
    private String name;

    @Column(name = "HARDWARE_TYPE")
    private String type;

    @Column(name = "HARDWARE_DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "HARDWARE_QUANTITY")
    private Long quantity;
}
