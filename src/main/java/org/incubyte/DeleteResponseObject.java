package org.incubyte;

public class DeleteResponseObject {
  private boolean error;
  private String message;

  public DeleteResponseObject(boolean error, String message) {
    this.error = error;
    this.message = message;
  }

  public boolean isError() {
    return error;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
