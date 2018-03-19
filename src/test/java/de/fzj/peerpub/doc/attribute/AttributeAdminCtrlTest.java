package de.fzj.peerpub.doc.attribute;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.extension.ExtendWith;
// replace with "org.mockito.junit5.MockitoExtension" once it gets released...
// see https://github.com/mockito/mockito/issues/1221
// and https://github.com/mockito/mockito/issues/445
import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.MediaType;

import com.mongodb.MongoException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
@Tag("medium")
public class AttributeAdminCtrlTest {

  @Mock
  private AttributeRepository attributeRepository;

  @InjectMocks
  private AttributeAdminCtrl attributeAdminCtrl;

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    // MockMvc standalone approach
    mvc = MockMvcBuilders.standaloneSetup(attributeAdminCtrl)
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
  @Test
  void editGetForm() throws Exception {
    // given
    Attribute attr = AttributeTest.generate();
    Optional<Attribute> oAttr = Optional.of(attr);
    given(attributeRepository.findByName(attr.getName())).willReturn(oAttr);
    // when
    ResultActions result = mvc.perform(get("/admin/attributes/edit/{name}",attr.getName()));
    // then
    result.andExpect(status().isOk())
          .andExpect(view().name(AttributeAdminCtrl.ADD))
          .andExpect(model().attribute(AttributeAdminCtrl.EDIT_ATTR, true))
          .andExpect(model().attribute(AttributeAdminCtrl.MODEL_ATTR, attr));
  }
  @Test
  void editGetFormNonExistingName() throws Exception {
    //given
    String name = "test";
    given(attributeRepository.findByName(name)).willReturn(Optional.empty());
    //when
    ResultActions result = mvc.perform(get("/admin/attributes/edit/{name}",name));
    //then
    result.andExpect(status().isFound())
        .andExpect(flash().attribute("fail", "edit.failed"))
        .andExpect(redirectedUrl("/admin/attributes"));
  }
  @Test
  void editPostFormSuccess() throws Exception {
    // given
    Attribute attr = AttributeTest.generate();
    // when
    ResultActions result = mvc.perform(postForm("/admin/attributes/edit/"+attr.getName(), attr));
    // then
    result.andExpect(status().isFound())
          .andExpect(model().hasNoErrors())
          .andExpect(flash().attribute("success", "edit.success"))
          .andExpect(redirectedUrl("/admin/attributes"));
  }
  @Test
  void editPostFormInvalidNameKeyError() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    // set invalid name!
    attr.setName("test_ABC +123");
    attr.setKey("test_ABC +123");
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/edit/"+attr.getName(),attr));
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
  void editPostFormInvalidBlankError() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    // set invalid label, description and jsonschema
    attr.setLabel("");
    attr.setDescription("");
    attr.setJsonSchema("");
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/edit/"+attr.getName(),attr));
    //then
    result.andExpect(status().isOk())
        .andExpect(view().name(AttributeAdminCtrl.ADD))
        .andExpect(model().attribute(AttributeAdminCtrl.MODEL_ATTR, attr))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "label"))
        .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "description"))
        .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "jsonSchema"));
  }
  @Test
  void editPostFormNameMismatchError() throws Exception {
    //given
    Attribute attr = AttributeTest.generate();
    String requestName = "test";
    //when
    ResultActions result = mvc.perform(postForm("/admin/attributes/edit/"+requestName, attr));
    //then
    result.andExpect(status().isOk())
        .andExpect(view().name(AttributeAdminCtrl.ADD))
        .andExpect(model().attribute(AttributeAdminCtrl.MODEL_ATTR, attr))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeHasFieldErrors(AttributeAdminCtrl.MODEL_ATTR, "name"))
        .andExpect(model().attributeHasFieldErrorCode(AttributeAdminCtrl.MODEL_ATTR, "name", "mismatch.name"));
  }

  // DELETE
  @Test
  void deleteSuccess() throws Exception {
    //given
    String name = "test";
    /*
     * not usefull here, as deleteById() return void if successfull...
     * willThrow(new DataAccessException("test")).given(attributeRepository).deleteById(name);
     */
    //when
    ResultActions result = mvc.perform(get("/admin/attributes/delete/{name}",name));
    //then
    result.andExpect(status().isFound())
          .andExpect(flash().attribute("success", "delete.success"))
          .andExpect(redirectedUrl("/admin/attributes"));
  }
  @Test
  void deleteInvalidOrNonExistingName() throws Exception {
    //given
    String name = "test +ABC";
    willThrow(new MongoException("fault")).given(attributeRepository).deleteById(name);
    //when
    ResultActions result = mvc.perform(get("/admin/attributes/delete/{name}",name));
    //then
    result.andExpect(status().isFound())
          .andExpect(flash().attribute("fail", "delete.failed"))
          .andExpect(redirectedUrl("/admin/attributes"));
  }
}
