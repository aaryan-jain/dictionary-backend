package org.incubyte;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@MicronautTest
public class DictionaryRepositoryShould {
  @Inject DictionaryRepository dictionaryRepository;

  @Test
  public void that_all_words_with_a_substring_in_it_will_be_returned() {
    String wordSubstring = "Fl";
    this.dictionaryRepository.save(new Word("Flander"));
    this.dictionaryRepository.save(new Word("Fluke"));
    this.dictionaryRepository.save(new Word("Ramona"));

    List<Word> words = this.dictionaryRepository.findByWordContains(wordSubstring);
    Assertions.assertThat(words.size()).isEqualTo(2);
  }

  @Test
  public void that_all_words_with_a_substring_in_it_will_be_returned_without_worrying_about_case() {
    String wordSubstring = "fl";
    this.dictionaryRepository.save(new Word("Flander"));
    this.dictionaryRepository.save(new Word("Fluke"));
    this.dictionaryRepository.save(new Word("Ramona"));

    List<Word> words =
        this.dictionaryRepository.listOfWordsContainingASubstringIgnoreCase(wordSubstring);

    System.out.println("words = " + words);

    Assertions.assertThat(words.size()).isEqualTo(2);
  }
}
