package com.example.fcss_02.controller.request;

import com.example.fcss_02.annotation.CustomEncryption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HelloRequestBody {
    private String id;

    @CustomEncryption
    private String password;
}
