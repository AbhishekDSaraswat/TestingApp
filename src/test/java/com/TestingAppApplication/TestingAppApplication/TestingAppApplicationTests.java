package com.TestingAppApplication.TestingAppApplication;

import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
class TestingAppApplicationTests {

	@BeforeEach
	void setUp(){
		log.info("Starting the method ,settingup config");
	}

	@Test
//	@Disabled
	void testNumberOne() {
		int a = 5;
		int b = 3;

		int result = addTwoNumbers(a,b);
//		Assertions.assertEquals(8,result);

		assertThat(result)
				.isEqualTo(8)
				.isCloseTo(9, Offset.offset(1));



		assertThat("Apple")
				.isEqualTo("Apple")
				.startsWith("App")
				.endsWith("le")
				.hasSize(5);
	}




	@Test
//	@DisplayName("displaynametwo")
	void testNumberTwo(){

	}

	int addTwoNumbers(int a ,int b){
		return a+b;
	}

}
