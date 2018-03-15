package de.fzj.peerpub.doc.validator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import de.fzj.peerpub.doc.validator.Referable;
import de.fzj.peerpub.doc.validator.ReferableValidator;

import java.util.Set;
import java.lang.reflect.Field;

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
  }
}
