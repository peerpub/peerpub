package de.fzj.peerpub.doc.validator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import de.fzj.peerpub.doc.validator.Referable;
import de.fzj.peerpub.doc.validator.ReferableValidator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag("fast")
public class ReferableValidatorTest {

  @Referable
  public String test = "";

  @Test
  void validate() throws Exception {
    // given
    // get annotation via reflection
    Field annotated = this.getClass().getField("test");
    Referable annotation = annotated.getAnnotation(Referable.class);
    String valid = "test_ABC-12";
    String invalid = "test_A-B-C +12/de";
    ReferableValidator validator = new ReferableValidator();
    validator.initialize(annotation);

    // when & then
    assertTrue(validator.isValid(valid, null));
    assertFalse(validator.isValid(invalid, null));
    assertFalse(validator.isValid(null, null));
  }
  
  @Test
  void validateList() throws Exception {
    // given
    // get annotation via reflection
    Field annotated = this.getClass().getField("test");
    Referable annotation = annotated.getAnnotation(Referable.class);
    List<String> valid = Arrays.asList("test_ABC-12","test2-abc_123","test-test");
    List<String> valid_null = Arrays.asList(null,"test123");
    List<String> invalid_only = Arrays.asList("test_A-B-C +12/de","2 test #");
    List<String> invalid_mixed = Arrays.asList("test_A-B-C +12/de","test123");
    ReferableListValidator validator = new ReferableListValidator();
    validator.initialize(annotation);
  
    // when & then
    assertTrue(validator.isValid(valid, null));
    assertTrue(validator.isValid(null, null));
    assertTrue(validator.isValid(valid_null, null));
    assertFalse(validator.isValid(invalid_only, null));
    assertFalse(validator.isValid(invalid_mixed, null));
  }
  
  @Test
  void validateMap() throws Exception {
    // given
    // get annotation via reflection
    Field annotated = this.getClass().getField("test");
    Referable annotation = annotated.getAnnotation(Referable.class);
    Map<String,String> valid = new HashMap<>();
    valid.put("test_ABC-12", "value");
    valid.put("test2-abc_123", "value");
    // null values as keys make no sense to test, as this would make no sense in a map.
    
    Map<String,String> invalid_only = new HashMap<>();
    invalid_only.put("test_A-B-C +12/de", "value");
    invalid_only.put("2 test #", "value");
    
    Map<String,String> invalid_mixed = new HashMap<>();
    invalid_mixed.put("test_A-B-C +12/de", "value");
    invalid_mixed.put("test123", "value");
    
    ReferableMapValidator validator = new ReferableMapValidator();
    validator.initialize(annotation);
  
    // when & then
    assertTrue(validator.isValid(null, null));
    assertTrue(validator.isValid(valid, null));
    assertFalse(validator.isValid(invalid_only, null));
    assertFalse(validator.isValid(invalid_mixed, null));
  }
}
