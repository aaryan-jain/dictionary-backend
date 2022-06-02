package org.incubyte;

public class UpdateResponseObject extends SaveResponseObject {
  public UpdateResponseObject(boolean b, String message, Word word) {
    super(b, message, word);
  }

  public UpdateResponseObject(boolean b, String message) {
    super(b, message);
  }

  private UpdateResponseObject() {}
}
