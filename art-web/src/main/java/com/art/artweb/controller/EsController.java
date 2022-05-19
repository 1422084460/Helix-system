package com.art.artweb.controller;

import com.art.artsearch.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author lou
 * @create 2022/5/11
 */
@RestController
@RequestMapping("/api/es")
public class EsController {

    @Autowired
    private EsService service;
    @Autowired
    private ElasticsearchRestTemplate template;

    @RequestMapping("/test")
    public void aaaa(){
        //service.save();
        //service.find();
        //service.del();
        //service.importData();
        service.find();
    }
}
