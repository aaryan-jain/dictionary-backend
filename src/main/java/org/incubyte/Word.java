package org.incubyte;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Word {
  @Column(unique = true)
  private String word;

  @Id @GeneratedValue private Long id;

  public Word(String word) {
    this.word = word;
  }

  private Word() {}

  public Word(String word, long id) {
    this.word = word;
    this.id = id;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public Long getId() {
    return id;
  }
}
