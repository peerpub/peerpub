package de.fzj.peerpub.doc.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Validator for annotation "Referable"
 */
public class ReferableMapValidator implements ConstraintValidator<Referable, Map<String, ?>> {
  /**
   * Regular expression store
   */
  private String regex;

  @Override
  public void initialize(Referable paramA) {
    this.regex = paramA.allowedRegex();
  }
  
  /**
   * In contrast to @Referable on String objects, we allow null values for the map
   * and the keys. This is due to be able to use this annotation within form
   * models, too. The values of the map are not validated.
   * @param referableMap
   * @param ctx
   * @return
   */
  @Override
  public boolean isValid(Map<String, ?> referableMap, ConstraintValidatorContext ctx) {
    if (referableMap == null) {
      return true;
    }
    for (String ref : referableMap.keySet()) {
      // validate attribute names match /^[a-zA-Z0-9\-_]+$/
      if (!ref.matches(this.regex)) {
        return false;
      }
    }
    return true;
  }

}
