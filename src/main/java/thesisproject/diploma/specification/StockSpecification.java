package thesisproject.diploma.specification;

import org.springframework.data.jpa.domain.Specification;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.pattern.StockPattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import static thesisproject.diploma.specification.SpecificatinHelper.getContainsLike;
public class StockSpecification implements Specification<Stock>{

    private final StockPattern criteria;

    public StockSpecification(StockPattern stockPattern){
        this.criteria = stockPattern;
    }

    @Override
    public Predicate toPredicate(Root<Stock> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
