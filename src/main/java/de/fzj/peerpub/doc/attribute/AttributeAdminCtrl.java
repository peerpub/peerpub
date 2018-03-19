package de.fzj.peerpub.doc.attribute;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;

import de.fzj.peerpub.doc.attribute.AttributeRepository;
import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.log.*;
import de.fzj.peerpub.doc.validator.Referable;

import java.util.List;
import java.util.ArrayList;

@Controller
@Validated
@RequestMapping("/admin/attributes")
public class AttributeAdminCtrl {

  public static final String LIST = "attributeList";
  public static final String ADD = "attributeAdd";
  public static final String EDIT = "attributeEdit";
  public static final String DELETE = "attributeDelete";
  public static final String MODEL_ATTR = "attribute";

  @Autowired
  AttributeRepository attributeRepository;

  /**
   * Read all attributes and list 'em
   */
  @GetMapping(path={"","/"})
  public String list(ModelMap model) {
    model.addAttribute("attributes", attributeRepository.findAll());
    return LIST;
  }

  /**
   * Get form to add an attribute
   */
  @GetMapping("/add")
  public String addGetForm() {
    return ADD;
  }
  /**
   * Form handling for action "add" via POST request
   */
  @PostMapping("/add")
  public String addPostForm(ModelMap model,
                            @ModelAttribute(MODEL_ATTR) @Validated Attribute a,
                            BindingResult binding,
                            RedirectAttributes redirectAttr) {
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      return ADD;
    }
    // reject if entity with same name present in the database
    if(attributeRepository.findByName(a.getName()).isPresent()) {
      binding.rejectValue("name","duplicate.name","Name already in use!");
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
   * Delete an attribute in the database
   */
  @GetMapping("/delete/{name}")
  public String delete(@Referable @PathVariable("name") String name, RedirectAttributes redirectAttr) {
    try {
      attributeRepository.deleteById(name);
      redirectAttr.addFlashAttribute("success", "delete.success");
    } catch(Exception e) {
      // TODO: log this exception
      redirectAttr.addFlashAttribute("fail", "delete.failed");
    }
    return "redirect:/admin/attributes";
  }
}
