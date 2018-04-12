package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeRepository;
import de.fzj.peerpub.doc.attribute.AttributeTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
public class DocTypeFormValidatorTest {
  
  @Mock
  AttributeRepository attributeRepository;
  @Mock
  DocTypeService docTypeService;
  
  DocTypeFormValidator docTypeFormValidator;
  
  DocTypeForm valid;
  Errors errors;
  
  @BeforeEach
  void setup() {
    this.docTypeFormValidator = new DocTypeFormValidator(attributeRepository, docTypeService);
    
    List<Attribute> attrs = AttributeTest.generate(2);
    doReturn(Optional.of(attrs.get(0))).when(attributeRepository).findByName(attrs.get(0).getName());
    doReturn(Optional.of(attrs.get(1))).when(attributeRepository).findByName(attrs.get(1).getName());
    
    // create non-system type
    this.valid = DocTypeForm.toForm(DocTypeTest.generate(attrs, 2));
    this.valid.setSystem(false);
    this.valid = DocTypeFormTest.adaptToViewValues(this.valid);
    
    this.errors = new BeanPropertyBindingResult(this.valid, "valid");
  }
  
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void supports() {
    assertFalse(this.docTypeFormValidator.supports(Object.class));
    assertTrue(this.docTypeFormValidator.supports(DocTypeForm.class));
  }
  
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void validateValidWithoutAttributes() {
    List<Attribute> attrs = AttributeTest.generate(2);
    DocTypeForm test = DocTypeForm.toForm(DocTypeTest.generate(attrs, 0));
    test.setSystem(false);
    Errors errors = new BeanPropertyBindingResult(test, "valid");
    
    this.docTypeFormValidator.validate(test, errors);
    assertFalse(errors.hasErrors());
  }
  
  @Test
  void validateValid() {
    this.docTypeFormValidator.validate(this.valid, errors);
    assertFalse(errors.hasErrors());
  }
  
  @Test
  void validateDoesNotAlterObject() {
    // given
    DocTypeForm original = DocTypeFormTest.clone(this.valid);
    // when
    this.docTypeFormValidator.validate(this.valid, errors);
    // then
    assertEquals(original, this.valid);
  }
  
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void validateNull() {
    this.docTypeFormValidator.validate(null, errors);
    assertTrue(errors.hasErrors());
    assertTrue(errors.getGlobalError().getCode().matches("doctype.form.empty"));
  }
  
  @Test
  void validateAddNewSystemType() {
    // given
    DocTypeForm invalid = valid;
    invalid.setSystem(true);
    given(docTypeService.getByName(invalid.getName())).willReturn(Optional.empty());
  
    // when
    this.docTypeFormValidator.validate(invalid, errors);
  
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("system").getCode().matches("doctype.system.cannotAdd"));
  }
  
  @Test
  void validateSystemTypeEditChangesSystemProperty() {
    // given
    DocTypeForm original = DocTypeFormTest.clone(this.valid);
    original.setSystem(true);
    DocTypeForm invalid = valid;
    invalid.setSystem(false);
    given(docTypeService.getByName(invalid.getName())).willReturn(Optional.of(original));
  
    // when
    this.docTypeFormValidator.validate(invalid, errors);
  
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("system").getCode().matches("doctype.system.cannotChange"));
  }
  
  @Test
  void validateSystemTypeEditChangesDisplayName() {
    // given
    DocTypeForm original = DocTypeFormTest.clone(this.valid);
    original.setSystem(true);
    DocTypeForm invalid = valid;
    invalid.setSystem(true);
    invalid.setDisplayName("invalid");
    given(docTypeService.getByName(invalid.getName())).willReturn(Optional.of(original));
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("displayName").getCode().matches("doctype.system.cannotChangeDisplay"));
  }
  
  @Test
  void validateSystemTypeEditChangesMultiDoc() {
    // given
    DocTypeForm original = DocTypeFormTest.clone(this.valid);
    original.setSystem(true);
    original.setMultiDoc(true);
    
    DocTypeForm invalid = valid;
    invalid.setSystem(true);
    invalid.setMultiDoc(false);
    
    assertFalse(original.equals(invalid));
    
    given(docTypeService.getByName(invalid.getName())).willReturn(Optional.of(original));
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("multiDoc").getCode().matches("doctype.system.cannotChangeMulti"));
  }
  
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void validateNonExistantAttribute() {
    // given
    DocTypeForm invalid = valid;
    invalid.getAttributes().add("test");
    given(attributeRepository.findByName("test")).willReturn(Optional.empty());
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("attributes").getCode().matches("doctype.attribute.notexist"));
  }
  
  @Test
  @DisplayName("mandatory does not contain all attributes")
  void validateNotMatchingAttributeMandatoryMissing() {
    // given
    DocTypeForm invalid = valid;
    invalid.getMandatory().remove(invalid.getAttributes().get(0));
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("mandatory").getCode().matches("doctype.mandatory.mismatch"));
  }
  
  @Test
  @DisplayName("mandatory contains more elements than attributes exist")
  void validateNotMatchingAttributeMandatoryBigger() {
    // given
    DocTypeForm invalid = valid;
    invalid.getMandatory().put("test",true);
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("mandatory").getCode().matches("doctype.mandatory.mismatch"));
  }
  
  @Test
  void validateInvalidMandatoryStatus() {
    // given
    DocTypeForm invalid = valid;
    invalid.getMandatory().put(invalid.getAttributes().get(0),false);
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("mandatory").getCode().matches("doctype.mandatory.invalid"));
  }
  
  @Test
  @DisplayName("defaults contains more elements than attributes exist")
  void validateNotMatchingAttributeDefaultsBigger() {
    // given
    DocTypeForm invalid = valid;
    invalid.getDefaults().put("test","test");
    
    // when
    this.docTypeFormValidator.validate(invalid, errors);
    
    // then
    assertTrue(errors.hasErrors());
    assertTrue(errors.getFieldError("defaults").getCode().matches("doctype.default.mismatch"));
  }
  
}
