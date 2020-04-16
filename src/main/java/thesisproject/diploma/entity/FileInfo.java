package thesisproject.diploma.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "file_info")
public class FileInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_info_seq")
    @SequenceGenerator(name = "file_info_seq", sequenceName = "file_info_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "type")
    private String fileType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file_data_id")
    private FileData fileData;

    public FileInfo() {
    }

    public FileInfo(String name, String description, String size, String fileType, FileData fileData) {
        this.name = name;
        this.description = description;
        this.fileSize = size;
        this.fileType = fileType;
        this.fileData = fileData;
    }
}
