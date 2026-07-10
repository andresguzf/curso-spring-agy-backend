package com.andres.course.agy.springboot.springapi.app;

import com.andres.course.agy.springboot.springapi.app.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	void contextLoads() {
		assertEquals(10, customerRepository.count(), "The database should be pre-loaded with exactly 10 customers");
	}

}
