package de.fzj.peerpub.doc.doctype;

import de.fzj.peerpub.doc.attribute.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import java.util.*;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@Tag("medium")
public class DocTypeAdminCtrlTest {
  
  @Mock
  private DocTypeService docTypeService;
  @Mock
  private AttributeService attributeService;
  @Mock
  private AttributeRepository attributeRepository;
  @Mock
  private DocTypeFormValidator docTypeFormValidator;
  
  private DocTypeAdminCtrl docTypeAdminCtrl;
  private DocTypeForm valid;
  private Map<String, Attribute> attrMap;

  @Autowired
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    when(docTypeFormValidator.supports(eq(DocTypeForm.class))).thenReturn(true);
    this.docTypeAdminCtrl = new DocTypeAdminCtrl(docTypeService,
                                                 attributeService,
                                                 docTypeFormValidator);
    // MockMvc standalone approach
    mvc = MockMvcBuilders.standaloneSetup(docTypeAdminCtrl)
            //.setControllerAdvice(new SuperHeroExceptionHandler())
            //.addFilters(new SuperHeroFilter())
            .build();
    
    // Build a valid DocTypeForm to be used in tests
    List<Attribute> attrs = AttributeTest.generate(2);
    this.attrMap = attributeMap(attrs);
    this.valid = DocTypeFormTest.adaptToViewValues(DocTypeForm.toForm(DocTypeTest.generate(attrs, attrs.size())));
    this.valid.setSystem(false);
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
  @MockitoSettings(strictness = Strictness.LENIENT)
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
  
  // CREATE
  @Test
  void addGetForm() throws Exception {
    // given
    given(attributeService.getNameBasedMap()).willReturn(this.attrMap);
    
    // when
    ResultActions result = mvc.perform(get("/admin/doctypes/add"));
    
    // then
    result.andExpect(status().isOk())
        .andExpect(model().attribute(DocTypeAdminCtrl.ATTRMAP_ATTR, attrMap))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, new DocTypeForm()))
        .andExpect(view().name(DocTypeAdminCtrl.ADD));
  }
  
  @Test
  void addPostFormSuccess() throws Exception {
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/add", this.valid));
    
    // then
    result.andExpect(status().isFound())
        .andExpect(flash().attribute("success", "add.success"))
        .andExpect(redirectedUrl("/admin/doctypes"));
  }
  
  @Test
  void addPostFormBindingErrors() throws Exception {
    // given
    // simulate a validator error needing handling
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        Errors errors = (Errors) invocationOnMock.getArguments()[1];
        errors.reject("forcing some error");
        return null;
      }
    }).when(docTypeFormValidator).validate(any(DocTypeForm.class), any(Errors.class));
    
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/add", this.valid));
    
    // then
    result.andExpect(status().isOk())
        .andExpect(view().name(DocTypeAdminCtrl.ADD))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
        .andExpect(model().hasErrors());
  }
  
  @Test
  void addPostFormDuplicateException() throws Exception {
    // given
    given(docTypeService.saveAdd(this.valid)).willThrow(DuplicateKeyException.class);
    
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/add", this.valid));
    
    // then
    result.andExpect(status().isOk())
        .andExpect(view().name(DocTypeAdminCtrl.ADD))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
        .andExpect(model().attributeHasErrors(DocTypeAdminCtrl.MODEL_ATTR));
  }
  
  @Test
  void addPostFormExceptionDTO() throws Exception {
    // given
    given(docTypeService.saveAdd(this.valid)).willThrow(RuntimeException.class);
    
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/add", this.valid));
    
    // then
    result.andExpect(status().isOk())
        .andExpect(view().name(DocTypeAdminCtrl.ADD))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
        .andExpect(model().attributeHasErrors(DocTypeAdminCtrl.MODEL_ATTR));
  }
  
  // UPDATE
  @Test
  void editGetForm() throws Exception {
    // given
    given(attributeService.getNameBasedMap()).willReturn(this.attrMap);
    given(docTypeService.getByName(this.valid.getName())).willReturn(Optional.of(this.valid));
  
    // when
    ResultActions result = mvc.perform(get("/admin/doctypes/edit/{name}", this.valid.getName()));
  
    // then
    result.andExpect(status().isOk())
          .andExpect(model().attribute(DocTypeAdminCtrl.ATTRMAP_ATTR, attrMap))
          .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
          .andExpect(model().attribute(DocTypeAdminCtrl.EDIT_ATTR, true))
          .andExpect(view().name(DocTypeAdminCtrl.ADD));
  }
  
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void editGetFormNonExistingName() throws Exception {
    //given
    String name = "invalid";
    given(docTypeService.getByName(name)).willReturn(Optional.empty());
    
    // when
    ResultActions result = mvc.perform(get("/admin/doctypes/edit/{name}", name));
    
    // then
    result.andExpect(status().isFound())
          .andExpect(flash().attribute("fail", "edit.failed"))
          .andExpect(redirectedUrl("/admin/doctypes"));
  }
  
  @Test
  void editPostFormSuccess() throws Exception {
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/edit/"+this.valid.getName(), this.valid));
    
    // then
    result.andExpect(status().isFound())
        .andExpect(flash().attribute("success", "edit.success"))
        .andExpect(redirectedUrl("/admin/doctypes"));
  }
  
  @Test
  void editPostFormNonMatchingNames() throws Exception {
    // when
    MockHttpServletRequestBuilder post = postForm("/admin/doctypes/edit/invalid", this.valid);
    ResultActions result = mvc.perform(post);
    
    // then
    result.andExpect(status().isOk())
        .andExpect(view().name(DocTypeAdminCtrl.ADD))
        .andExpect(model().attribute(DocTypeAdminCtrl.EDIT_ATTR, true))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeHasFieldErrorCode(DocTypeAdminCtrl.MODEL_ATTR, "name", "mismatch.name"));
  }
  
  @Test
  void editPostFormBindingErrors() throws Exception {
    // given
    // simulate a validator error needing handling
    doAnswer(new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        Errors errors = (Errors) invocationOnMock.getArguments()[1];
        errors.reject("forcing some error");
        return null;
      }
    }).when(docTypeFormValidator).validate(any(DocTypeForm.class), any(Errors.class));
    
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/edit/"+this.valid.getName(), valid));
    
    // then
    result.andExpect(status().isOk())
        .andExpect(view().name(DocTypeAdminCtrl.ADD))
        .andExpect(model().attribute(DocTypeAdminCtrl.EDIT_ATTR, true))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
        .andExpect(model().hasErrors());
  }
  
  @Test
  void editPostFormExceptionDTO() throws Exception {
    // given
    given(docTypeService.saveEdit(this.valid)).willThrow(RuntimeException.class);
    
    // when
    ResultActions result = mvc.perform(postForm("/admin/doctypes/edit/"+this.valid.getName(), valid));
    
    // then
    result.andExpect(status().isOk())
        .andExpect(view().name(DocTypeAdminCtrl.ADD))
        .andExpect(model().attribute(DocTypeAdminCtrl.EDIT_ATTR, true))
        .andExpect(model().attribute(DocTypeAdminCtrl.MODEL_ATTR, this.valid))
        .andExpect(model().attributeHasErrors(DocTypeAdminCtrl.MODEL_ATTR));
  }
  
  // DELETE
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void deleteSuccess() throws Exception {
    // when
    ResultActions result = mvc.perform(get("/admin/doctypes/delete/{type}", this.valid.getName()));
  
    // then
    result.andExpect(status().isFound())
        .andExpect(flash().attribute("success", "delete.success"))
        .andExpect(redirectedUrl("/admin/doctypes"));
  }
  
  @Test
  @MockitoSettings(strictness = Strictness.LENIENT)
  void deleteExceptionDTO() throws Exception {
    // given
    willThrow(RuntimeException.class).given(docTypeService).deleteById(this.valid.getName());
    
    // when
    ResultActions result = mvc.perform(get("/admin/doctypes/delete/{type}", this.valid.getName()));
  
    // then
    result.andExpect(status().isFound())
        .andExpect(flash().attribute("fail", "delete.failed"))
        .andExpect(redirectedUrl("/admin/doctypes"));
  }
  
  /**
   * Generate a map of attributes from a list, like in {@link AttributeService}.
   * This is easier to use without any special mocking, etc.
   * @param attrs
   * @return
   */
  public static HashMap<String, Attribute> attributeMap(List<Attribute> attrs) {
    HashMap<String, Attribute> map = new HashMap<>();
    for(Attribute a : attrs) {
      map.put(a.getName(), a);
    }
    return map;
  }
}
