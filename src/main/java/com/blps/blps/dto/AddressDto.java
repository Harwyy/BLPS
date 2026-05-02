package com.blps.blps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {
    private String city;
    private String street;
    private Integer building;
    private Double latitude;
    private Double longitude;
    private String floor;
    private String apartment;
}
