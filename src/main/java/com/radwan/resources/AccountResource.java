package com.radwan.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.radwan.exception.AccountCreationException;
import com.radwan.exception.AccountTransferException;
import com.radwan.model.Account;
import com.radwan.request.AccountTransferRequest;
import com.radwan.request.NewAccountRequest;
import com.radwan.service.BankingService;
import com.radwan.service.BankingServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/account")
@Produces(MediaType.APPLICATION_JSON)
@Api(value="Account", description = "Accounts management APIs")
public class AccountResource {

    @Inject
    private BankingService bankingService = new BankingServiceImpl();

    @GET
    @Timed
    @ApiOperation(value="get an Account using the accountId", response = Account.class)
    public Response get(@QueryParam("id") @NotNull String accountId) {
        return Response.ok(bankingService.get(accountId)).build();
    }

    @POST
    @Timed
    @ApiOperation(value="create new Account", response = Account.class, notes = "First Name & Last Name are not unique in this release")
    public Response create(@NotNull @Valid NewAccountRequest newAccountRequest) throws AccountCreationException {
        Account account = Account.builder().ownerFirstName(newAccountRequest.getOwnerFirstName())
                .ownerLastName(newAccountRequest.getOwnerLastName())
                .balance(newAccountRequest.getBalance()).build();

        return Response.ok(bankingService.create(account)).build();
    }

    @POST
    @Timed
    @Path("/actions/transfer/invoke")
    @ApiOperation(value="transfer money between two accounts")
    public Response transfer(@NotNull @Valid AccountTransferRequest accountTransferRequest) throws AccountTransferException {
        bankingService.transfer(accountTransferRequest.getFrom(),
                accountTransferRequest.getTo(), accountTransferRequest.getAmount());
        return Response.ok().build();
    }
}
