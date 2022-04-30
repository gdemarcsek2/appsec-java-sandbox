package com.gdemarcsek.appsec.visibility.demo.presentation;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import com.gdemarcsek.appsec.visibility.demo.util.AuditAccess;

import io.swagger.v3.oas.annotations.media.Schema;

import com.gdemarcsek.appsec.visibility.demo.core.Sensitive;

@Data
@AuditAccess
public class GetPersonDto {
      @NotNull
      @JsonProperty("name")
      @Schema(type = "string")
      private Sensitive<String> fullName;

      private int yearBorn;

      @NotNull
      private String id;
}
