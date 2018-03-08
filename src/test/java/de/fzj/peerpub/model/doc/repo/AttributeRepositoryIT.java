package de.fzj.peerpub.model.doc.repo;

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

import com.mongodb.client.MongoCollection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import de.fzj.peerpub.model.doc.Attribute;
import de.fzj.peerpub.model.doc.repo.AttributeRepository;

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
    void uniqueIndexOnName() {
      assertNotNull(attributeRepository);
      Attribute a = new Attribute("a","a","A","A a attribute...","{}");
      Attribute b = new Attribute("a","b","B","A b attribute...","{}");
      attributeRepository.save(a);
      assertThrows(DuplicateKeyException.class, () -> {attributeRepository.save(b);});
    }

    @Test
    void findByName() {
      assertNotNull(attributeRepository);
      Attribute a = new Attribute("a","a","A","A a attribute...","{}");
      Attribute b = new Attribute("b","b","B","A b attribute...","{}");
      List all = Arrays.asList(a, b);
      attributeRepository.saveAll(all);
      assertEquals(a,attributeRepository.findByName("a"));
      assertNotEquals(a,attributeRepository.findByName("b"));
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
