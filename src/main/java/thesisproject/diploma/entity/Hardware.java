package thesisproject.diploma.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "HARDWARE_TYPE")
    private String type;

    @Column(name = "HARDWARE_DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "CAMPUS_BLOCK")
    private String campusBlock;

    @Column(name = "ROOM_NUMBER")
    private Long roomNumber;

    @Column(name = "IS_DELETED")
    private Boolean isDeleted;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    public Hardware(String name, String description, String campusBlock, String type, Long roomNumber, Boolean isDeleted){
        this.name= name;
        this.type = type;
        this.description = description;
        this.campusBlock = campusBlock;
        this.roomNumber = roomNumber;
        this.isDeleted = isDeleted;
        this.createdDate = new Date();
    }
}
