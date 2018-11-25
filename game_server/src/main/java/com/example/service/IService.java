package com.example.service;

import org.springframework.stereotype.Service;

@Service
public interface IService<T> {

    T selectByPrimaryKey(Object key);

    int insert(T entity);

    int delete(Object key);

    int update(T entity);

    int updateNotNull(T entity);
}
