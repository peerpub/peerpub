package de.fzj.peerpub.model.doc;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.ToString;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;

@ToString
public class DocType {
  @NonNull @Getter private String name;
  @NonNull @Getter private Boolean system = false;
  @NonNull @Getter private Boolean multidoc = false;

  private final String nameRegex = "^[a-zA-Z\\-_]+$";

  // STATIC
  /**
   * Check if a name is valid.
   * TODO: Make the regular expression configurable via settings.
   * @param: String name A name to check against the regular expression.
   * @return: true or throws IllegalArgumentException
   */
  public static Boolean checkName(String name) {
    if(!name.matches(this.nameRegex))
      throw new IllegalArgumentException();
    else
      return true;
  }

  // CONSTRUCTORS
  public DocType(String name) {
    checkName(name);
    this.name = name;
  }
  public DocType(String name, Boolean multidoc) {
    checkName(name);
    this.name = name;
    this.multidoc = multidoc;
  }
  public DocType(String name, Boolean multidoc, Boolean system) {
    checkName(name);
    this.name = name;
    this.system = system;
    this.multidoc = multidoc;
  }

  // SETTERS
  public void setName(String name) {
    if(this.system)
      throw new AssertionError("Cannot change a system type name.");
    else
      checkName(name);
      this.name = name;
  }

  public void setMultidoc(Boolean m) {
    if(this.system)
      throw new AssertionError("Cannot change a system type name.");
    else
      this.multidoc = m;
  }
}
