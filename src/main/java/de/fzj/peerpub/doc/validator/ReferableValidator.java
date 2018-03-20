package de.fzj.peerpub.doc.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for annotation "Referable"
 */
public class ReferableValidator implements ConstraintValidator<Referable, String> {
  
  /**
   * Regular expression store
   */
  private String regex;

  @Override
  public void initialize(Referable paramA) {
    this.regex = paramA.allowedRegex();
  }

  @Override
  public boolean isValid(String referableString, ConstraintValidatorContext ctx) {
    // validate attribute names match /^[a-zA-Z0-9\-_]+$/
    if (referableString == null || !referableString.matches(this.regex)) {
      return false;
    }
    return true;
  }

}
