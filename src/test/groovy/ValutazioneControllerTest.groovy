import com.google.gson.Gson
import org.rconfalonieri.nzuardi.shootingapp.ShootingAppApplication
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ValutazioneDto
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
class ValutazioneControllerTest extends Specification{
    @Autowired
    protected MockMvc mvc

    // Could be autowired using @Value
    private String serverPort = "8080";

    /**
     * Test che controlla se cercando una valutazione tramite id che non esiste viene ritornato un errore 404
     */
    def "se cerco una valutazione che non viene trovata, allora ritorna NOT FOUND"() {
        given:
        StringBuilder url = new StringBuilder("http://localhost:");
        url.append(serverPort);
        url.append("/api/valutazione/-1");

        when:
        def response = mvc.perform(get(url.toString())).andReturn().response

        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    /**
     * Test che crea una valutazione, la salva nel database, la aggiorna e poi la elimina.
     * In questo modo effettuo il testing per quanto riguarda creazione, aggiornamento, cancellazione e ricerca di una valutazione.
     */
    def "se creo una valutazione, la ricerco, la aggiorno e poi la elimino, mi ritorna OK"() {
        given:
        StringBuilder urlSave = new StringBuilder("http://localhost:");
        urlSave.append(serverPort);
        urlSave.append("/api/valutazione/");
        String body = "{\"voto\": \"1\"}";

        StringBuilder urlUpdate = new StringBuilder("http://localhost:");
        urlUpdate.append(serverPort);
        urlUpdate.append("/api/valutazione/");
        String bodyUpdate = "{\"voto\": \"5\"}";

        StringBuilder urlFind = new StringBuilder("http://localhost:");
        urlFind.append(serverPort);
        urlFind.append("/api/valutazione/");

        StringBuilder urlDelete = new StringBuilder("http://localhost:");
        urlDelete.append(serverPort);
        urlDelete.append("/api/valutazione/");

        when:
        // Test save
        def responseSave = mvc.perform(
                post(urlUpdate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().response

        Gson g = new Gson()
        ValutazioneDto valutazione = g.fromJson(responseSave.getContentAsString(), ValutazioneDto.class)

        // Test find
        def responseFind = mvc.perform(get(urlFind.append(valutazione.getId()).toString())).andReturn().response

        // Test update
        def responseUpdate = mvc.perform(
                put(urlUpdate.append(valutazione.getId()).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyUpdate))
                .andReturn().response

        ValutazioneDto valutazioneNew = g.fromJson(responseUpdate.getContentAsString(), ValutazioneDto.class)

        // Test delete
        def responseDelete = mvc.perform(delete(urlDelete.toString() + "/" + valutazione.getId())).andReturn().response

        then:
        responseSave.status == HttpStatus.OK.value() &&
                responseFind.status == HttpStatus.OK.value() &&
                responseDelete.status == HttpStatus.OK.value() &&
                responseUpdate.status == HttpStatus.OK.value() &&
                valutazione.getId() == valutazioneNew.getId() &&
                valutazioneNew.getVoto() == 5
    }
}
