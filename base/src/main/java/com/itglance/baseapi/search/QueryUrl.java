package com.itglance.baseapi.search;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import java.util.Optional;

class QueryUrl<T extends EntityPathBase> {
    private static final String STARTS_WITH_OPERATOR = "~";
    private static final String EQUALS_OPERATOR = "=";
    private static final String ORDER_BY_ASCENDING = "asc";
    private static final String ORDER_BY_DESCENDING = "desc";

    private final Class<T> classType;
    private final PathMetadata metaData;

    private QueryUrl(Class parameterizedClass, EntityPathBase entityPathBase) {
        this.metaData = entityPathBase.getMetadata();
        this.classType = parameterizedClass;
    }

    static QueryUrl forClass(Class parameterizedClass, EntityPathBase entityPathBase) {
        return new QueryUrl(parameterizedClass, entityPathBase);
    }

    Optional<BooleanExpression> getPredicate(QuerySearchCriteria criteria) {

        PathBuilder<T> entityPath = new PathBuilder<>(classType, metaData);


            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(EQUALS_OPERATOR)) {
                return Optional.of(path.eq(criteria.getValue().toString()));
            }
            if (criteria.getOperation().equalsIgnoreCase(STARTS_WITH_OPERATOR)) {
                return Optional.of(path.startsWith(criteria.getValue().toString()));
            }

        return Optional.empty();
    }

    Optional<OrderSpecifier> getOrderSpecifier(QuerySearchCriteria criteria) {
        PathBuilder<T> entityPath = new PathBuilder<>(classType, metaData);
        StringPath path = entityPath.getString(criteria.getKey());
        switch (criteria.getValue().toString()) {
            case ORDER_BY_ASCENDING:
                return Optional.of(path.asc());
            case ORDER_BY_DESCENDING:
                return Optional.of(path.desc());
        }
        return Optional.empty();
    }

}