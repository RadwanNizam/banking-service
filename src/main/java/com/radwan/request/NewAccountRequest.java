package com.radwan.request;

import lombok.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
public class NewAccountRequest {
    private String ownerFirstName;
    private String ownerLastName;
    private BigDecimal balance;

}
