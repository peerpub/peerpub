/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Spring validator for DocType form inputs
 * used in {@link DocTypeAdminCtrl}
 */
@Component
public class DocTypeFormValidator implements Validator {
  
  /**
   * Metadata attribute DAO
   */
  private AttributeRepository attributeRepository;
  
  /**
   * DocType DTO
   */
  private DocTypeService docTypeService;
  
  /**
   * Constructor with explicit dependency
   * @param attributeRepository
   */
  public DocTypeFormValidator(@Autowired AttributeRepository attributeRepository,
                              @Autowired DocTypeService docTypeService) {
    this.attributeRepository = attributeRepository;
    this.docTypeService = docTypeService;
  }
  
  @Override
  public boolean supports(Class<?> clazz) {
    return DocTypeForm.class.equals(clazz);
  }
  
  @Override
  public void validate(Object target, Errors errors) {
    if (target == null) {
      errors.reject("doctype.form.empty");
      return;
    }
    
    DocTypeForm dtf = (DocTypeForm) target;
  
    // Get the original doc type from the database if present.
    Optional<DocTypeForm> optOrig = docTypeService.getByName(dtf.getName());
    // If not in database, this is an addition. Do not allow system property set for this.
    if (!optOrig.isPresent() && dtf.getSystem()) {
      errors.rejectValue("system", "doctype.system.cannotAdd",
          "You cannot add new system document types.");
    }
    // If in database...
    if (optOrig.isPresent()) {
      DocTypeForm original = optOrig.get();
      // Do not allow to change from system type to non-system type
      if (!dtf.getSystem().equals(original.getSystem())) {
        errors.rejectValue("system", "doctype.system.cannotChange",
            "You cannot change a system document type to non-system type.");
      }
      // If system type, check the displayName property has not been changed
      if (original.getSystem() && !original.getDisplayName().equals(dtf.getDisplayName())) {
        errors.rejectValue("displayName", "doctype.system.cannotChangeDisplay",
            "You cannot change a system document types display name.");
      }
      // If system type, check the multiDoc property has not been changed
      if (original.getSystem() && !original.getMultiDoc().equals(dtf.getMultiDoc())) {
        errors.rejectValue("multiDoc", "doctype.system.cannotChangeMulti",
            "You cannot change a system document types context (single/multiple document).");
      }
      // Attributes may be altered even on system types, thus don't check them.
    }
    
    if (dtf.getAttributes() != null) {
      Set<String> attrs = new HashSet<>(dtf.getAttributes());
      
      // 1.) check if the attributes exist, otherwise reject
      for (String attr : attrs) {
        if (!attributeRepository.findByName(attr).isPresent()) {
          errors.rejectValue("attributes",
                            "doctype.attribute.notexist",
                            "Attributes contains an attribute that does not exist!");
        }
      }
      
      // 2.) check if the attribute names used in the list and in mandatory map match
      if (dtf.getMandatory() != null) {
        Set<String> mand = dtf.getMandatory().keySet();
        // all attributes have an entry?
        if (!mand.containsAll(attrs)) {
          errors.rejectValue("mandatory", "doctype.mandatory.mismatch",
                       "List of attribute stati and attribute names do not match!");
        }
        // not more statuses than attributes?
        if (mand.size() != attrs.size()) {
          errors.rejectValue("mandatory", "doctype.mandatory.mismatch",
                       "List of attribute stati and attribute names do not match!");
        }
      }
      
      // 3.) check if all mandatory statuses are set to true (= checked box) or null (= unchecked box)
      for (String attr : attrs) {
        if (dtf.getMandatory() != null
            && dtf.getMandatory().get(attr) != null
            && !dtf.getMandatory().get(attr)) {
          errors.rejectValue("mandatory", "doctype.mandatory.invalid",
                       "Mandatory contains elements set to something else than true or null");
        }
      }
      
      // 4.) check if the attribute names used in the list and in default values map match
      if (dtf.getDefaults() != null) {
        Set<String> defs = dtf.getDefaults().keySet();
        if (!attrs.containsAll(defs)) {
          errors.rejectValue("defaults", "doctype.default.mismatch",
                       "Default values contain more elements than attributes!");
        }
      }
    }
    
    // TODO: extend here to validate default values against attributes JSON schema
    //       (most certainly you will autowire the attribute repository in...)
  }
}
