package de.fzj.peerpub.doc.funding;

import com.mongodb.MongoException;
import de.fzj.peerpub.doc.attribute.Attribute;
import de.fzj.peerpub.doc.attribute.AttributeAdminCtrl;
import de.fzj.peerpub.doc.attribute.AttributeRepository;
import de.fzj.peerpub.doc.attribute.AttributeTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils.postForm;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Tag("medium")
public class FundingAwardAdminCtrlTest {

  @Mock
  private AwardRepository awardRepository;
  
  private FundingAwardAdminCtrl fundingAwardAdminCtrl;
  
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    this.fundingAwardAdminCtrl = new FundingAwardAdminCtrl(awardRepository);
    // MockMvc standalone approach
    this.mvc = MockMvcBuilders.standaloneSetup(this.fundingAwardAdminCtrl)
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
    List<Award> awards = AwardTest.generate(3);
    given(awardRepository.findAll()).willReturn(awards);

    // when
    ResultActions result = mvc.perform(get("/admin/funding/awards"));

    // then
    result.andExpect(status().isOk())
          .andExpect(model().attribute("awards", awards))
          .andExpect(view().name(FundingAwardAdminCtrl.LIST));
  }

  // CREATE
  @Test
  void addGetForm() throws Exception {
    // given
    // when
    ResultActions result = mvc.perform(get("/admin/funding/awards/add"));
    // then
    result.andExpect(status().isOk())
          .andExpect(model().hasNoErrors())
          .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, new Award()))
          .andExpect(view().name(FundingAwardAdminCtrl.ADD));
  }
  @Test
  void addPostFormValid() throws Exception {
    //given
    Award award = AwardTest.generate();
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/add", award));
    //then
    result.andExpect(status().isFound())
          .andExpect(model().hasNoErrors())
          .andExpect(flash().attribute("success", "add.success"))
          .andExpect(redirectedUrl("/admin/funding/awards"));
  }
  @Test
  void addPostFormDuplicateError() throws Exception {
    //given
    Award award = AwardTest.generate();
    Optional<Award> oAward = Optional.of(award);
    given(awardRepository.findById(award.getId())).willReturn(oAward);
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/add",award));
    //then
    result.andExpect(status().isOk())
          .andExpect(view().name(FundingAwardAdminCtrl.ADD))
          .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors(FundingAwardAdminCtrl.MODEL_ATTR,"id"))
          .andExpect(model().attributeHasFieldErrorCode(FundingAwardAdminCtrl.MODEL_ATTR, "id", "duplicate.name"));
  }
  @Test
  void addPostFormInvalidNameKeyError() throws Exception {
    //given
    Award award = AwardTest.generate();
    // set invalid name!
    award.setId("test_ABC +123");
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/add", award));
    //then
    result.andExpect(status().isOk())
          .andExpect(view().name(FundingAwardAdminCtrl.ADD))
          .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors(FundingAwardAdminCtrl.MODEL_ATTR, "id"))
          .andExpect(model().attributeHasFieldErrorCode(FundingAwardAdminCtrl.MODEL_ATTR, "id", "Referable"));
  }
  
  @Test
  void addPostFormPersistenceError() throws Exception {
    //given
    Award award = AwardTest.generate();
    given(awardRepository.save(award)).willThrow(RuntimeException.class);
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/add", award));
    //then
    result.andExpect(status().isOk())
          .andExpect(view().name(FundingAwardAdminCtrl.ADD))
          .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award))
          .andExpect(model().hasErrors());
  }

  // UPDATE
  @Test
  void editGetForm() throws Exception {
    // given
    Award award = AwardTest.generate();
    Optional<Award> oAward = Optional.of(award);
    given(awardRepository.findById(award.getId())).willReturn(oAward);
    // when
    ResultActions result = mvc.perform(get("/admin/funding/awards/edit/{id}", award.getId()));
    // then
    result.andExpect(status().isOk())
          .andExpect(view().name(FundingAwardAdminCtrl.ADD))
          .andExpect(model().attribute(FundingAwardAdminCtrl.EDIT_ATTR, true))
          .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award));
  }
  @Test
  void editGetFormNonExistingName() throws Exception {
    //given
    String id = "test";
    given(awardRepository.findById(id)).willReturn(Optional.empty());
    //when
    ResultActions result = mvc.perform(get("/admin/funding/awards/edit/{id}", id));
    //then
    result.andExpect(status().isFound())
        .andExpect(flash().attribute("fail", "edit.failed"))
        .andExpect(redirectedUrl("/admin/funding/awards"));
  }
  @Test
  void editPostFormSuccess() throws Exception {
    // given
    Award award = AwardTest.generate();
    // when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/edit/"+award.getId(), award));
    // then
    result.andExpect(status().isFound())
          .andExpect(model().hasNoErrors())
          .andExpect(flash().attribute("success", "edit.success"))
          .andExpect(redirectedUrl("/admin/funding/awards"));
  }
  @Test
  void editPostFormInvalidNameKeyError() throws Exception {
    //given
    Award award = AwardTest.generate();
    // set invalid name!
    award.setId("test_ABC +123");
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/edit/"+award.getId(), award));
    //then
    result.andExpect(status().isOk())
        .andExpect(view().name(FundingAwardAdminCtrl.ADD))
        .andExpect(model().attribute(FundingAwardAdminCtrl.EDIT_ATTR, true))
        .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeHasFieldErrors(FundingAwardAdminCtrl.MODEL_ATTR, "id"))
        .andExpect(model().attributeHasFieldErrorCode(FundingAwardAdminCtrl.MODEL_ATTR, "id", "Referable"));
  }
  
  @Test
  void editPostFormNameMismatchError() throws Exception {
    //given
    Award award = AwardTest.generate();
    String requestName = "test";
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/edit/"+requestName, award));
    //then
    result.andExpect(status().isOk())
        .andExpect(view().name(FundingAwardAdminCtrl.ADD))
        .andExpect(model().attribute(FundingAwardAdminCtrl.EDIT_ATTR, true))
        .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award))
        .andExpect(model().hasErrors())
        .andExpect(model().attributeHasFieldErrors(FundingAwardAdminCtrl.MODEL_ATTR, "id"))
        .andExpect(model().attributeHasFieldErrorCode(FundingAwardAdminCtrl.MODEL_ATTR, "id", "mismatch.name"));
  }
  @Test
  void editPostFormPersistenceError() throws Exception {
    //given
    Award award = AwardTest.generate();
    given(awardRepository.save(award)).willThrow(RuntimeException.class);
    //when
    ResultActions result = mvc.perform(postForm("/admin/funding/awards/edit/"+award.getId(), award));
    //then
    result.andExpect(status().isOk())
        .andExpect(view().name(FundingAwardAdminCtrl.ADD))
        .andExpect(model().attribute(FundingAwardAdminCtrl.EDIT_ATTR, true))
        .andExpect(model().attribute(FundingAwardAdminCtrl.MODEL_ATTR, award))
        .andExpect(model().hasErrors());
  }

  // DELETE
  @Test
  void deleteSuccess() throws Exception {
    //given
    String id = "test";
    /*
     * not usefull here, as deleteById() return void if successfull...
     * willThrow(new DataAccessException("test")).given(attributeRepository).deleteById(name);
     */
    //when
    ResultActions result = mvc.perform(get("/admin/funding/awards/delete/{id}",id));
    //then
    result.andExpect(status().isFound())
          .andExpect(flash().attribute("success", "delete.success"))
          .andExpect(redirectedUrl("/admin/funding/awards"));
  }
  @Test
  void deleteInvalidOrNonExistingName() throws Exception {
    //given
    String id = "test";
    willThrow(new MongoException("fault")).given(awardRepository).deleteById(id);
    //when
    ResultActions result = mvc.perform(get("/admin/funding/awards/delete/{id}", id));
    //then
    result.andExpect(status().isFound())
          .andExpect(flash().attribute("fail", "delete.failed"))
          .andExpect(redirectedUrl("/admin/funding/awards"));
  }
}
