package thesisproject.diploma.specification;

import org.springframework.data.jpa.domain.Specification;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.pattern.HardwarePattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

import static thesisproject.diploma.specification.SpecificatinHelper.getContainsLike;
public class HardwareSpecification implements Specification<Hardware> {

    private HardwarePattern criteria;

    public HardwareSpecification(HardwarePattern hardwarePattern){
        this.criteria = hardwarePattern;
    }
    @Override
    public Predicate toPredicate(Root<Hardware> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isFalse(root.<Boolean>get("isDeleted")));
        if(criteria.getName() != null && !criteria.getName().isEmpty()){
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("name")),
                    getContainsLike(criteria.getName())));
        }
        if(criteria.getDescription() != null && !criteria.getDescription().isEmpty()){
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("description")),
                    getContainsLike(criteria.getDescription())));
        }
        if(criteria.getType() != null && !criteria.getType().isEmpty()){
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("type")),
                    getContainsLike(criteria.getType())));
        }
        if(criteria.getCampusBlock() != null && !criteria.getCampusBlock().isEmpty()){
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("campusBlock")),
                    getContainsLike(criteria.getCampusBlock())));
        }
        if(criteria.getRoomNumber() != null && criteria.getRoomNumber() != 0){
            predicates.add(criteriaBuilder.equal(root.<Long>get("roomNumber"), criteria.getRoomNumber()));
        }
        if(criteria.getDate() != null && !criteria.getDate().equals(null)){
            predicates.add(criteriaBuilder.equal(root.<Long>get("createdDate"), criteria.getDate()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
