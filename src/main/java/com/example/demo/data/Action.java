package com.example.demo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class Action {
    private String name;
    private String sql;
    private List<String> reqParams;
}
