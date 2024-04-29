package org.example.spring_task.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubResponse {
  private List<Item> items;

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Item {
    @JsonProperty("full_name")
    private String fullName;

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
  }
}
