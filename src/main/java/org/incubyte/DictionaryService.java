package org.incubyte;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class DictionaryService {
  private final DictionaryRepository dictionaryRepository;

  public DictionaryService(DictionaryRepository dictionaryRepository) {
    this.dictionaryRepository = dictionaryRepository;
  }

  public Iterable<Word> fetchAllWordsFromDB() {
    return this.dictionaryRepository.findAll();
  }

  public SaveResponseObject saveWordToDB(Word word) {

    if (isWordValid(word)) {
      try {
        this.dictionaryRepository.save(word);
      } catch (Exception e) {
        return new SaveResponseObject(true, e.getMessage());
      }
      SaveResponseObject saveResponseObject = new SaveResponseObject(false, "Saved word.", word);
      return saveResponseObject;
    } else {
      SaveResponseObject saveResponseObject =
          new SaveResponseObject(true, "Word contains characters other than alphabets");
      return saveResponseObject;
    }
  }

  public boolean isWordValid(Word word) {
    String wordTemp = word.getWord();
    Pattern pattern = Pattern.compile("[a-zA-Z]+", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(wordTemp);
    return matcher.matches();
  }

  public Optional<Word> findWordBasedOnName(String name) {
    //      throw new UnsupportedOperationException();
    Word word = this.dictionaryRepository.find(name);
    if (word == null) {
      return Optional.empty();
    }
    return Optional.of(word);
  }

  public List<Word> fetchWordsBasedOnSubstringFromDB(String substring) {
    return this.dictionaryRepository.listOfWordsContainingASubstringIgnoreCase(substring);
  }

  public Optional<Word> findWordBasedOnId(Long id) {
    return this.dictionaryRepository.findById(id);
  }

  public UpdateResponseObject updateWordUsingId(Word word, Long id) {
    if (isWordValid(word)) {
      try {
          this.dictionaryRepository.update(new Word(word.getWord(), id));
      } catch (Exception e) {
        return new UpdateResponseObject(true, e.getMessage());
      }
      UpdateResponseObject updateResponseObject =
          new UpdateResponseObject(
              false, "Updated word.", word);
      return updateResponseObject;
    } else {
      UpdateResponseObject updateResponseObject =
          new UpdateResponseObject(true, "Word contains characters other than alphabets");
      return updateResponseObject;
    }
  }

  public void deleteWordFromDB(Word word) {
    this.dictionaryRepository.delete(word);
  }

  public void deleteAllWordsFromDB() {
    this.dictionaryRepository.deleteAll();
  }
}
