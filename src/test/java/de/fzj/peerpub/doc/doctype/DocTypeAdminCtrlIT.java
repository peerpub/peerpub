package de.fzj.peerpub.doc.doctype;

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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
public class DocTypeAdminCtrlIT {
  
  @Autowired
  private DocTypeService docTypeService;
  
  @Autowired
  private MockMvc mvc;
  
  // READ
  @Test
  void list() throws Exception {
    // given
    List<DocType> dtList = docTypeService.getAll();
    
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
        assertTrue(content.contains("id=\""+dt.getName()+"-system\""));
      
      // TODO: add 1) localized check for displayName, 2) check for attributes
    }
  }
}
