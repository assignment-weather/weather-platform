package com.weather.weather.exceptions;

import lombok.AllArgsConstructor;
import lombok.Generated;

@AllArgsConstructor
@Generated
public class CityNotFoundException extends RuntimeException {

    private String message;
}
