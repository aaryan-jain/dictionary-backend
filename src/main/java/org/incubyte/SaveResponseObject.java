package org.incubyte;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SaveResponseObject {

  @JsonProperty("word")
  private Optional<Word> word;

  @JsonProperty("message")
  private String message;

  @JsonProperty("error")
  private boolean error;

  public SaveResponseObject(
      @JsonProperty boolean error, @JsonProperty String message, @JsonProperty Word word) {
    this.error = error;
    this.message = message;
    this.word = Optional.of(word);
  }

  public SaveResponseObject(@JsonProperty boolean error, @JsonProperty String message) {
    this.error = error;
    this.message = message;
    this.word = Optional.empty();
  }

  // for jackson
  public SaveResponseObject() {}

  @JsonGetter
  public Optional<Word> getWord() {
    return word;
  }

  @JsonGetter
  public String getMessage() {
    return message;
  }

  @JsonGetter
  public boolean getError() {
    return error;
  }
}
