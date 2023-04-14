package com.synpulsebankapi.auxiliary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Object used to read the JWT token from the body, not used unless we're creating
 * a new token
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Token {
    private String token;
}
