package com.home.factory;

import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Amarendra Kumar on 10/10/2016.
 */
@Configuration
public class StorageNodeConfig{

    @Bean(name = "StorageNodeConfig")
    public Config config(){
        return new Config();
    }
}
