package io.polyrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
public class PolyRepoAnalyserApplication {


	public static void main(String[] args) {
		SpringApplication.run(PolyRepoAnalyserApplication.class, args);
	}

}
