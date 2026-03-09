package com.blps.blps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDto {

    private Long id;

    @NotBlank(message = "Город обязателен")
    private String city;

    @NotBlank(message = "Улица обязательна")
    private String street;

    @NotNull(message = "Номер дома обязателен")
    private Integer building;

    private String floor;
    private String apartment;

    @NotNull(message = "Широта обязательна")
    private Double latitude;

    @NotNull(message = "Долгота обязательна")
    private Double longitude;
}
