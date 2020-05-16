package thesisproject.diploma.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "REPORT_DIPLOMA")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CAMPUS_BLOCK")
    private String campusBlock;

    @Column(name = "ROOM_NUMBER")
    private Long roomNumber;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "FILE_TEMPLATE_INFO_ID")
    private FileInfo fileTemplate;
}
