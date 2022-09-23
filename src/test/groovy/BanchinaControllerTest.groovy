import com.google.gson.Gson
import org.rconfalonieri.nzuardi.shootingapp.ShootingAppApplication
import org.rconfalonieri.nzuardi.shootingapp.model.dto.BanchinaDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest(classes = ShootingAppApplication.class)
@AutoConfigureMockMvc
class BanchinaControllerTest extends Specification{
    @Autowired
    protected MockMvc mvc

    // Could be autowired using @Value
    private String serverPort = "8080";

    /**
     * Test che controlla se cercando una banchina tramite id che non esiste viene ritornato un errore 404
     */
    def "se cerco una banchina che non viene trovata, allora ritorna NOT FOUND"() {
        given:
        StringBuilder url = new StringBuilder("http://localhost:");
        url.append(serverPort);
        url.append("/api/banchina/-1");

        when:
        def response = mvc.perform(get(url.toString())).andReturn().response

        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    /**
     * Test che crea una banchina, la salva nel database, la aggiorna e poi la elimina.
     * In questo modo effettuo il testing per quanto riguarda creazione, aggiornamento, cancellazione e ricerca di una banchina.
     */
    def "se creo una banchina, la ricerco, la aggiorno e poi la elimino, mi ritorna OK"() {
        given:
        StringBuilder urlSave = new StringBuilder("http://localhost:");
        urlSave.append(serverPort);
        urlSave.append("/api/banchina/");
        String body = "{\"nome\": \"NomeBanchina\"}";

        StringBuilder urlUpdate = new StringBuilder("http://localhost:");
        urlUpdate.append(serverPort);
        urlUpdate.append("/api/banchina/");
        String bodyUpdate = "{\"nome\": \"NomeBanchinaNuovo\"}";

        StringBuilder urlFind = new StringBuilder("http://localhost:");
        urlFind.append(serverPort);
        urlFind.append("/api/banchina/");

        StringBuilder urlDelete = new StringBuilder("http://localhost:");
        urlDelete.append(serverPort);
        urlDelete.append("/api/banchina/");

        when:
        // Test save
        def responseSave = mvc.perform(
                post(urlUpdate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().response

        Gson g = new Gson()
        BanchinaDto banchina = g.fromJson(responseSave.getContentAsString(), BanchinaDto.class)

        // Test find
        def responseFind = mvc.perform(get(urlFind.append(banchina.getId()).toString())).andReturn().response

        // Test update
        def responseUpdate = mvc.perform(
                put(urlUpdate.append(banchina.getId()).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyUpdate))
                .andReturn().response

        BanchinaDto banchinaNew = g.fromJson(responseUpdate.getContentAsString(), BanchinaDto.class)

        // Test delete
        def responseDelete = mvc.perform(delete(urlDelete.toString() + "/" + banchina.getId())).andReturn().response

        then:
        responseSave.status == HttpStatus.OK.value() &&
                responseFind.status == HttpStatus.OK.value() &&
                responseDelete.status == HttpStatus.OK.value() &&
                responseUpdate.status == HttpStatus.OK.value() &&
                banchina.getId() == banchinaNew.getId() &&
                banchinaNew.getNome() == "NomeBanchinaNuovo"
    }
}
