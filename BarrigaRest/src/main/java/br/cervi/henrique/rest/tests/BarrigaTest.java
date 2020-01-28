package br.cervi.henrique.rest.tests;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.cervi.henrique.rest.core.BaseTest;


public class BarrigaTest extends BaseTest {
	
	@Test
	public void naoDeveAcessarSemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401) // 401 é status não autorizado.
		;
	}
	

}
