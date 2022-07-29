package com.gdemarcsek.appsec.visibility.demo.resources;

import com.gdemarcsek.appsec.visibility.demo.core.Person;
import com.gdemarcsek.appsec.visibility.demo.core.User;
import com.gdemarcsek.appsec.visibility.demo.db.PersonDAO;
import com.gdemarcsek.appsec.visibility.demo.presentation.CreatePersonDto;
import com.gdemarcsek.appsec.visibility.demo.presentation.GetPersonDto;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.modelmapper.ModelMapper;


@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
@PermitAll
public class PersonResource {
    private final PersonDAO peopleDAO;
    private final ModelMapper modelMapper;

    @Inject
    public PersonResource(PersonDAO dao, ModelMapper mm) {
        this.peopleDAO = dao;
        this.modelMapper = mm;
    }

    @POST
    @UnitOfWork
    @Operation(description = "Adds a person", responses = {@ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetPersonDto.class)))})
    @RolesAllowed({"ADMIN"})
    @Valid
    @Path("/")
    public GetPersonDto createPerson(
            @Valid @RequestBody(description = "Create Person DTO", content = @Content(schema = @Schema(implementation = CreatePersonDto.class)))
                CreatePersonDto person
    ) {
        Person p = this.peopleDAO.create(modelMapper.map(person, Person.class));
        
        return modelMapper.map(p, GetPersonDto.class);
    }

    @Path("/{id}")
    @UnitOfWork
    @GET
    @Valid
    @Operation(description = "Gets a person", responses = {@ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetPersonDto.class)))})
    public GetPersonDto getOnePerson(@PathParam("id") @Valid UUID id, @Auth Optional<User> user) {
        Person p = this.peopleDAO.findById(id).orElseThrow(() -> new NotFoundException("No such person"));
        
        return modelMapper.map(p, GetPersonDto.class);
    }
}