package de.fzj.peerpub.doc.doctype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Document Type Data Transfer Object
 */
@Service
public class DocTypeService {
  
  /**
   * Document Type DAO
   */
  @Autowired
  private DocTypeRepository docTypeRepository;
  
  /**
   * Get all document types and put into view model
   */
  public List<DocType> getAll() {
    return docTypeRepository.findAll();
  }
  
}
