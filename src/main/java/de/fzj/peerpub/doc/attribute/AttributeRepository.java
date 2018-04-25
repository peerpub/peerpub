/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.doc.attribute;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB DAO abstraction interface for Metadata Attributes
 */
public interface AttributeRepository extends MongoRepository<Attribute, String> {

  /**
   * Retrieve a single, unique item by its name (the id attribute). If it
   * does not exist in the database, it returns an empty Optional.
   * @param name The unique name
   * @return Optional attribute
   */
  Optional<Attribute> findByName(String name);

  /**
   * Retrieve a (possible empty) list of attributes that have a given label set
   * @param label The label
   * @return (Empty) list of attributes
   */
  List<Attribute> findByLabel(String label);

  /**
   * Retrieve a (possible empty) list of attributes that have a given metadata key set
   * @param key The key
   * @return (Empty) list of attributes
   */
  List<Attribute> findByKey(String key);

}
