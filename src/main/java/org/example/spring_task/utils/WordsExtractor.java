package org.example.spring_task.utils;

import java.util.ArrayList;
import java.util.List;


public class WordsExtractor {
  private  String targetWord;

  public String getTargetWord() {
    return targetWord;
  }
  public void setTargetWord(String word) {
    this.targetWord = word;
  }

  public Boolean findWord(String text) {
    /*
     * Извлекает вхождения целевого слова из текста.
     *
     * @param targetWord Слово, которое нужно извлечь из текста.
     *
     * @return Массив строк, содержащий все вхождения целевого слова в текст.
     */
    // Разделяем текст на слова
    String[] words = text.split("\\s+");
    List<String> occurrences = new ArrayList<>();

    // Извлекаем вхождения целевого слова
    for (String word : words) {
      if (word.equalsIgnoreCase(targetWord)) {
        return true;
      }
    }

    return false;
  }
}
