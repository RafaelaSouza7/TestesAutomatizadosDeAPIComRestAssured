package br.testedeapi.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserJsonTest {

    @Test
    public void verificarPrimeiroNivel() {
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/1")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", containsString("Silva"))
            .body("age", greaterThanOrEqualTo(18))
        ;
    }

    @Test
    public void verificarPrimeiroNivelComOutrasFormas() {
        Response response = request(Method.GET, "https://restapi.wcaquino.me/users/1");

        //Path
        response.path("id").equals(1);
        assertEquals(new Integer(1), response.path("id"));
        assertEquals(new Integer(1), response.path("%s", "id"));

        //JsonPath
        JsonPath jpath = new JsonPath(response.asString());
        assertEquals(1, jpath.getInt("id"));

        //From
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);
    }

    @Test
    public void verificarSegundoNivel(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/2")
        .then()
            .statusCode(200)
            .body("id", is(2))
            .body("name", containsString("Joaquina"))
            .body("endereco.rua", is("Rua dos bobos"))
            //Se houvesse mais níveis: endereco.rua.numero.complemento
            .body("endereco.numero", is(0))
        ;
    }

    @Test
    public void verificarLista(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/3")
        .then()
            .statusCode(200)
            .body("id", is(3))
            .body("name", containsString("Ana"))
            .body("filhos", hasSize(2))
            .body("filhos[0].name", is("Zezinho"))
            .body("filhos[1].name", is("Luizinho"))
            .body("filhos.name", hasItem("Zezinho"))
            .body("filhos.name", hasItems("Zezinho","Luizinho"))
        ;
    }

    @Test
    public void verificarErroUsuarioInexistente(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/4")
        .then()
            .statusCode(404)
            .body("error", is("Usuário inexistente"))
        ;
    }

    @Test
    public void verificarListaNaRaiz(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/")
        .then()
            .statusCode(200)
            .body("$", hasSize(3)) //Utilizar o $ é uma convenção, mas funciona se deixar vazio
            .body("", hasSize(3))
            .body("name", hasSize(3))
            .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
            .body("age[1]", is(25))
            .body("endereco[1].rua", is("Rua dos bobos"))
            .body("endereco[1].numero", is(0))
            .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))
            .body("filhos[2].name[1]", is("Luizinho"))
            .body("salary", contains(1234.5678f, 2500, null))
        ;
    }

    @Test
    public void verificarDeFormaAvancada(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/")
        .then()
                .statusCode(200)
                .body("age.findAll{it <= 25}.size()", is(2))
                .body("age.findAll{it > 20 && it <= 25}.size()", is(1))
                .body("findAll{it.age > 20 && it.age <= 25}.name", hasItem("Maria Joaquina")) //Retorna uma lista com apenas 1 objeto, por isso temos que usar o hasItem
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina")) //Forma de pegar apenas o 1º objeto da lista para conseguir usar o is
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia")) //Último registro
                .body("find{it.age <= 25}.name", is("Maria Joaquina")) //Forma de pegar apenas o 1º objeto da lista para conseguir usar o is sem especificar a posição na lista
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina","Ana Júlia"))
                .body("findAll{it.name.length() >= 9}.name", hasItems("Maria Joaquina","Ana Júlia", "João da Silva"))
                .body("name.collect{it.toUpperCase()}", hasItems("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItems("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
                .body("age.collect{it * 2}", hasItems(60, 50, 40))
                .body("id.max()", is(3))
                .body("salary.min()", is(1234.5678f))
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
                .body("salary.findAll{it != null}.sum()", allOf(greaterThan(2000d), lessThan(5000d)))
        ;
    }

    @Test
    public void unirJsonPathComJava(){
        ArrayList<String> nomes =
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/")
                .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}");

        assertEquals(1, nomes.size());
        assertTrue(nomes.get(0).equalsIgnoreCase("mAria jOaquinA"));
        assertEquals("maria joaquina".toUpperCase(Locale.ROOT), nomes.get(0).toUpperCase(Locale.ROOT));
    }
}
