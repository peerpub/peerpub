package de.fzj.peerpub.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/**
 * MongoBee configuration to execute database migrations when PeerPub is started
 */
@Configuration
@ComponentScan
public class MongobeeConfig extends AbstractMongoConfiguration {
  
  /**
   * MongoDB URI. Constructed from Spring Boot configuration values.
   */
  @Value("mongodb://${spring.data.mongodb.host}:${spring.data.mongodb.port}/${spring.data.mongodb.database}")
  private String uri;
  
  /**
   * MongoDB Database name. Taken from Spring Boot configuration value.
   */
  @Value("${spring.data.mongodb.database}")
  private String database;
  
  @Override
  public MongoClient mongoClient() {
    return new MongoClient(new MongoClientURI(uri));
  }
  
  @Override
  protected String getDatabaseName() {
    return this.database;
  }
  
  @Bean(name = "bee")
  public Mongobee mongobee() throws Exception {
    Mongobee runner = new Mongobee(uri);
    runner.setDbName(getDatabaseName());
    runner.setChangeLogsScanPackage("de.fzj.peerpub.config");
    runner.setMongoTemplate(mongoTemplate());
    return runner;
  }
}
