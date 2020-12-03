package org.xynok.lab.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xynok.lab.dao.Cus;
import org.xynok.lab.mapper.CusMapper;
import org.xynok.tools.datasource.CustomDataSource;

@Service
public class DbService {
    @Autowired
    private CusMapper cusMapper;

    public List<Cus> allCus(){
        return cusMapper.allCus();
    }

    @CustomDataSource(name="cus1")
    public List<Cus> allCus1(){
        return cusMapper.allCus();
    }

    @CustomDataSource(name="cus2")
    public List<Cus> allCus2(){
        return cusMapper.allCus();
    }
}