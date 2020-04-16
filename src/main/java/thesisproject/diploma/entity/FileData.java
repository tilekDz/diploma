package thesisproject.diploma.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "file_data")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_data_seq")
    @SequenceGenerator(name = "file_data_seq", sequenceName = "file_data_seq", allocationSize = 1)
    private Long id;

    @Column(name = "content")
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private byte[] content;

    public FileData(){}

    public FileData(byte[] content){
        this.content = content;
    }
}

