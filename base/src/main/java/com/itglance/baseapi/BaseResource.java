package com.itglance.baseapi;

import com.itglance.baseapi.mapper.BaseMapper;
import com.itglance.baseapi.search.QueryUrlBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class BaseResource<T extends BaseEntity, D extends BaseDto> {

    public static final String BASE_URL = "/api";

    private final BaseService<T> service;

    private final BaseMapper<D, T> mapper;

    private final static Pattern queryUrlSearchPattern = Pattern.compile("(\\w+?)(=|<|>|~)([\\w.]+?),");

    private final static Pattern queryUrlSortByPattern = Pattern.compile("([\\w.]+?)(:)(\\w+?),");

    private final Class<T> classType;

    private final EntityPathBase entityPathBase;

    @PostMapping
    public ResponseEntity<D> create(@RequestBody D dto) throws URISyntaxException {
        if (dto.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        T entity = mapper.toEntity(dto);
        entity = service.save(entity);
        D newDto = mapper.toDto(entity);
        return ResponseEntity.created(new URI(BASE_URL + "/" + newDto.getId())).body(newDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<D> update(@RequestBody D dto, @PathVariable Long id) {
        if (dto.getId() == null || !dto.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        T entity = mapper.toEntity(dto);
        entity = service.update(entity);
        D newDto = mapper.toDto(entity);
        return ResponseEntity.ok().body(newDto);
    }

    @GetMapping
    public ResponseEntity<Iterable<D>> getAll(@RequestParam(required = false) String search, @RequestParam(required = false) String sortBy) {
        QueryUrlBuilder builderSearch = QueryUrlBuilder.forClass(classType, entityPathBase);
        QueryUrlBuilder builderSort = QueryUrlBuilder.forClass(classType, entityPathBase);
        if (search != null) {
            Matcher matcher = queryUrlSearchPattern.matcher(search + ",");
            while (matcher.find()) {
                builderSearch.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        if (sortBy != null) {
            Matcher matcher = queryUrlSortByPattern.matcher(sortBy + ",");
            while (matcher.find()) {
                builderSort.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        BooleanExpression exp = builderSearch.buildSearch();
        List<OrderSpecifier> orderBy = builderSort.buildSort();
        Iterable<T> results = service.findAll(exp, orderBy.toArray(new OrderSpecifier[orderBy.size()]));
        List<D> dtos = mapper.toDto(results);
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> get(@PathVariable Long id) {
        Optional<T> result = service.findOne(id);
        return result.map(r -> ResponseEntity.ok().body(mapper.toDto(r))).
                orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}

