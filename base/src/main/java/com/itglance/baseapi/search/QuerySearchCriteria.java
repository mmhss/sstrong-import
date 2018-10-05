package com.itglance.baseapi.search;

import lombok.Data;

@Data
class QuerySearchCriteria {

    private final String key;

    private final String operation;

    private final Object value;

    QuerySearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
}