package com.aeon.restrictionpoc.mappers;

import com.aeon.restrictionpoc.domain.AddressRestriction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressRestrictionMapper {

    List<AddressRestriction> findAll();

    int insert(AddressRestriction restriction);
}
