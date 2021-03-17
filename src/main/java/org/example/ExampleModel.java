package org.example;

import java.io.Serializable;

public class ExampleModel implements Serializable {
  public String message;

  public ExampleModel(String message) {
    this.message = message;
  }
}
