package com.art.artsearch.dao;

import com.art.artsearch.document.Use;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * description
 *
 * @author lou
 * @create 2022/5/13
 */
public interface UserDao extends ElasticsearchRepository<Use,Integer> {
}
