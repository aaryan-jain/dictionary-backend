package org.incubyte;

import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface DictionaryRepository extends CrudRepository<Word, Long> {
  @Executable
  Word find(String word);

  //    List<Word> findByWord(String word);

  List<Word> findByWordContains(String wordSubstring);

  @Query(
      value = "select * from Word w where upper(w.word) like CONCAT('%',upper(:title),'%')",
      nativeQuery = true)
  List<Word> listOfWordsContainingASubstringIgnoreCase(String title);
}
