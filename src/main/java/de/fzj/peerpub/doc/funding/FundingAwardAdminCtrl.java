/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.doc.funding;

import de.fzj.peerpub.doc.validator.Referable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Spring Controller for administration pages of metadata attributes.
 */
@Controller
@Validated
@RequestMapping("/admin/funding/awards")
public class FundingAwardAdminCtrl {

  static final String LIST = "doc/funding/award/list";
  static final String ADD = "doc/funding/award/addedit";
  static final String MODEL_ATTR = "award";
  static final String EDIT_ATTR = "edit";

  /**
   * Awards DAO (using directly, not using unnecessary service layer)
   */
  private AwardRepository awardRepository;
  
  /**
   * Constructor with explicit but autowired dependency on repository
   */
  public FundingAwardAdminCtrl(@Autowired AwardRepository repo) {
    this.awardRepository = repo;
  }

  /**
   * Read all awards and list 'em
   * @return View name
   */
  @GetMapping(path = {"", "/", "/list"})
  public String list(ModelMap model) {
    model.addAttribute("awards", awardRepository.findAll());
    return LIST;
  }

  /**
   * Get form to add an award
   * @return View name
   */
  @GetMapping("/add")
  public String addGetForm(ModelMap model) {
    model.addAttribute(MODEL_ATTR, new Award());
    return ADD;
  }
  /**
   * Form handling for action "add" via POST request
   * @param a The award after data binding from the request
   * @return View name if failing or redirect to attribute list on success
   */
  @PostMapping("/add")
  public String addPostForm(@ModelAttribute(MODEL_ATTR) @Validated Award a,
                            BindingResult binding,
                            RedirectAttributes redirectAttr) {
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      return ADD;
    }
    // reject if entity with same name present in the database
    if (awardRepository.findById(a.getId()).isPresent()) {
      binding.rejectValue("id", "duplicate.name", "Name already in use!");
      return ADD;
    }
    // try to save attribute to database
    try {
      awardRepository.save(a);
    // present any error to the user
    } catch (Exception e) {
      binding.reject("exception.unknown", e.getMessage());
      //TODO: add logging of this exception
      return ADD;
    }
    // return to list of all attributes and set success flag with message code
    redirectAttr.addFlashAttribute("success", "add.success");
    return "redirect:/admin/funding/awards";
  }
  
  /**
   * Get edit form.
   * @param name The name of the award to be edited (retrieve from database)
   * @return View name or redirect to list of awards
   */
  @GetMapping("/edit/{id}")
  public String editGetForm(ModelMap model,
                            @Referable @PathVariable("id") String name,
                            RedirectAttributes redirectAttr) {
    // try to save attribute to database
    try {
      Optional<Award> oa = awardRepository.findById(name);
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
      return "redirect:/admin/funding/awards";
    }
  }
  
  /**
   * Form handling for action "edit" via POST request
   * @param name The name of the attribute to be edited
   * @param a Data binded attribute value to be inserted into database.
   * @return View name or redirect to list of attributes
   */
  @PostMapping("/edit/{id}")
  public String editPostForm(ModelMap model,
                             @Referable @PathVariable("id") String name,
                             @ModelAttribute(MODEL_ATTR) @Validated Award a,
                             BindingResult binding,
                             RedirectAttributes redirectAttr) {
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      model.addAttribute(EDIT_ATTR, true);
      return ADD;
    }
    // reject if name parameter and model do NOT match
    if (!name.equals(a.getId())) {
      binding.rejectValue("id", "mismatch.name", "Name parameter and model name do not match!");
      model.addAttribute(EDIT_ATTR, true);
      return ADD;
    }
    // try to save award to database (internal upsert!)
    try {
      awardRepository.save(a);
      // present any error to the user
    } catch (Exception e) {
      binding.reject("exception.unknown", e.getMessage());
      model.addAttribute(EDIT_ATTR, true);
      //TODO: add logging of this exception
      return ADD;
    }
    // return to list of all attributes and set success flag with message code
    redirectAttr.addFlashAttribute("success", "edit.success");
    return "redirect:/admin/funding/awards";
  }

  /**
   * Delete an award in the database
   * @param name Path variable with the award name
   * @return Redirection to list of awards
   */
  @GetMapping("/delete/{id}")
  public String delete(@Referable @PathVariable("id") String name, RedirectAttributes redirectAttr) {
    try {
      awardRepository.deleteById(name);
      redirectAttr.addFlashAttribute("success", "delete.success");
    } catch (Exception e) {
      // TODO: log this exception
      redirectAttr.addFlashAttribute("fail", "delete.failed");
    }
    return "redirect:/admin/funding/awards";
  }
}
