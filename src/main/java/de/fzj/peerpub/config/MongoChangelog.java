/*
 * Copyright (c) 2018 "Forschungszentrum Jülich GmbH". All rights reserved.
 *
 * This file is part of PeerPub, licensed under the GNU Affero General Public
 * License v3 or later. Refer to the included LICENSE file for full text of license.
 */

package de.fzj.peerpub.config;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Mongobee based change sets that migrate between different database schema versions.
 */
@ChangeLog
public class MongoChangelog {
  
  @ChangeSet(order = "001", id = "initialImport", author = "obertuch")
  public void importInitialDataSet(MongoTemplate mongoTemplate) throws Exception {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    
    // get json resources from the classpath
    Resource[] res = resolver.getResources("classpath*:migrations/001-*.json");
    
    // iterate over the files
    for (Resource r : res) {
      // parse json from the file
      JsonObject jsonFile = Json.parse(new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8)).asObject();
      
      // save all documents to the collection referenced in the top key
      for (JsonObject.Member member : jsonFile) {
        String collection = member.getName();
        JsonArray docs = member.getValue().asArray();
        for (JsonValue doc : docs) {
          mongoTemplate.save(doc.toString(), collection);
        }
      }
    }
  }
}
