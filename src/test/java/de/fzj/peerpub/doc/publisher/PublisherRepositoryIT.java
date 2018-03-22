package de.fzj.peerpub.doc.publisher;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.beans.factory.annotation.Autowired;

import de.fzj.peerpub.doc.doctype.*;
import de.fzj.peerpub.doc.attribute.*;

import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DataMongoTest
@ActiveProfiles("test")
@Tag("integration-embedded")
public class PublisherRepositoryIT {

    @Autowired
    private AttributeRepository attrRepository;
    @Autowired
    private DocTypeRepository docTypeRepository;
    @Autowired
    private PublisherRepository pubRepository;

    @BeforeEach
    void emptyDatabase() {
      assertNotNull(attrRepository);
      assertNotNull(docTypeRepository);
      assertNotNull(pubRepository);
      attrRepository.deleteAll();
      docTypeRepository.deleteAll();
      pubRepository.deleteAll();
    }


    @Test
    @DisplayName("Save a Publisher to the database and read it back")
    void saveAndFindByName() {
      List<DocType> dts = DocTypeTest.generate(4);
      Publisher pub = PublisherTest.generate(dts);

      pubRepository.save(pub);
      assertTrue(pub.equalsDeep(pubRepository.findByName(pub.getName())));
    }

    @Test
    @DisplayName("Receive a set of publishers supporting a certain doc type")
    void getBySupports() {
      List<DocType> dts = DocTypeTest.generate(4);
      List<Publisher> pubs = PublisherTest.generate(5, dts);

      // add a special doc type to only two of the publishers
      List<Publisher> pubsSup = pubs.subList(0,2);
      DocType specialDt = DocTypeTest.generate();
      for(Publisher p : pubsSup)
        p.addSupDocType(specialDt);
      pubRepository.saveAll(pubs);

      assertEquals(new HashSet(pubsSup), pubRepository.findBySupports(specialDt.getName()));
    }

    @Test
    @DisplayName("Receive a set of publishers matching some search string with their names and aliases")
    void getByNameOrAliasSearch() {
      List<DocType> dts = DocTypeTest.generate(4);
      List<Publisher> pubs = PublisherTest.generate(5, dts);
      List<String> displayNames = Arrays.asList("Journal of XYZ", "Nature", "Science", "Journal of Applied ZZZ", "Journal of WackaWacka");
      for(int i = 0; i < pubs.size(); i++) {
        pubs.get(i).setDisplayName(displayNames.get(i));
      }
      pubs.get(1).addAlias("Journal of Nature");
      pubRepository.saveAll(pubs);

      // basic search
      TextCriteria search = TextCriteria.forDefaultLanguage().matching("Journal");
      HashSet<Publisher> journal = new HashSet<Publisher>(Arrays.asList(pubs.get(0),pubs.get(3),pubs.get(4),pubs.get(1)));
      assertEquals(journal, pubRepository.findAllBy(search));

      // search with stemmed words
      search = TextCriteria.forDefaultLanguage().matching("Journals");
      assertEquals(journal, pubRepository.findAllBy(search));

      // search via regex (slower than fulltext index but supports partial strings)
      assertEquals(journal, pubRepository.findByDisplayStartsWith("Journ"));
    }

    /*
    @Test
    void saveFindCompare() {
      MongoCollection<Document> c = mongoops.getCollection(mongoops.getCollectionName(Publisher.class));
      for (Document d : c.find())
        System.out.println(d);
      for (Document d : c.listIndexes())
        System.out.println(d);
    }
    */
}
