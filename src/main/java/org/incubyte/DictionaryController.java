package org.incubyte;


import io.micronaut.http.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller("/words")
public class DictionaryController {

  private final DictionaryService dictionaryService;

  public DictionaryController(DictionaryService dictionaryService) {
    this.dictionaryService = dictionaryService;
  }

  @Get("/all-words")
  public Iterable<Word> fetchAllWords() {
    return this.dictionaryService.fetchAllWordsFromDB();
  }

  @Post("/save")
  public SaveResponseObject saveWord(Word word) {
    return this.dictionaryService.saveWordToDB(word);
  }

  @Get("/find-by-substring/{substring}")
  public List<Word> fetchWordsBasedOnSubstring(String substring) {
    return this.dictionaryService.fetchWordsBasedOnSubstringFromDB(substring);
  }

  @Get("/{id}")
  public Optional<Word> findWordById(Long id) {
    return this.dictionaryService.findWordBasedOnId(id);
  }

  @Put("/{id}")
  public UpdateResponseObject updateWord(Long id, Word word) {
    return this.dictionaryService.updateWordUsingId(word, id);
  }

  @Delete("/{id}")
  public String deleteWord(Long id) throws Exception {
    Optional<Word> maybeWord = this.dictionaryService.findWordBasedOnId(id);
    if(maybeWord.isEmpty()) {
      throw new Exception("could not find resource");
    }
    this.dictionaryService.deleteWordFromDB(maybeWord.get());

    return "deleted";
  }

  @Delete("/")
  public String deleteAllWords() {
    this.dictionaryService.deleteAllWordsFromDB();
    return "deleted all";
  }
}
