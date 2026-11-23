package vn.hoidanit.laptopshop.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;

import java.util.List;

@Component
public class ProductSpecs {
    public Specification<Product> matchList(List<String> list, String field) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(field)).value(list));
    }

    public Specification<Product> matchPrice(double min, double max) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Product_.PRICE), min, max));
    }
}
