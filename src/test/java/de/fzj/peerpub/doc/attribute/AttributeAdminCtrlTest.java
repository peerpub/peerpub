package de.fzj.peerpub.doc.attribute;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.extension.ExtendWith;
// replace with "org.mockito.junit5.MockitoExtension" once it gets released...
// see https://github.com/mockito/mockito/issues/1221
// and https://github.com/mockito/mockito/issues/445
import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.*;

import java.util.List;
import java.util.Optional;

import de.fzj.peerpub.doc.attribute.*;
import de.fzj.peerpub.log.*;

@ExtendWith(MockitoExtension.class)
public class AttributeAdminCtrlTest {

  @Autowired
  private MockMvc mvc;

  @Mock
  private AttributeRepository attributeRepository;

  @InjectMocks
  private AttributeAdminCtrl attributeAdminCtrl;

  @BeforeEach
  void setup() {
    // MockMvc standalone approach
    mvc = MockMvcBuilders.standaloneSetup(attributeAdminCtrl)
            //.setControllerAdvice(new SuperHeroExceptionHandler())
            //.addFilters(new SuperHeroFilter())
            .build();
  }

  // READ
  @Test
  void list() throws Exception {
    // given
    List<Attribute> attrs = AttributeTest.generate(3);
    given(attributeRepository.findAll()).willReturn(attrs);

    // when
    ResultActions result = mvc.perform(get("/admin/attributes"));

    // then
    result.andExpect(status().isOk())
          .andExpect(model().attribute("attributes", attrs))
          .andExpect(view().name(AttributeAdminCtrl.LIST));
  }

  // CREATE
  @Test
  void addGetForm() throws Exception {
    // given
    // when
    ResultActions result = mvc.perform(get("/admin/attributes/add"));
    // then
    result.andExpect(status().isOk())
          .andExpect(view().name(AttributeAdminCtrl.ADD));
  }
  @Test
  void addPostFormValid() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/add",attr));
    //then
    result.andExpect(status().isFound())
          .andExpect(model().hasNoErrors())
          .andExpect(flash().attribute("success", "add.success"))
          .andExpect(redirectedUrl("/admin/attributes"));
  }
  @Test
  void addPostFormDuplicateError() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    Optional<Attribute> oAttr = Optional.of(attr);
    given(attributeRepository.findByName(attr.getName())).willReturn(oAttr);
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/add",attr));
    //then
    result.andExpect(status().isOk())
          .andExpect(view().name(AttributeAdminCtrl.ADD))
          .andExpect(model().attribute(AttributeAdminCtrl.MODEL_ATTR, attr))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR,"name"))
          .andExpect(model().attributeHasFieldErrorCode(AttributeAdminCtrl.MODEL_ATTR, "name", "duplicate.name"));
  }
  @Test
  void addPostFormInvalidNameKeyError() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    // set invalid name!
    attr.setName("test_ABC +123");
    attr.setKey("test_ABC +123");
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/add",attr));
    //then
    result.andExpect(status().isOk())
          .andExpect(view().name(AttributeAdminCtrl.ADD))
          .andExpect(model().attribute(AttributeAdminCtrl.MODEL_ATTR, attr))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "name"))
          .andExpect(model().attributeHasFieldErrorCode(AttributeAdminCtrl.MODEL_ATTR, "name", "Referable"))
          .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "key"))
          .andExpect(model().attributeHasFieldErrorCode(AttributeAdminCtrl.MODEL_ATTR, "key", "Referable"));
  }
  @Test
  void addPostFormInvalidBlankError() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    // set invalid label, description and jsonschema
    attr.setLabel("");
    attr.setDescription("");
    attr.setJsonSchema("");
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/add",attr));
    //then
    result.andExpect(status().isOk())
          .andExpect(view().name(AttributeAdminCtrl.ADD))
          .andExpect(model().attribute(AttributeAdminCtrl.MODEL_ATTR, attr))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "label"))
          .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "description"))
          .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "jsonSchema"));
  }

  // UPDATE
  // DELETE
}
