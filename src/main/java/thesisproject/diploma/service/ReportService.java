package thesisproject.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Report;
import thesisproject.diploma.repository.ReportRepository;


@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Page<Report> findAll(Specification specification, Pageable pageable){
        return reportRepository.findAll(specification, pageable);
    }

    public void save(Report report){
        reportRepository.save(report);
    }

    public Report getById(Long id){
        return reportRepository.getOne(id);
    }
}
