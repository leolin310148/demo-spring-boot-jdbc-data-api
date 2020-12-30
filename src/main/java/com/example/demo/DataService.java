package com.example.demo;

import com.example.demo.data.ExecutionActionReq;

import java.util.Collection;

public interface DataService {
    void executeAction(Collection<ExecutionActionReq> reqList);
}
