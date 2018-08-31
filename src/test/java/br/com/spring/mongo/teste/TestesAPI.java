/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.spring.mongo.teste;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

//SEGUINDO A ORDEM ALFABETICA DOS METODOS
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestesAPI {

    public TestesAPI() {

    }

    @Test
    public void test1CadastroUsuario() {
        baseURI = "http://localhost:8080/SpringRestMongo/usuarios";
        String myJson = "{ 	\n"
                + " 	\"usuario\" :\"admin\",\n"
                + " 	\"senha\" : \"admin\",\n"
                + "     \"tipo\" :  \"USUARIO_ADMINISTRADOR\"\n"
                + "}";

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1Yjg5NGRmMTgzMDI1YTE4NDAwYWY2NDgiLCJpc3MiOiJHZWRPbmxpbmUiLCJpYXQiOjE1MzU3NDU5NTYsImV4cCI6MTUzODMzNzk1Niwicm9sZXMiOlsiVVNVQVJJT19BRE1JTklTVFJBRE9SIl19.b-pFRC78fU1TtPrOv_KKVMLKbrZNyEJz_AVE3O-WTBk")
                .body(myJson)
                .when()
                .post("/cadastrar")
                .then()
                .statusCode(200)
                .body("returnCode", is(0)); // 0 cadsatro ok
    }

    @Test
    public void test2LoginUsuario() {
        baseURI = "http://localhost:8080/SpringRestMongo/usuarios";
        String myJson = "{ 	\n"
                + " 	\"usuario\" :\"admin\",\n"
                + " 	\"senha\" : \"admin\"\n"
                + "}";

        given()
                .contentType("application/json")
                .body(myJson)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("returnCode", is(0)); // 0 login ok
    }

    @Test
    public void test3ConsultaUsuarios() {
        baseURI = "http://localhost:8080/SpringRestMongo/usuarios";
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1Yjg5NGRmMTgzMDI1YTE4NDAwYWY2NDgiLCJpc3MiOiJHZWRPbmxpbmUiLCJpYXQiOjE1MzU3NDU5NTYsImV4cCI6MTUzODMzNzk1Niwicm9sZXMiOlsiVVNVQVJJT19BRE1JTklTVFJBRE9SIl19.b-pFRC78fU1TtPrOv_KKVMLKbrZNyEJz_AVE3O-WTBk")
                .when()
                .get("/listar")
                .then()
                .statusCode(200)
                .assertThat()
                .body("returnCode", is(0)); // consulta ok
    }

}
