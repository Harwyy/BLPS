package com.blps.blps.mapper;

import com.blps.blps.dto.AddressDto;
import com.blps.blps.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressDto dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setCity(dto.getCity());
        address.setStreet(dto.getStreet());
        address.setBuilding(dto.getBuilding());
        address.setFloor(dto.getFloor());
        address.setApartment(dto.getApartment());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        return address;
    }

    public AddressDto toDto(Address address) {
        if (address == null) return null;
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setCity(address.getCity());
        dto.setStreet(address.getStreet());
        dto.setBuilding(address.getBuilding());
        dto.setFloor(address.getFloor());
        dto.setApartment(address.getApartment());
        dto.setLatitude(address.getLatitude());
        dto.setLongitude(address.getLongitude());
        return dto;
    }
}
