package de.fzj.peerpub.doc.publisher;

import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeService;
import de.fzj.peerpub.doc.doctype.DocTypeForm;
import de.fzj.peerpub.doc.doctype.DocTypeFormValidator;
import de.fzj.peerpub.doc.doctype.DocTypeService;
import de.fzj.peerpub.doc.validator.Referable;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Spring Controller for administration pages of document types.
 */
@Controller
@Validated
@RequestMapping("/admin/publishers")
public class PublisherAdminCtrl {
  
  final String BASE = this.getClass().getAnnotation(RequestMapping.class).value()[0];
  static final String ADD = "doc/publisher/addedit";
  
  private PublisherRepository publisherRepository;
  
  /**
   * Constructor with explicit dependencies
   */
  public PublisherAdminCtrl(@Autowired PublisherRepository publisherRepository) {
    this.publisherRepository = publisherRepository;
  }
  
  @GetMapping(path = {"", "/", "/list"})
  @ResponseBody
  public List<Publisher> listPublishers() {
    return publisherRepository.findAll();
  }
  
  @GetMapping("/add")
  public String addGetForm(ModelMap model) {
    PublisherTestForm ptf = new PublisherTestForm();
    org.bson.Document doc = new Document();
    HashMap<String, org.bson.Document> inner = new HashMap<>();
    HashMap<String, Map<String, org.bson.Document>> outer = new HashMap<>();
    doc.put(PublisherTestForm.DEFAULT, "test1234");
    inner.put("attribute", doc);
    outer.put("test", inner);
    ptf.setSupports(new ArrayList<>(Arrays.asList("test")));
    ptf.setMap(outer);
    
    model.addAttribute("publisher", ptf);
    return this.ADD;
  }
  
  @PostMapping("/add")
  public String addPostForm(@ModelAttribute("publisher") PublisherTestForm ptf,
                            BindingResult binding,
                            RedirectAttributes redirectAttr,
                            HttpServletRequest request) {
    System.out.println(request);
    System.out.println(ptf);
    return "redirect:/admin/publishers";
  }
  
}
