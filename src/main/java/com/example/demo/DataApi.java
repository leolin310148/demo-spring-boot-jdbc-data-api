package com.example.demo;

import com.example.demo.data.ExecutionActionReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class DataApi {

    @Autowired
    DataService dataService;

    @PostMapping("batchAction")
    public void doBatchAction(
            @RequestBody Collection<ExecutionActionReq> reqList
    ) {
        dataService.executeAction(reqList);
    }
}
