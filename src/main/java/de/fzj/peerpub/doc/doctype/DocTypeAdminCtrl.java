package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring Controller for administration pages of document types.
 */
@Controller
@Validated
@RequestMapping("/admin/doctypes")
public class DocTypeAdminCtrl {
  
  static final String LIST = "doc/doctype/list";
  
  /**
   * DocType DTO
   */
  @Autowired
  private DocTypeService docTypeService;
  
  /**
   * Metadata attribute DTO
   */
  @Autowired
  private AttributeService attributeService;
  
  /**
   * Read all attributes and list 'em
   * @return View name
   */
  @GetMapping(path = {"", "/", "/list"})
  public String list(ModelMap model) {
    model.addAttribute("attributeMap", attributeService.getNameBasedMap());
    model.addAttribute("doctypes", docTypeService.getAll());
    return LIST;
  }
  
}
