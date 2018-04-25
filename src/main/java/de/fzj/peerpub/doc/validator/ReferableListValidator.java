/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.doc.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for annotation "Referable"
 */
public class ReferableListValidator implements ConstraintValidator<Referable, Iterable<String>> {
  
  /**
   * Regular expression store
   */
  private String regex;

  @Override
  public void initialize(Referable paramA) {
    this.regex = paramA.allowedRegex();
  }
  
  /**
   * In contrast to @Referable on String objects, we allow null values for the iterable
   * and the content. This is due to be able to use this annotation within form
   * models, too.
   * @param referableList
   * @param ctx
   * @return
   */
  @Override
  public boolean isValid(Iterable<String> referableList, ConstraintValidatorContext ctx) {
    if (referableList == null) {
      return true;
    }
    for (String ref : referableList) {
      // validate attribute names match /^[a-zA-Z0-9\-_]+$/
      if (ref != null && !ref.matches(this.regex)) {
        return false;
      }
    }
    return true;
  }

}
