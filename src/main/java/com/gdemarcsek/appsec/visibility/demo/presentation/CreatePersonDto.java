package com.gdemarcsek.appsec.visibility.demo.presentation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CreatePersonDto {
      @NotNull
      @JsonProperty("name")
      @Size(min = 1, max = 100)
      private String fullName;

      @Min(value = 0)
      @Max(value = 9999)
      private int yearBorn;

      @NotNull
      private String jobTitle;
}
