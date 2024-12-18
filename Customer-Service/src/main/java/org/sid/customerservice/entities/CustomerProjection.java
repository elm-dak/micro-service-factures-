package org.sid.customerservice.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "c1", types = Customer.class)
public interface CustomerProjection {
    public Long getId();
    public String getName();
}
