package de.fzj.peerpub.config;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configure Hibernate JSR-303 validation (primarily messages)
 */
@Configuration
public class ValidationConfig {
  
  /**
   * MessageSourceProperties is built from @Value("spring.messages"), thus
   * reuse this for our configuration.
   */
  @Autowired
  private MessageSourceProperties properties;
  
  /**
   * Create custom Hibernate JSR303 validator with custom message location.
   * Reuse the MessageSourceProperties bean as does Spring Boot in MessageSourceAutoConfiguration.java
   * @return Hibernate Validator
   */
  @Bean
  public Validator validator() {
    List<String> messages = new ArrayList<>();
    
    if (StringUtils.hasText(properties.getBasename())) {
      Collections.addAll(messages, StringUtils.commaDelimitedListToStringArray(
                                      StringUtils.trimAllWhitespace(properties.getBasename())));
    }
  
    javax.validation.Configuration<?> config = Validation.byDefaultProvider().configure();
    config.messageInterpolator(new ResourceBundleMessageInterpolator(new AggregateResourceBundleLocator(messages)));
  
    ValidatorFactory factory = config.buildValidatorFactory();
    return factory.getValidator();
  }
  
  /**
   * Invoke post processor for validation, so we do not need to specify a validation.xml configuration
   * but can stick to Spring Boot configuration
   * @return
   */
  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
    methodValidationPostProcessor.setValidator(validator());
    return methodValidationPostProcessor;
  }
}
