package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeRepository;
import de.fzj.peerpub.doc.attribute.AttributeTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
@Tag("integration-embedded")
public class DocTypeServiceIT {

    @Autowired
    private DocTypeRepository docTypeRepository;
  
    DocTypeService docTypeService;
    
    @BeforeEach
    void setup() {
      this.docTypeService = new DocTypeService(docTypeRepository);
      assertNotNull(docTypeRepository);
      docTypeRepository.deleteAll();
    }

    @Test
    void findEditSaveCompare() {
      // given
      DocType dt = DocTypeTest.generate();
      docTypeRepository.save(dt);
      DocTypeForm original = docTypeService.getByName(dt.getName()).get();
      
      // when
      dt.putAttribute("test", false, "");
      DocTypeForm edited = DocTypeForm.toForm(dt);
      docTypeService.saveEdit(edited);
      
      DocTypeForm read = docTypeService.getByName(dt.getName()).get();
      
      // then
      assertFalse(original.equals(edited));
      assertEquals(1, docTypeService.getAll().size());
      assertEquals(read.getAttributes(), edited.getAttributes());
    }
  
    @Test
    void deleteById() {
      // given
      DocType dt = DocTypeTest.generate();
      dt.setSystem(false);
      docTypeRepository.save(dt);
      
      // when
      docTypeService.deleteById(dt.getName());
      
      // then
      assertEquals(Optional.empty(), docTypeService.getByName(dt.getName()));
    }
    
    @Test
    void deleteByIdFailsSystemType() {
      // given
      DocType dt = DocTypeTest.generate();
      dt.setSystem(true);
      docTypeRepository.save(dt);
  
      // when
      assertThrows(IllegalArgumentException.class, () -> docTypeService.deleteById(dt.getName()));
    }
}
