package thesisproject.diploma.specification;

import org.springframework.data.jpa.domain.Specification;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Report;
import thesisproject.diploma.pattern.ReportPattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

import static thesisproject.diploma.specification.SpecificatinHelper.getContainsLike;
public class ReportSpecification implements Specification<Report> {

    private ReportPattern criteria;

    public ReportSpecification(ReportPattern reportPattern){
        this.criteria = reportPattern;
    }

    @Override
    public Predicate toPredicate(Root<Report> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();

        if(criteria.getCampusBlock() != null && !criteria.getCampusBlock().isEmpty()){
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("campusBlock")),
                    getContainsLike(criteria.getCampusBlock())));
        }
        if(criteria.getRoomNumber() != null && criteria.getRoomNumber() != 0){
            predicates.add(criteriaBuilder.equal(root.<Long>get("roomNumber"), criteria.getRoomNumber()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
