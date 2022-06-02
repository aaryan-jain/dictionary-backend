package org.incubyte;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DictionaryServiceShould {

  Word word1;
  Word word4;
  Word word5;
  Word word6;
  Word word7;
  List<Word> words = new ArrayList<>();
  @Mock private DictionaryRepository dictionaryRepository;

  @BeforeEach
  public void init() {
    word1 = new Word("Alexa");
    word4 = new Word("Alexandra");
    word5 = new Word("Brittany");
    word6 = new Word("Camille");
    word7 = new Word("Daisy");
    words.add(word1);
    words.add(word4);
    words.add(word5);
    words.add(word6);
    words.add(word7);
  }

  @Test
  public void all_words_returned_from_repository_when_service_called() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    Iterable<Word> words = dictionaryService.fetchAllWordsFromDB();
    verify(dictionaryRepository).findAll();
  }

  @Test
  public void word_is_not_saved_in_repository_if_it_is_does_contain_numbers() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    Word word = new Word("abc123");
    SaveResponseObject saveResponseObject = dictionaryService.saveWordToDB(word);
    assertThat(saveResponseObject.getError()).isTrue();
    assertThat(saveResponseObject.getWord()).isNotPresent();
    assertThat(saveResponseObject.getMessage())
        .isEqualTo("Word contains characters other than alphabets");
  }

  @Test
  public void word_is_saved_in_repository_if_it_is_valid() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);

    Word word = new Word("abcDefGHJI");
    SaveResponseObject saveResponseObject = dictionaryService.saveWordToDB(word);

    assertThat(saveResponseObject.getError()).isFalse();
    assertThat(saveResponseObject.getWord()).isPresent();
    assertThat(saveResponseObject.getWord().get().getWord()).isEqualTo(word.getWord());
    assertThat(saveResponseObject.getMessage()).isEqualTo("Saved word.");

    verify(dictionaryRepository).save(word);
  }

  @Test
  public void word_is_not_saved_if_it_is_already_saved() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    Word word = new Word("Helen");
    SaveResponseObject saveResponseObject = dictionaryService.saveWordToDB(word);
    assertThat(saveResponseObject.getMessage()).isEqualTo("Saved word.");
    word = new Word("Helen");
    when(dictionaryRepository.save(word))
        .thenThrow(new PersistenceException("could not execute statement"));
    SaveResponseObject saveResponseObject1 = dictionaryService.saveWordToDB(word);
    Assertions.assertThat(saveResponseObject1.getError()).isTrue();
    Assertions.assertThat(saveResponseObject1.getMessage())
        .isEqualTo("could not execute statement");
  }

  @Test
  public void find_word_based_on_wordName() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    String name = "Flash";
    Optional<Word> returnWord = Optional.of(word1);
    when(dictionaryRepository.find(name)).thenReturn(returnWord.get());
    Optional<Word> word = dictionaryService.findWordBasedOnName(name);
    Assertions.assertThat(word.get()).isEqualTo(returnWord.get());
  }

  @Test
  public void find_word_based_on_id() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    when(dictionaryRepository.findById(1L)).thenReturn(Optional.of(new Word("Farzi", 1L)));

    Optional<Word> maybeword = dictionaryService.findWordBasedOnId(1L);
    //    System.out.println("maybeword = " + maybeword.get().getWord());
    Assertions.assertThat(maybeword.get().getWord()).isEqualTo("Farzi");
    Assertions.assertThat(maybeword.get().getId()).isEqualTo(1L);
  }

  @Test
  public void find_list_of_words_based_on_substring() {

    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    when(dictionaryRepository.listOfWordsContainingASubstringIgnoreCase("alex"))
        .thenReturn(words.subList(0, 2));
    List<Word> wordList = dictionaryService.fetchWordsBasedOnSubstringFromDB("alex");
    Assertions.assertThat(wordList.get(0).getWord()).containsIgnoringCase("alex");
    Assertions.assertThat(wordList.get(1).getWord()).containsIgnoringCase("alex");
  }

  @Test
  public void update_word_in_db_using_word_id() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    SaveResponseObject sro = dictionaryService.saveWordToDB(new Word("happy"));

    Word word = sro.getWord().get();
    word.setWord("sad");

    UpdateResponseObject uro = dictionaryService.updateWordUsingId(word, 1L);
//    System.out.println("uro.getMessage() = " + uro.getMessage());
    Assertions.assertThat(uro.getWord().get().getWord()).isEqualTo("sad");
  }

  @Test
  public void delete_word_from_db_using_word_object() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    SaveResponseObject sro = dictionaryService.saveWordToDB(new Word("Hello"));
    Word toBeDeleted = sro.getWord().get();
    dictionaryService.deleteWordFromDB(toBeDeleted);
    Optional<Word> isDeleted = dictionaryService.findWordBasedOnId(toBeDeleted.getId());

    Assertions.assertThat(isDeleted).isNotPresent();
  }

  @Test
  public void delete_all_words_from_db() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    dictionaryService.deleteAllWordsFromDB();
    Iterable<Word> words = dictionaryService.fetchAllWordsFromDB();
    long size = StreamSupport.stream(words.spliterator(), false).count();

    Assertions.assertThat(size).isZero();
  }

  @Test
  public void word_invalid_if_it_contains_numbers() {
    DictionaryService dictionaryService = new DictionaryService(dictionaryRepository);
    boolean valid = dictionaryService.isWordValid(new Word("abc123"));
    assertThat(valid).isFalse();

    valid = dictionaryService.isWordValid(new Word("1"));
    assertThat(valid).isFalse();

    valid = dictionaryService.isWordValid(new Word("abcDefG"));
    assertThat(valid).isTrue();

    valid = dictionaryService.isWordValid(new Word("AAAbbb"));
    assertThat(valid).isTrue();

    valid = dictionaryService.isWordValid(new Word("ab-cd"));
    assertThat(valid).isFalse();

    valid = dictionaryService.isWordValid(new Word("/n"));
    assertThat(valid).isFalse();
  }
}
