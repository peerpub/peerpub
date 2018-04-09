package de.fzj.peerpub.doc.doctype;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
public class DocTypeServiceTest {
  
  @Mock DocTypeRepository docTypeRepository;
  
  DocTypeService docTypeService;
  
  @BeforeEach
  void setup() {
    this.docTypeService = new DocTypeService(docTypeRepository);
  }
  
  @Test
  void getAll() {
    // given
    List<DocType> docTypes = DocTypeTest.generate(5);
    given(docTypeRepository.findAll()).willReturn(docTypes);
    
    // when
    List<DocType> get = docTypeService.getAll();
    
    // then
    assertEquals(docTypes, get);
  }
  
  @Test
  void getByName() {
    // given
    DocType dt = DocTypeTest.generate();
    given(docTypeRepository.findByName(dt.getName())).willReturn(Optional.of(dt));
    DocTypeForm dtf = DocTypeForm.toForm(dt);
    
    // when
    Optional<DocTypeForm> get = docTypeService.getByName(dt.getName());
    // then
    assertTrue(get.isPresent());
    assertEquals(dtf, get.get());
  }
  
  @Test
  void getByNameEmpty() {
    // given
    DocType dt = DocTypeTest.generate();
    given(docTypeRepository.findByName(dt.getName())).willReturn(Optional.empty());
    
    // when
    Optional<DocTypeForm> get = docTypeService.getByName(dt.getName());
    // then
    assertEquals(Optional.empty(), get);
  }
  
  @Test
  void saveEdit() {
    // given
    DocTypeForm dtf = DocTypeForm.toForm(DocTypeTest.generate());
    // when
    docTypeService.saveEdit(dtf);
    // then
    // NOOP - save operation just returns the saved object.
  }
  
}
