package com.chouti.analyse;

import com.chouti.analyse.service.ChoutiTrainService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringApplicationConfiguration(classes = ChoutiAnalyseApplication.class)
@WebIntegrationTest(value = {"server.port=1099", "spring.profiles.active=dev"})
public class ChoutiAnalyseApplicationTests {

	@Test
	public void contextLoads() throws Exception{

		System.out.println("test");
	}

}
