package de.fzj.peerpub.doc.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReferableValidator implements ConstraintValidator<Referable, String> {

  String regex;

  @Override
  public void initialize(Referable paramA) {
    this.regex = paramA.allowedRegex();
  }

  @Override
  public boolean isValid(String referableString, ConstraintValidatorContext ctx) {
    if(referableString == null){
      return false;
    }
    // validate attribute names match /^[a-zA-Z0-9\-_]+$/
    if (referableString.matches(this.regex)) return true;
    else return false;
  }

}
