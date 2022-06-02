package org.incubyte;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DictionaryControllerShould {

  @Mock DictionaryService dictionaryService;

  @Test
  public void return_word_by_using_service_when_found_by_id() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    Word word = new Word("Mercy");
    when(dictionaryService.saveWordToDB(word))
        .thenReturn(new SaveResponseObject(false, "Saved word", word));
    SaveResponseObject savedResponseObject = dictionaryController.saveWord(word);
    Optional<Word> wordMaybe =
        dictionaryController.findWordById(savedResponseObject.getWord().get().getId());
    verify(dictionaryService).findWordBasedOnId(savedResponseObject.getWord().get().getId());
  }

  @Test
  public void return_list_of_words_using_service_when_all_words_are_called_using_http() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    Iterable<Word> wordList = dictionaryController.fetchAllWords();
    verify(dictionaryService).fetchAllWordsFromDB();
  }

  @Test
  public void return_saved_response_when_words_are_saved_and_call_service_for_that() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    Word word = new Word("Dylan");
    SaveResponseObject savedResponseObject = dictionaryController.saveWord(word);
    verify(dictionaryService).saveWordToDB(word);
  }

  @Test
  public void return_list_of_words_containing_substring_using_service() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    String substring = "someSub";
    List<Word> words = dictionaryController.fetchWordsBasedOnSubstring(substring);
    verify(dictionaryService).fetchWordsBasedOnSubstringFromDB(substring);
  }

  @Test
  public void update_word_based_on_id_with_another_wordName_using_service() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    //    Word word = new Word("Stress");
    when(this.dictionaryService.findWordBasedOnId(1L))
        .thenReturn(Optional.of(new Word("Stress", 1L)));
    Word word = dictionaryController.findWordById(1L).get();
    word.setWord("Free");
    UpdateResponseObject updatedWord = dictionaryController.updateWord(1L, word);
    verify(dictionaryService).updateWordUsingId(word, 1L);
  }

  @Test
  public void delete_word_with_id_using_service() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    when(this.dictionaryService.findWordBasedOnId(1L))
        .thenReturn(Optional.of(new Word("Stress", 1L)));
    Word word = dictionaryController.findWordById(1L).get();
    try {
      dictionaryController.deleteWord(word.getId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    verify(dictionaryService).deleteWordFromDB(word);
  }

  @Test
  public void delete_word_returns_different_string_if_word_not_found() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    when(this.dictionaryService.findWordBasedOnId(1L)).thenReturn(Optional.empty());


    Exception e = assertThrows(Exception.class, ()-> {dictionaryController.deleteWord(1L);});

    String actualMessage = e.getMessage();
    String expectedMessage = "could not find resource";

    Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
  }

  @Test
  public void delete_all_words() {
    DictionaryController dictionaryController = new DictionaryController(dictionaryService);
    dictionaryController.deleteAllWords();
    verify(this.dictionaryService).deleteAllWordsFromDB();
  }
}
