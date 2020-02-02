package br.cervi.henrique.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;

import br.cervi.henrique.rest.core.BaseTest;


public class BarrigaTest extends BaseTest {
	
	private String TOKEN;
	
	private Movimentacoes getMovimentacoesValida() {
		Movimentacoes mov = new Movimentacoes();
		
		mov.setConta_id(57966);
		//mov.setUsuario_id(usuario_id);
		mov.setDescricao("Pagamento Facul Bruna");
		mov.setEnvolvido("Bruna");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2020");
		mov.setData_pagamento("30/01/2020");
		mov.setValor(176.4f);
		mov.setStatus(true);
		return mov;
		
	}
	
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
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		Movimentacoes mov = getMovimentacoesValida();
		
//			Movimentacoes mov = new Movimentacoes();
//			
//			mov.setConta_id(57966);
//			//mov.setUsuario_id(usuario_id);
//			mov.setDescricao("Pagamento Facul Bruna");
//			mov.setEnvolvido("Bruna");
//			mov.setTipo("REC");
//			mov.setData_transacao("01/01/2020");
//			mov.setData_pagamento("30/01/2020");
//			mov.setValor(176.4f);
//			mov.setStatus(true);
			
		given()
			.header("Authorization", "JWT " + TOKEN)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			;

	}
	
	@Test
	public void deveValidarCamposObrigatorios() {
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		.body("{}")
	.when()
		.post("/transacoes")
	.then()
		.statusCode(400)
		.body("$", hasSize(8))
		.body("msg", hasItems(
				"Data da Movimentação é obrigatório",
				"Data do pagamento é obrigatório",
				"Descrição é obrigatório",
				"Interessado é obrigatório",
				"Valor é obrigatório",
				"Valor deve ser um número",
				"Conta é obrigatório",
				"Situação é obrigatório"
				))
		;

	}
	
	@Test
	public void naoDeveCadastrarMovimentacaoFutura () {
			Movimentacoes mov = getMovimentacoesValida();
			
			mov.setDescricao("Pagamento Cartão Nubank");
			mov.setEnvolvido("Henrique");
			mov.setData_transacao("03/02/2030");
			mov.setData_pagamento("05/02/2030");
			mov.setValor(1000f);
			
			
			given()
				.header("Authorization", "JWT " + TOKEN)
				.body(mov)
			.when()
				.post("/transacoes")
			.then()
				.statusCode(400)
				.body("$", hasSize(1))
				.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
			;
			
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao () {
				
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/contas/57966")
		.then()
			.log().all()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void deveCalcularSaldoDasContas () {
		
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 57966}.saldo", is("352.80")) // ele usa o find para achar apenas um elemento, (it, iterador).
			
		;
	}
	
	@Test
	public void deveRemoverMovimentacao() {
		given()
			.header("Authorization", "JWT " + TOKEN)
		.when()
			.delete("/transacoes/46536")
		.then()
			.statusCode(204)
			.log().all()
	;
		
	}
	

}
