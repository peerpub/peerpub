package de.fzj.peerpub.model.doc.repo;

import java.util.List;

import de.fzj.peerpub.model.doc.Attribute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttributeRepository extends MongoRepository<Attribute, String> {

    public Attribute findByName(String name);
    public List<Attribute> findByLabel(String label);
    public List<Attribute> findByKey(String key);

}