package de.fzj.peerpub.doc.attribute;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.bson.Document;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.fzj.peerpub.doc.attribute.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DataMongoTest
@Tag("integration-embedded")
public class AttributeRepositoryIT {

    @Autowired
    private AttributeRepository attributeRepository;

    @BeforeEach
    void emptyDatabase() {
      assertNotNull(attributeRepository);
      attributeRepository.deleteAll();
    }

    @Test
    void findByName() {
      assertNotNull(attributeRepository);
      Attribute a = AttributeTest.generate();
      Attribute b = AttributeTest.generate();
      attributeRepository.saveAll(Arrays.asList(a, b));
      assertEquals(Optional.of(a),attributeRepository.findByName(a.getName()));
      assertNotEquals(Optional.of(a),attributeRepository.findByName(b.getName()));
    }

    @Test
    void findByLabel() {
      assertNotNull(attributeRepository);
      Attribute a = new Attribute("a","a","A","A a attribute...","{}");
      Attribute b = new Attribute("b","b","B","A b attribute...","{}");
      Attribute c = new Attribute("c","c","B","A c attribute...","{}");
      List all = Arrays.asList(a, b, c);
      List aOnly = Arrays.asList(a);
      List bOnly = Arrays.asList(b, c);
      attributeRepository.saveAll(all);
      assertEquals(aOnly,attributeRepository.findByLabel("A"));
      assertEquals(bOnly,attributeRepository.findByLabel("B"));
    }
}
