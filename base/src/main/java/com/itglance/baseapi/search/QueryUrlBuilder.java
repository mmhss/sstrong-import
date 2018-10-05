package com.itglance.baseapi.search;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QueryUrlBuilder {
    private final Class classType;
    private final EntityPathBase entityPathBase;

    private List<QuerySearchCriteria> params;

    private QueryUrlBuilder(Class parameterizedClass, EntityPathBase entityPathBase) {
        params = new ArrayList<>();
        this.classType = parameterizedClass;
        this.entityPathBase = entityPathBase;

    }

    public static QueryUrlBuilder forClass(Class parameterizedClass, EntityPathBase entityPathBase) {
        return new QueryUrlBuilder(parameterizedClass, entityPathBase);
    }

    public QueryUrlBuilder with(
            String key, String operation, Object value) {
        params.add(new QuerySearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression buildSearch() {
        if (params.isEmpty()) {
            return null;
        }
        List<BooleanExpression> predicates = new ArrayList<>();
        for (QuerySearchCriteria param : params) {
            Optional<BooleanExpression> exp = QueryUrl.forClass(classType, entityPathBase).getPredicate(param);
            exp.ifPresent(predicates::add);
        }

        Optional<BooleanExpression> expression = predicates.stream().reduce(BooleanExpression::and);

        return expression.get();
    }

    public List<OrderSpecifier> buildSort() {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        if (params.isEmpty()) {
            return orderSpecifiers;
        }
        for (QuerySearchCriteria param : params) {
            Optional<OrderSpecifier> exp = QueryUrl.forClass(classType, entityPathBase).getOrderSpecifier(param);
            exp.ifPresent(orderSpecifiers::add);
        }
        return orderSpecifiers;
    }
}