package com.gdemarcsek.appsec.visibility.demo.presentation;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdemarcsek.appsec.visibility.demo.util.AuditAccess;

import lombok.Data;
import lombok.ToString;

@Data
@AuditAccess
@ToString(onlyExplicitlyIncluded = true)
public class GetPersonDto extends ResponseDto {
      @NotNull @JsonProperty("name")
      private String fullName;

      private int yearBorn;

      @NotNull @ToString.Include
      private String id;
}
