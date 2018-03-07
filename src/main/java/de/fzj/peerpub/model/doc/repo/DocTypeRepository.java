package de.fzj.peerpub.model.doc.repo;

import java.util.List;

import de.fzj.peerpub.model.doc.DocType;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocTypeRepository extends MongoRepository<DocType, String> {

    public DocType findByName(String name);
    public List<DocType> findBySystem(Boolean system);
    public List<DocType> findByMultidoc(Boolean multidoc);

}
