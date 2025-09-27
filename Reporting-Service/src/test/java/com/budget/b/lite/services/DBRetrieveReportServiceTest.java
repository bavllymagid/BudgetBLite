package com.budget.b.lite.services;

import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DBRetrieveReportServiceTest {

    @Autowired
    DBRetrieveReportService service;
    @Autowired
    ExpensesRepository expensesRepository;
    @Autowired
    IncomeRepository incomeRepository;

    @Test
    void generateReport() {
        System.out.println(service.generateReport("b1@gmail.com").toString());
    }
}