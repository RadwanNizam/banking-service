package com.radwan.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferRequest {
    @NotNull
    private String source;

    @NotNull
    private String target;

    @NotNull
    @Min(1)
    private Double amount;
}
