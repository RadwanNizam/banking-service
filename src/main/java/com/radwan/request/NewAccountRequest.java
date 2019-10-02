package com.radwan.request;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull(message = "ownerFirstName should not be null")
    @Size(message = "ownerFirstName should be between 5 and 15 characters", min=5, max = 15)
    private String ownerFirstName;

    @NotNull(message = "ownerFirstName should not be null")
    @Size(message = "ownerFirstName should be between 5 and 15 characters", min=5, max = 15)
    private String ownerLastName;

    @NotNull
    @Min(0)
    private BigDecimal balance;

}
