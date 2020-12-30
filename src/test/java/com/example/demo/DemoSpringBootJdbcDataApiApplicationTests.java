package com.example.demo;

import com.example.demo.data.ExecutionActionReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DemoSpringBootJdbcDataApiApplicationTests {

    @Autowired
    DataService dataService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @Order(100)
    void shouldAbleToInsertAuthor() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "Leo");
        dataService.executeAction(Collections.singletonList(
                new ExecutionActionReq(
                        "insert_author",
                        paramMap
                )
        ));
        Assertions.assertEquals(
                1,
                jdbcTemplate.queryForList("select * from Author where name = 'Leo'").size()
        );
    }

    @Test
    @Order(200)
    void shouldAbleToInsertAuthorAndBook() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "John");
        List<ExecutionActionReq> reqList = Arrays.asList(
                new ExecutionActionReq(
                        "insert_author",
                        paramMap
                ),
                new ExecutionActionReq(
                        "insert_book",
                        createBookParam("Java", 200)
                ),
                new ExecutionActionReq(
                        "insert_book",
                        createBookParam("javascript", 300)
                )
        );
        try {
            System.out.println(new ObjectMapper().writeValueAsString(reqList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        dataService.executeAction(reqList);

        Assertions.assertEquals(
                1,
                jdbcTemplate.queryForList("select * from Author where name = 'John'").size()
        );
        Assertions.assertEquals(
                2,
                jdbcTemplate.queryForList("select * from Book where author_id = (select id from Author where name = 'John')").size()
        );
    }

    @Test
    @Order(300)
    void shouldNotInsertAuthorWhenInsertBookFailed() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "Lilly");
        try {
            dataService.executeAction(Arrays.asList(
                    new ExecutionActionReq(
                            "insert_author",
                            paramMap
                    ),
                    new ExecutionActionReq(
                            "insert_book",
                            createBookParam("LongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLongLong", 200)
                    )
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(
                0,
                jdbcTemplate.queryForList("select * from Author where name = 'Lilly'").size()
        );
    }


    private Map<String, Object> createBookParam(String name, Integer price) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        paramMap.put("price", price);
        return paramMap;
    }

}
