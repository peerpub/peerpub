package de.fzj.peerpub.doc.doctype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Document Type Data Transfer Object
 */
@Service
public class DocTypeService {
  
  /**
   * Document Type DAO
   */
  private DocTypeRepository docTypeRepository;
  
  /**
   * Constructor to make explicit dependency on DocTypeRepository visible
   * @param docTypeRepository
   */
  public DocTypeService(@Autowired DocTypeRepository docTypeRepository) {
    this.docTypeRepository = docTypeRepository;
  }
  
  /**
   * Get all document types and put into view model
   */
  public List<DocType> getAll() {
    return docTypeRepository.findAll();
  }
  
}
