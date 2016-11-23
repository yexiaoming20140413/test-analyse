package com.chouti.analyse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebIntegrationTest(value = {"server.port=1099", "spring.profiles.active=dev"})
public class ChoutiAnalyseApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println("test");
	}

}
