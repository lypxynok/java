package org.xynok.lab.dao;


import java.io.Serializable;

import lombok.Data;

@Data
public class Cus implements Serializable{
    private String name;
    private String value;
}