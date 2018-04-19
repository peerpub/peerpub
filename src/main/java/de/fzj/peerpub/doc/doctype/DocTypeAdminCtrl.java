package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeService;
import de.fzj.peerpub.doc.validator.Referable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

/**
 * Spring Controller for administration pages of document types.
 */
@Controller
@Validated
@RequestMapping("/admin/doctypes")
public class DocTypeAdminCtrl {
  
  static final String LIST = "doc/doctype/list";
  static final String ADD = "doc/doctype/addedit";
  static final String MODEL_ATTR = "doctype";
  static final String ATTRMAP_ATTR = "attributeMap";
  static final String EDIT_ATTR = "edit";
  
  /**
   * DocType DTO
   */
  private DocTypeService docTypeService;
  
  /**
   * Metadata attribute DTO
   */
  private AttributeService attributeService;
  
  /**
   * DocTypeForm validator to validate more complex things
   * the annotation based validator cannot handle (relations, etc)
   */
  private DocTypeFormValidator docTypeFormValidator;
  
  /**
   * Constructor with explicit dependencies
   */
  public DocTypeAdminCtrl(@Autowired DocTypeService docTypeService,
                          @Autowired AttributeService attributeService,
                          @Autowired DocTypeFormValidator docTypeFormValidator) {
    this.docTypeService = docTypeService;
    this.attributeService = attributeService;
    this.docTypeFormValidator = docTypeFormValidator;
  }
  
  /**
   * Add validator to doctype model attribute parameter
   */
  @InitBinder(MODEL_ATTR)
  public void setupBinder(WebDataBinder binder) {
    binder.addValidators(docTypeFormValidator);
  }
  
  /**
   * Always add all attributes to the model.
   * Most likely this is needed in all administrative actions.
   */
  @ModelAttribute(ATTRMAP_ATTR)
  public Map<String, Attribute> getAttributeMap() {
    return attributeService.getNameBasedMap();
  }
  
  /**
   * Read all doc types plus attributes and list 'em
   * @return View name
   */
  @GetMapping(path = {"", "/", "/list"})
  public String list(ModelMap model) {
    model.addAttribute("doctypes", docTypeService.getAll());
    return LIST;
  }
  
  /**
   * Get add form.
   * @return View name or redirect to list of attributes
   */
  @GetMapping("/add")
  public String addGetForm(ModelMap model,
                            RedirectAttributes redirectAttr) {
    model.addAttribute(MODEL_ATTR, new DocTypeForm());
    return ADD;
  }
  
  /**
   * Post add form
   */
  @PostMapping("/add")
  public String addPostForm(ModelMap model,
                            @ModelAttribute(MODEL_ATTR) @Validated DocTypeForm dtf,
                            BindingResult binding,
                            RedirectAttributes redirectAttr) {
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      return ADD;
    }
    try {
      docTypeService.saveAdd(dtf);
    } catch (DuplicateKeyException e) {
      binding.rejectValue("name", "duplicate.name", "This name is already in use.");
      return ADD;
    } catch (RuntimeException e) {
      binding.reject("exception.unknown", e.getMessage());
      //TODO: add logging of this exception
      return ADD;
    }
    
    // return to list of all attributes and set success flag with message code
    redirectAttr.addFlashAttribute("success", "add.success");
    return "redirect:/admin/doctypes";
  }
  
  /**
   * Get edit form.
   * @param name The name of the document type to be edited (retrieve from database)
   * @return View name or redirect to list of attributes
   */
  @GetMapping("/edit/{name}")
  public String editGetForm(ModelMap model,
                            @Referable @PathVariable("name") String name,
                            RedirectAttributes redirectAttr) {
    // try to get doctype from database
    try {
      Optional<DocTypeForm> oDtf = docTypeService.getByName(name);
      if (oDtf.isPresent()) {
        model.addAttribute(EDIT_ATTR, true);
        model.addAttribute(MODEL_ATTR, oDtf.get());
        return ADD;
      } else {
        throw new RuntimeException("edit.notfound");
      }
      // present any error to the user
    } catch (RuntimeException e) {
      // TODO: log this exception
      redirectAttr.addFlashAttribute("fail", "edit.failed");
      return "redirect:/admin/doctypes";
    }
  }
  
  /**
   * Post edit form
   */
  @PostMapping("/edit/{type}")
  public String editPostForm(ModelMap model,
                             @Referable @PathVariable("type") String type,
                             @ModelAttribute(MODEL_ATTR) @Validated DocTypeForm dtf,
                             BindingResult binding,
                             RedirectAttributes redirectAttr) {
    // reject if name parameter and model do NOT match
    if (dtf != null && !type.equals(dtf.getName())) {
      binding.rejectValue("name", "mismatch.name", "Type name parameter and model type name do not match!");
    }
    
    // validation errors: let the user edit and try again
    if (binding.hasErrors()) {
      model.addAttribute(EDIT_ATTR, true);
      return ADD;
    }
  
    try {
      docTypeService.saveEdit(dtf);
    } catch (RuntimeException e) {
      binding.reject("exception.unknown", e.getMessage());
      model.addAttribute(EDIT_ATTR, true);
      //TODO: add logging of this exception
      return ADD;
    }
    
    // return to list of all attributes and set success flag with message code
    redirectAttr.addFlashAttribute("success", "edit.success");
    return "redirect:/admin/doctypes";
  }
  
  /**
   * Delete a document type in the database
   * @param name Path variable with the document type name
   * @return Redirection to list of document types
   */
  @GetMapping("/delete/{type}")
  public String delete(@Referable @PathVariable("type") String name, RedirectAttributes redirectAttr) {
    try {
      docTypeService.deleteById(name);
      redirectAttr.addFlashAttribute("success", "delete.success");
    } catch (Exception e) {
      // TODO: log this exception
      redirectAttr.addFlashAttribute("fail", "delete.failed");
      redirectAttr.addFlashAttribute("cause", e.getMessage());
    }
    return "redirect:/admin/doctypes";
  }
  
}
