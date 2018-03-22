package de.fzj.peerpub.config;

import com.mongodb.client.MongoCollection;
import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeRepository;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DataMongoTest
@ActiveProfiles("test")
@Tag("integration-embedded")
public class MongoChangelogIT {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private AttributeRepository attributeRepository;
  
  private MongoChangelog change = new MongoChangelog();
  
  @Test
  void change001_importData() throws Exception {
    // given
    // when
    change.importInitialDataSet(mongoTemplate);
    
    // then
    List<Attribute> attrs = attributeRepository.findAll();
    assertTrue(attrs.size() > 0);
  }

}
