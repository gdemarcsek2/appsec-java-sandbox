package com.gdemarcsek.appsec.visibility.demo.resources;

import com.gdemarcsek.appsec.visibility.demo.core.Person;
import com.gdemarcsek.appsec.visibility.demo.db.PersonDAO;
import com.gdemarcsek.appsec.visibility.demo.presentation.CreatePersonDto;
import com.gdemarcsek.appsec.visibility.demo.presentation.GetPersonDto;

import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.modelmapper.ModelMapper;


@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    private final PersonDAO peopleDAO;

    private final ModelMapper modelMapper;

    public PersonResource(PersonDAO dao, ModelMapper mm) {
        this.peopleDAO = dao;
        this.modelMapper = mm;
    }

    @POST
    @UnitOfWork
    @Operation(description = "Adds a person", responses = {@ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetPersonDto.class)))})
    @RolesAllowed({"ADMIN"})
    public GetPersonDto createPerson(
            @RequestBody(description = "Create Person DTO", content = @Content(schema = @Schema(implementation = CreatePersonDto.class)))
                CreatePersonDto person
    ) {
        Person p = this.peopleDAO.create(modelMapper.map(person, Person.class));
        return modelMapper.map(p, GetPersonDto.class);
    }
}