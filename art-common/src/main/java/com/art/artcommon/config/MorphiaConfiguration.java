package com.art.artcommon.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.entity.base-package",havingValue = "com.art.artcommon.mongo")
public class MorphiaConfiguration {
    @Configuration
    @ConditionalOnProperty(prefix = "spring.data.mongodb", name = "database")
    public static class MongoConfiguration {
        @Resource
        private MongoProperties mongoProperties;
        @Value("${spring.data.mongodb.database}")
        private String database;
        @Value("${spring.data.mongodb.entity.base-package}")
        private String mongoEntityBasePackage;

        @Bean("mongoClient")
        public MongoClient mongoClient() {
            MongoClientOptions options = new MongoClientOptions.Builder().build();
            MongoClient mongoClient;
            if (mongoProperties.getUsername() != null && mongoProperties.getPassword() != null) {
                // 有密码连接
                MongoCredential credential = MongoCredential.createCredential(mongoProperties.getUsername(), mongoProperties.getDatabase(), mongoProperties.getPassword());
                mongoClient = new MongoClient(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()), credential, options);
            } else {
                // 无密码连接
                mongoClient = new MongoClient(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()));
            }
            return mongoClient;
        }

        @Bean("mongoDatabase")
        public MongoDatabase mongoDatabase(MongoClient mongoClient) {
            return mongoClient.getDatabase(database);
        }

        @Bean("mongoDataStore")
        @ConditionalOnMissingBean(name = "mongoDataStore")
        public Datastore mongoDataStore(MongoClient mongoClient) {
            Morphia morphia = new Morphia();
            morphia.mapPackage(mongoEntityBasePackage);
            Datastore datastore = morphia.createDatastore(mongoClient, database);
            datastore.ensureIndexes();
            return datastore;
        }
    }
}
