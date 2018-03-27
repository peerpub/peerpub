package de.fzj.peerpub.doc.attribute;

import static org.junit.jupiter.api.Assertions.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
public class AttributeAdminCtrlIT {
  
  @Autowired
  private AttributeRepository attributeRepository;
  
  @Autowired
  private MockMvc mvc;
  
  // READ
  @Test
  void list() throws Exception {
    // given
    List<Attribute> attrs = attributeRepository.findAll();
    
    // when
    ResultActions result = mvc.perform(get("/admin/attributes"));
    
    // then
    result.andExpect(status().isOk());
    String content = result.andReturn().getResponse().getContentAsString();
    
    for(Attribute a : attrs) {
      assertTrue(content.contains(a.getName()));
      assertTrue(content.contains(a.getKey()));
      assertTrue(content.contains(a.getLabel()));
      assertTrue(content.contains(a.getDescription()));
      assertTrue(content.contains(a.getJsonSchema()));
    }
  }
  
  // CREATE
  @Test
  void add() throws Exception {
    // given
    Attribute aNew = AttributeTest.generate();
  
    // when
    ResultActions result = mvc.perform(postForm("/admin/attributes/add", aNew));
    result.andExpect(status().isFound())
          .andExpect(redirectedUrl("/admin/attributes"));
    result = mvc.perform(get("/admin/attributes"));
  
    // then
    result.andExpect(status().isOk());
    String content = result.andReturn().getResponse().getContentAsString();
    
    assertTrue(content.contains(aNew.getName()));
    assertTrue(content.contains(aNew.getKey()));
    assertTrue(content.contains(aNew.getLabel()));
    assertTrue(content.contains(aNew.getDescription()));
    assertTrue(content.contains(aNew.getJsonSchema()));
  }
  
}
