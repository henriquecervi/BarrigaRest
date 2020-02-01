package br.cervi.henrique.rest.tests;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

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
	
	@Test
	public void deveIncluirContaComSucesso() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "henrique@henrique");
		login.put("senha", "123456");
		
		
		String token = given()
			.body(login)
//			.formParam("email", "henrique@henrique")
//			.formParam("senha", "123456")
		.when()
			.post("/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token");
		
		
//		System.out.println(token);
//		System.out.println("--------------------------------");
		
		given()
			.header("Authorization", "JWT " + token)
			// criando json para o eclipse incluir as barras.
			// { "nome": "Conta Henrique" }
			.body("{\"nome\": \"Conta Bruna\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(201)
		;
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		//praticamente mesmo processo do acima.
		Map<String, String> login = new HashMap<>();
		login.put("email", "henrique@henrique");
		login.put("senha", "123456");
		
		String token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token")
		;
		
		given()
			.header("Authorization", "JWT " + token)
			.body("{\"nome\": \"Conta Rique\" }")
		.when()
			.put("/contas/57966")
		.then()
			.log().all()
			.statusCode(200)
		;
	}
	

}


