package br.cervi.henrique.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.cervi.henrique.rest.core.BaseTest;


public class BarrigaTest extends BaseTest {
	
	private String TOKEN;
	
	@Before
	public void login () {
		
		Map<String, String> login = new HashMap<>();
		login.put("email", "henrique@henrique");
		login.put("senha", "123456");
		
		TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token")
		;
		
	}
	
	
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
		
		given()
			.header("Authorization", "JWT " + TOKEN)
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
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"Conta Rique\" }")
		.when()
			.put("/contas/57966")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("Conta Rique"))
		;
	}
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		//praticamente mesmo processo do acima.		
		
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body("{\"nome\": \"Conta Rique\" }")
		.when()
			.post("/contas/")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
			;

	}

}
