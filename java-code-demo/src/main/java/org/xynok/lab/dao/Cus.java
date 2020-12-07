package org.xynok.lab.dao;


import java.io.Serializable;

import lombok.Data;

@Data
public class Cus implements Serializable{
    private static final long serialVersionUID = -1895478505790318262L;
    private String name;
    private String value;
}