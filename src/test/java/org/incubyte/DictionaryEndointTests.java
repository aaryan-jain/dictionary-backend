package org.incubyte;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
public class DictionaryEndointTests {

  @Client("/")
  @Inject
  HttpClient httpClient;

  @BeforeEach
  public void init() {
    this.httpClient.toBlocking().retrieve(HttpRequest.DELETE("/words"));
  }

  @Test
  public void delete_all_words() {
    String response = this.httpClient.toBlocking().retrieve(HttpRequest.DELETE("/words"));

    List<Word> words =
        this.httpClient
            .toBlocking()
            .retrieve(HttpRequest.GET("/words/all-words"), Argument.listOf(Word.class));

    Assertions.assertThat(words).isEmpty();
    Assertions.assertThat(response).isEqualTo("deleted all");
  }

  @Test
  public void delete_word_based_on_id_when_http_method_is_called_on_an_endpoint() {
    SaveResponseObject saveResponseObject =
        this.httpClient
            .toBlocking()
            .retrieve(
                HttpRequest.POST("/words/save", new Word("Apple")),
                Argument.of(SaveResponseObject.class));
    Word responseWord = saveResponseObject.getWord().get();
    System.out.println("responseWord.getId() = " + responseWord.getId());
       this.httpClient.toBlocking().retrieve(HttpRequest.DELETE("/words/" + responseWord.getId()));
  }

  @Test
  public void update_word_when_calling_http_endpoint() {
    SaveResponseObject saveResponseObject =
        this.httpClient
            .toBlocking()
            .retrieve(
                HttpRequest.POST("/words/save", new Word("Yvonne")),
                Argument.of(SaveResponseObject.class));
    Word responseWord = saveResponseObject.getWord().get();
    Word word =
        this.httpClient
            .toBlocking()
            .retrieve(HttpRequest.GET("/words/" + responseWord.getId()), Argument.of(Word.class));
    word.setWord("Mississipi");
    UpdateResponseObject updateResponseObject =
        this.httpClient
            .toBlocking()
            .retrieve(
                HttpRequest.PUT("/words/" + word.getId(), word),
                Argument.of(UpdateResponseObject.class));

    Assertions.assertThat(updateResponseObject.getMessage()).isEqualTo("Updated word.");
    Assertions.assertThat(updateResponseObject.getError()).isFalse();
    Assertions.assertThat(updateResponseObject.getWord().get().getWord())
        .isEqualTo(word.getWord());
  }

  @Test
  public void find_word_based_on_id_after_calling_endpoint_http() {
    SaveResponseObject saveResponseObject =
        this.httpClient
            .toBlocking()
            .retrieve(
                HttpRequest.POST("/words/save", new Word("Burgundy")),
                Argument.of(SaveResponseObject.class));
    Word responseWord = saveResponseObject.getWord().get();

    Word word =
        this.httpClient
            .toBlocking()
            .retrieve(HttpRequest.GET("/words/" + responseWord.getId()), Argument.of(Word.class));

    System.out.println("wordID = " + word);
    assertThat(word.getWord()).isEqualTo(responseWord.getWord());
    Assertions.assertThat(word.getId()).isEqualTo(responseWord.getId());
  }

  @Test
  public void get_list_of_all_words_when_hit_endpoint() {
    this.httpClient
        .toBlocking()
        .retrieve(
            HttpRequest.POST("/words/save", new Word("Amanda")),
            Argument.of(SaveResponseObject.class));
    this.httpClient
        .toBlocking()
        .retrieve(
            HttpRequest.POST("/words/save", new Word("Olivia")),
            Argument.of(SaveResponseObject.class));
    this.httpClient
        .toBlocking()
        .retrieve(
            HttpRequest.POST("/words/save", new Word("Naomi")),
            Argument.of(SaveResponseObject.class));

    List<Word> words =
        this.httpClient
            .toBlocking()
            .retrieve(HttpRequest.GET("/words/all-words"), Argument.listOf(Word.class));
    Word word1 = words.get(0);
    Word word2 = words.get(1);
    Word word3 = words.get(2);

    assertThat(word1.getWord()).isEqualTo("Amanda");
    assertThat(word2.getWord()).isEqualTo("Olivia");
    assertThat(word3.getWord()).isEqualTo("Naomi");
  }

  @Test
  public void save_words_when_hit_save_endpoint() {
    Word word = new Word("Amanda");
    SaveResponseObject saveResponseObject =
        this.httpClient
            .toBlocking()
            .retrieve(HttpRequest.POST("/words/save", word), Argument.of(SaveResponseObject.class));
    assertThat(saveResponseObject.getWord()).isPresent();
    assertThat(saveResponseObject.getWord().get().getWord()).isEqualTo("Amanda");
    assertThat(saveResponseObject.getWord().get().getId()).isGreaterThan(0);
    assertThat(saveResponseObject.getMessage()).isEqualTo("Saved word.");
    assertThat(saveResponseObject.getError()).isFalse();
  }

  @Test
  public void get_list_of_words_based_on_a_substring_when_searched_at_an_endpoint() {
    String wordSubstring = "f";
    this.httpClient
        .toBlocking()
        .retrieve(
            HttpRequest.POST("/words/save", new Word("Flesh")),
            Argument.of(SaveResponseObject.class));
    this.httpClient
        .toBlocking()
        .retrieve(
            HttpRequest.POST("/words/save", new Word("Racoon")),
            Argument.of(SaveResponseObject.class));
    this.httpClient
        .toBlocking()
        .retrieve(
            HttpRequest.POST("/words/save", new Word("flAsk")),
            Argument.of(SaveResponseObject.class));

    List<Word> words =
        this.httpClient
            .toBlocking()
            .retrieve(
                HttpRequest.GET("/words/find-by-substring/" + wordSubstring),
                Argument.listOf(Word.class));

    Assertions.assertThat(words.size()).isEqualTo(2);
    Assertions.assertThat(words.get(0).getWord()).isEqualTo("Flesh");
    Assertions.assertThat(words.get(1).getWord()).isEqualTo("flAsk");
  }
}
