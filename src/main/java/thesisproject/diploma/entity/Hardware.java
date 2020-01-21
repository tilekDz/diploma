package thesisproject.diploma.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "HARDWARE_DIPLOMA")
public class Hardware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HARDWARE_NAME")
    private String name;

    @Column(name = "HARDWARE_DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "CAMPUS_BLOCK")
    private String campusBlock;

    @Column(name = "ROOM_NUMBER")
    private Long roomNumber;
}
