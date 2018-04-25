/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.doc.attribute;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import de.fzj.peerpub.doc.validator.Referable;

import java.util.Optional;

/**
 * Spring Controller for administration pages of metadata attributes.
 */
@Controller
@Validated
@RequestMapping("/admin/attributes")
public class AttributeAdminCtrl {

  static final String LIST = "doc/attribute/list";
  static final String ADD = "doc/attribute/addedit";
  // reuse add instead with some parameters, so it differs while viewing
  // and gets loaded with data from the database...
  // static final String EDIT = "attributeEdit";
  static final String DELETE = "doc/attribute/delete";
  static final String MODEL_ATTR = "attribute";
  static final String EDIT_ATTR = "edit";

  /**
   * Metadata attribute DAO (using directly, not using unnecessary service layer)
   */
  private AttributeRepository attributeRepository;
  
  /**
   * Constructor with explicit but autowired dependency on repository
   */
  public AttributeAdminCtrl(@Autowired AttributeRepository repo) {
    this.attributeRepository = repo;
  }

  /**
   * Read all attributes and list 'em
   * @return View name
   */
  @GetMapping(path = {"", "/", "/list"})
  public String list(ModelMap model) {
    model.addAttribute("attributes", attributeRepository.findAll());
    return LIST;
  }

  /**
   * Get form to add an attribute
   * @return View name
   */
  @GetMapping("/add")
  public String addGetForm(ModelMap model) {
    model.addAttribute(MODEL_ATTR, new Attribute());
    return ADD;
  }
  /**
   * Form handling for action "add" via POST request
   * @param a The attribute after data binding from the request
   * @return View name if failing or redirect to attribute list on success
   */
  @PostMapping("/add")
  public String addPostForm(@ModelAttribute(MODEL_ATTR) @Validated Attribute a,
                            BindingResult binding,
                            RedirectAttributes redirectAttr) {
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      return ADD;
    }
    // reject if entity with same name present in the database
    if (attributeRepository.findByName(a.getName()).isPresent()) {
      binding.rejectValue("name", "duplicate.name", "Name already in use!");
      return ADD;
    }
    // try to save attribute to database
    try {
      attributeRepository.save(a);
    // present any error to the user
    } catch (Exception e) {
      binding.reject("exception.unknown", e.getMessage());
      //TODO: add logging of this exception
      return ADD;
    }
    // return to list of all attributes and set success flag with message code
    redirectAttr.addFlashAttribute("success", "add.success");
    return "redirect:/admin/attributes";
  }
  
  /**
   * Get edit form.
   * @param name The name of the attribute to be edited (retrieve from database)
   * @return View name or redirect to list of attributes
   */
  @GetMapping("/edit/{name}")
  public String editGetForm(ModelMap model,
                            @Referable @PathVariable("name") String name,
                            RedirectAttributes redirectAttr) {
    // try to save attribute to database
    try {
      Optional<Attribute> oa =  attributeRepository.findByName(name);
      if (oa.isPresent()) {
        model.addAttribute(EDIT_ATTR, true);
        model.addAttribute(MODEL_ATTR, oa.get());
        return ADD;
      } else {
        throw new RuntimeException("edit.notfound");
      }
    // present any error to the user
    } catch (RuntimeException e) {
      // TODO: log this exception
      redirectAttr.addFlashAttribute("fail", "edit.failed");
      return "redirect:/admin/attributes";
    }
  }
  
  /**
   * Form handling for action "edit" via POST request
   * @param name The name of the attribute to be edited
   * @param a Data binded attribute value to be inserted into database.
   * @return View name or redirect to list of attributes
   */
  @PostMapping("/edit/{name}")
  public String editPostForm(ModelMap model,
                             @Referable @PathVariable("name") String name,
                             @ModelAttribute(MODEL_ATTR) @Validated Attribute a,
                             BindingResult binding,
                             RedirectAttributes redirectAttr) {
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      model.addAttribute(EDIT_ATTR, true);
      return ADD;
    }
    // reject if name parameter and model do NOT match
    if (!name.equals(a.getName())) {
      binding.rejectValue("name", "mismatch.name", "Name parameter and model name do not match!");
      model.addAttribute(EDIT_ATTR, true);
      return ADD;
    }
    // try to save attribute to database (internal upsert!)
    try {
      attributeRepository.save(a);
      // present any error to the user
    } catch (Exception e) {
      binding.reject("exception.unknown", e.getMessage());
      model.addAttribute(EDIT_ATTR, true);
      //TODO: add logging of this exception
      return ADD;
    }
    // return to list of all attributes and set success flag with message code
    redirectAttr.addFlashAttribute("success", "edit.success");
    return "redirect:/admin/attributes";
  }

  /**
   * Delete an attribute in the database
   * @param name Path variable with the attribute name
   * @return Redirection to list of attributes
   */
  @GetMapping("/delete/{name}")
  public String delete(@Referable @PathVariable("name") String name, RedirectAttributes redirectAttr) {
    try {
      attributeRepository.deleteById(name);
      redirectAttr.addFlashAttribute("success", "delete.success");
    } catch (Exception e) {
      // TODO: log this exception
      redirectAttr.addFlashAttribute("fail", "delete.failed");
    }
    return "redirect:/admin/attributes";
  }
}
