package com.home;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class StorageNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorageNodeApplication.class, args);
	}

    @Bean(destroyMethod = "shutdown")
	public HazelcastInstance customersStorageNode(
        @Qualifier("StorageNodeConfig")
        Config config)
        throws Exception{

        return Hazelcast.newHazelcastInstance(config);
    }

	@Bean(name = "StorageNodeConfig")
	public Config config(CustomersMapStore customerMapStore){
        Config config = new Config();

        MapConfig mapConfig = new MapConfig();

        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(customerMapStore);

        mapConfig.setMapStoreConfig(mapStoreConfig);
        mapConfig.setName("customers");

        config.addMapConfig(mapConfig);

        return config;
    }
}
