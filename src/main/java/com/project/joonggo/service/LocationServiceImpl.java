package com.project.joonggo.service;

import com.project.joonggo.repository.LocationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService{

    private final LocationMapper locationMapper;

    @Override
    public String getStreetAddress(String userId) {
        return locationMapper.getStreetAddress(userId);
    }
}
