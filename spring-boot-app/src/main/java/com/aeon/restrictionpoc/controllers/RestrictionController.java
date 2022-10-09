package com.aeon.restrictionpoc.controllers;

import com.aeon.restrictionpoc.domain.AddressRestriction;
import com.aeon.restrictionpoc.mappers.AddressRestrictionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestrictionController {

    @Autowired
    private AddressRestrictionMapper restrictionMapper;

    @GetMapping("/restrictions")
    public List<AddressRestriction> restrictions() {
        return restrictionMapper.findAll();
    }
}
