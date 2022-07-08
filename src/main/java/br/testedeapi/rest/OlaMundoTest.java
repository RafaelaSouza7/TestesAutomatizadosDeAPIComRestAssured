package br.testedeapi.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class OlaMundoTest {

    @Test
    public void testOlaMundo() {

        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.statusCode() == 200);
        Assert.assertTrue("O status code deveria ser 200", response.statusCode() == 200);
        Assert.assertEquals(200, response.statusCode());
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {

        //Forma não otimizada de fazer a validação
        Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        //Forma otimizada
        RestAssured.get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        //Utilizando import static do RestAssured
        get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        //Forma otimizada e com legibilidade utilizando Given, When e Then
        given()
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHamcrest() {

        Assert.assertThat("Maria", Matchers.is("Maria"));
        Assert.assertThat(777, Matchers.is(777));
        Assert.assertThat(777, Matchers.isA(Integer.class));
        Assert.assertThat(2207d, Matchers.greaterThanOrEqualTo(2207d));
        Assert.assertThat(-30, Matchers.lessThanOrEqualTo(-20));


        List<Integer> impares = Arrays.asList(1, 3, 5, 7);
        Assert.assertThat(impares, Matchers.hasSize(4));
        Assert.assertThat(impares, Matchers.contains(1,3,5,7));
        Assert.assertThat(impares, Matchers.containsInAnyOrder(7,5,3,1));
        Assert.assertThat(impares, Matchers.hasItems(1,3));

        //Sem utilizar o import static do Matchers e Assert
        Assert.assertThat("Maria", Matchers.is(Matchers.not("Joao")));

        //Utilizando o import static do Matchers e Assert
        assertThat("Maria", is(not("Joao")));
        assertThat("Maria", not("Joao"));
    }
}