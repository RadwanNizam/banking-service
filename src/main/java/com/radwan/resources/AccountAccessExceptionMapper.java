package com.radwan.resources;

import com.radwan.exception.AccountAccessException;
import com.radwan.exception.AccountCreationException;
import io.dropwizard.jersey.errors.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AccountAccessExceptionMapper implements ExceptionMapper<AccountAccessException> {

    public AccountAccessExceptionMapper(){
    }

    @Override
    public Response toResponse(AccountAccessException e) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(e.getErrorCode(),
                        e.getLocalizedMessage()))
                .build();
    }
}
