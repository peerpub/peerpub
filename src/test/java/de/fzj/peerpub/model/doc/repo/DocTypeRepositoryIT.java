package de.fzj.peerpub.model.doc.repo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import org.bson.Document;
import com.mongodb.client.MongoCollection;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.beans.factory.annotation.Autowired;

import de.fzj.peerpub.model.doc.DocType;
import de.fzj.peerpub.model.doc.repo.DocTypeRepository;
import de.fzj.peerpub.model.doc.Attribute;
import de.fzj.peerpub.model.doc.repo.AttributeRepository;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DataMongoTest
@Tag("integration-embedded")
public class DocTypeRepositoryIT {

    String collectionName;
    DocType doctype;

    @Autowired
    private AttributeRepository attrRepository;

    @Autowired
    private DocTypeRepository docTypeRepository;

    @Autowired
    private MongoOperations mongoops;

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

      Attribute a = new Attribute("a","a","A","An a desc...","{}");
      attrRepository.save(a);

      Map<String,Boolean> attribs = new HashMap<String,Boolean>();
      Map<String,String> defs = new HashMap<String,String>();
      attribs.put(a.getName(),true);
      defs.put(a.getName(),"value");
      DocType dt = new DocType("test",true,true,Arrays.asList(a),attribs,defs);

      docTypeRepository.save(dt);

      /*
      MongoCollection<Document> c = mongoops.getCollection(mongoops.getCollectionName(DocType.class));
      for (Document d : c.find())
        System.out.println(d);
      */

      DocType dt2 = docTypeRepository.findByName("test");
      assertEquals(dt,dt2);
      assertEquals(dt.getName(),dt2.getName());
      assertEquals(dt.getSystem(),dt2.getSystem());
      assertEquals(dt.getMultidoc(),dt2.getMultidoc());
      assertEquals(dt.getAttributes(),dt2.getAttributes());
      assertEquals(dt.getMandatory(),dt2.getMandatory());
      assertEquals(dt.getDefaults(),dt2.getDefaults());
    }
}
