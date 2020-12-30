package com.example.demo.impl;

import com.example.demo.DataService;
import com.example.demo.data.Action;
import com.example.demo.data.ExecutionActionReq;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class DataServiceImpl implements DataService {
    @Autowired
    DataSource dataSource;
    @Autowired
    ResourcePatternResolver resourcePatternResolver;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private Map<String, Action> actionMap = new HashMap<>();

    @PostConstruct
    public void init() throws Throwable {
        Resource[] resources = resourcePatternResolver.getResources(
                "classpath:sqls/*.sql"
        );
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            System.out.println(filename);
            String actionName = filename.substring(0, filename.lastIndexOf("."));
            String sql = IOUtils.toString(resource.getInputStream(), "UTF-8");
            List<String> reqParams = new ArrayList<>();
            Matcher matcher = Pattern.compile(":\\s*(\\w+)").matcher(sql);
            while (matcher.find()) {
                reqParams.add(matcher.group(1));
            }
            Action action = new Action(
                    actionName,
                    sql,
                    reqParams
            );
            actionMap.put(actionName, action);
        }
    }

    @Override
    public void executeAction(Collection<ExecutionActionReq> reqList) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> generatedKeyMap = new HashMap<>();
        for (ExecutionActionReq req : reqList) {
            String actionName = req.getActionName();
            Action action = actionMap.get(actionName);
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            Map<String, Object> reqParamMap = req.getParamMap();
            for (String reqParamName : action.getReqParams()) {
                Object value = Optional.ofNullable(reqParamMap.get(reqParamName)).orElse(generatedKeyMap.get(reqParamName));
                paramSource.addValue(
                        reqParamName,
                        value
                );
            }
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(action.getSql(), paramSource, generatedKeyHolder);
            Optional.ofNullable(generatedKeyHolder.getKeys())
                    .orElse(new HashMap<>())
                    .forEach((k, v) -> generatedKeyMap.put(actionName + "_" + k, v));
        }
    }
}
