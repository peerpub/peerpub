package de.fzj.peerpub.doc.doctype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
  
  /**
   * Get a specific document type by its name as a form object
   * @param name The (referable) name of the document type
   */
  public Optional<DocTypeForm> getByName(@NotNull String name) {
    Optional<DocType> oDt = docTypeRepository.findByName(name);
    if (oDt.isPresent()) {
      DocType dt = oDt.get();
      return Optional.of(DocTypeForm.toForm(dt));
    }
    return Optional.empty();
  }
  
  /**
   * Save new doc type to database
   */
  public DocTypeForm saveAdd(@NotNull DocTypeForm dtf) {
    DocType dt = dtf.toType();
    if (docTypeRepository.existsById(dt.getName())) {
      throw new DuplicateKeyException("duplicate.name");
    }
    return DocTypeForm.toForm(docTypeRepository.save(dt));
  }
  
  /**
   * Save to database after editing a doc type
   */
  public DocTypeForm saveEdit(@NotNull DocTypeForm dtf) {
    DocType dt = dtf.toType();
    return DocTypeForm.toForm(docTypeRepository.save(dt));
  }
}
