package com.art.artservice.handler;

import org.springframework.stereotype.Component;

//@Component
public interface Handler {

    void handler(String queue, String JsonEntity);
}
