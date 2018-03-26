package de.fzj.peerpub.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * MongoBee configuration to execute database migrations when PeerPub is started
 */
@Configuration
@ComponentScan
public class MongobeeConfig {
  
  /**
   * MongoDB Database name. Taken from Spring Boot configuration value.
   */
  @Value("${spring.data.mongodb.database}")
  private String database;
  
  /**
   * MongoDB client, autowired in to allow Embedded MongoDB with random
   * port to kick in.
   */
  @Autowired
  private MongoClient mongoClient;
  /**
   * MongoDB template, autowired in to allow using Embedded MongoDB with
   * the existing client connection.
   */
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Bean
  public Mongobee mongobee() throws Exception {
    Mongobee runner = new Mongobee(mongoClient);
    runner.setDbName(this.database);
    runner.setChangeLogsScanPackage("de.fzj.peerpub.config");
    runner.setMongoTemplate(mongoTemplate);
    return runner;
  }
}
