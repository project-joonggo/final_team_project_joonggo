package com.project.joonggo.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocationMapper {
    String getStreetAddress(int userNum);
}
