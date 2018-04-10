package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeRepository;
import de.fzj.peerpub.doc.attribute.AttributeService;
import de.fzj.peerpub.doc.attribute.AttributeTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
public class DocTypeAdminCtrlIT {
  
  @Autowired
  private DocTypeFormValidator docTypeFormValidator;
  @Autowired
  private DocTypeRepository docTypeRepository;
  @Autowired
  private DocTypeService docTypeService;
  @Autowired
  private AttributeRepository attributeRepository;
  @Autowired
  private AttributeService attributeService;
  
  @Autowired
  private MockMvc mvc;
  
  private Map<String, Attribute> attrMap;
  private DocTypeForm valid;
  
  // READ
  @Test
  void list() throws Exception {
    // given
    // BE AWARE that we load some system types via Mongobee before the tests
    // run, so this actually will not be empty...
    List<DocType> dtList = docTypeService.getAll();
    assertTrue(dtList.size() > 0);
    
    // when
    ResultActions result = mvc.perform(get("/admin/doctypes"));
    
    // then
    result.andExpect(status().isOk());
    String content = result.andReturn().getResponse().getContentAsString();
    
    for(DocType dt : dtList) {
      assertTrue(content.contains(dt.getName()));
      if (dt.getSystem())
        assertTrue(content.contains("id=\""+dt.getName()+"-system\""));
      if (dt.getMultiDoc())
        assertTrue(content.contains("id=\""+dt.getName()+"-multidoc\""));
      
      // TODO: add 1) localized check for displayName, 2) check for attributes
    }
  }
}
