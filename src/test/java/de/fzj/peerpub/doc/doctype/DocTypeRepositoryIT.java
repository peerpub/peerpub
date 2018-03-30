package de.fzj.peerpub.doc.doctype;

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

import de.fzj.peerpub.doc.attribute.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
@Tag("integration-embedded")
public class DocTypeRepositoryIT {

    @Autowired
    private AttributeRepository attrRepository;

    @Autowired
    private DocTypeRepository docTypeRepository;

    @BeforeEach
    void emptyDatabase() {
      assertNotNull(attrRepository);
      assertNotNull(docTypeRepository);
      attrRepository.deleteAll();
      docTypeRepository.deleteAll();
    }

    @Test
    void saveFindCompare() {
      assertNotNull(attrRepository);
      assertNotNull(docTypeRepository);

      List<Attribute> attrs = AttributeTest.generate(2);
      DocType dt = DocTypeTest.generate(attrs);
      // this is needed as we delete anything in MetadataAttributes before each test...
      attrRepository.saveAll(attrs);
      docTypeRepository.save(dt);

      /*
      MongoCollection<Document> c = mongoops.getCollection(mongoops.getCollectionName(DocType.class));
      for (Document d : c.find())
        System.out.println(d);
      */

      String dtName = dt.getName();
      Optional<DocType> odt2 = docTypeRepository.findByName(dtName);
      assertTrue(dt.equalsDeep(odt2.get()));
    }
}
