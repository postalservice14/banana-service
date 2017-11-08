package com.carsaver.banana.controller;

import com.carsaver.banana.domain.Banana;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BananaResourceAssembler extends ResourceAssemblerSupport<Banana, BananaResource> {

    public BananaResourceAssembler() {
        super(BananaController.class, BananaResource.class);
    }

    @Override
    public BananaResource toResource(Banana banana) {
        if (Objects.isNull(banana)) {
            throw new NullPointerException();
        }

        BananaResource bananaResource = createResourceWithId(banana.getId(), banana);
        bananaResource.pickedAt = banana.getPickedAt();
        bananaResource.peeled = banana.getPeeled();
        return bananaResource;
    }
}
