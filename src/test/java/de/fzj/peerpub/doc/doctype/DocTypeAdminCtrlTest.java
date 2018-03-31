package de.fzj.peerpub.doc.doctype;

import com.mongodb.MongoException;
import de.fzj.peerpub.doc.attribute.*;
import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// replace with "org.mockito.junit5.MockitoExtension" once it gets released...
// see https://github.com/mockito/mockito/issues/1221
// and https://github.com/mockito/mockito/issues/445

@ExtendWith(MockitoExtension.class)
@Tag("medium")
public class DocTypeAdminCtrlTest {
  
  @Mock
  private DocTypeService docTypeService;
  @Mock
  private AttributeService attributeService;
  
  @InjectMocks
  private DocTypeAdminCtrl docTypeAdminCtrl;

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    // MockMvc standalone approach
    mvc = MockMvcBuilders.standaloneSetup(docTypeAdminCtrl)
            //.setControllerAdvice(new SuperHeroExceptionHandler())
            //.addFilters(new SuperHeroFilter())
            .build();
  }

  /**
   * Alternate approach using static MockMvc, thus only initializing the context once:
   * 1) private static AttributeAdminCtrl attributeAdminCtrl = new AttributeAdminCtrl();
   * 2) private static MockMvc mvc = MockMvcBuilders.standaloneSetup(attributeAdminCtrl).build();
   * 3) Remove setup() as not needed.
   * This approach seems to be slightly quicker (about 0.4 secs for 6 tests).
   */

  // READ
  @Test
  void list() throws Exception {
    // given
    Attribute testAttr = AttributeTest.generate();
    Map<String, Attribute> attrMap = new HashMap<>();
    attrMap.put(testAttr.getName(), testAttr);
    DocType testDT = DocTypeTest.generate(Arrays.asList(testAttr));
    List<DocType> dtList = Arrays.asList(testDT);
    
    given(attributeService.getNameBasedMap()).willReturn(attrMap);
    given(docTypeService.getAll()).willReturn(dtList);

    // when
    ResultActions result = mvc.perform(get("/admin/doctypes"));

    // then
    result.andExpect(status().isOk())
          .andExpect(model().attribute("attributeMap", attrMap))
          .andExpect(model().attribute("doctypes", dtList))
          .andExpect(view().name(DocTypeAdminCtrl.LIST));
  }
}
