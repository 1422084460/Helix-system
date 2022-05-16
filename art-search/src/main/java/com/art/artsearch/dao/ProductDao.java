package com.art.artsearch.dao;

import com.art.artsearch.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * description
 *
 * @author lou
 * @create 2022/5/12
 */
public interface ProductDao extends ElasticsearchRepository<Product, Integer> {
}
