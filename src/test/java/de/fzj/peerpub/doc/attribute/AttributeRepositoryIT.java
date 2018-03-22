package de.fzj.peerpub.doc.attribute;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DataMongoTest
@ActiveProfiles("test")
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
      Attribute a = new Attribute("a","a","A","A a attribute...");
      Attribute b = new Attribute("b","b","B","A b attribute...");
      Attribute c = new Attribute("c","c","B","A c attribute...");
      List all = Arrays.asList(a, b, c);
      List aOnly = Arrays.asList(a);
      List bOnly = Arrays.asList(b, c);
      attributeRepository.saveAll(all);
      assertEquals(aOnly,attributeRepository.findByLabel("A"));
      assertEquals(bOnly,attributeRepository.findByLabel("B"));
    }
}
