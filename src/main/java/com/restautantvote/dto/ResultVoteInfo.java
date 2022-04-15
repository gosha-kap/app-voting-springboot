package com.restautantvote.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultVoteInfo {

    private Boolean isSuccess;
    private String message;

}
