import com.google.gson.Gson
import org.rconfalonieri.nzuardi.shootingapp.ShootingAppApplication
import org.rconfalonieri.nzuardi.shootingapp.model.dto.PrenotazioneDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType

import java.text.SimpleDateFormat

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@SpringBootTest(classes = ShootingAppApplication.class)
@AutoConfigureMockMvc
class PrenotazioneControllerTest extends Specification{
    @Autowired
    protected MockMvc mvc

    // Could be autowired using @Value
    private String serverPort = "8080";

    /**
     * Test che controlla se cercando una prenotazione tramite id che non esiste viene ritornato un errore 404
     */
    def "se cerco una prenotazione che non viene trovata, allora ritorna NOT FOUND"() {
        given:
        StringBuilder url = new StringBuilder("http://localhost:");
        url.append(serverPort);
        url.append("/api/prenotazione/-1");

        when:
        def response = mvc.perform(get(url.toString())).andReturn().response

        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    /**
     * Test che crea una prenotazione, la salva nel database, la aggiorna e poi la elimina.
     * In questo modo effettuo il testing per quanto riguarda creazione, aggiornamento, cancellazione e ricerca di una prenotazione.
     */
    // TODO -> se non aggiungo un utente alla prenotazione, non posso salvarlo, di conseguenza il test fallisce
    def "se creo una prenotazione, la ricerco, la aggiorno e poi la elimino, mi ritorna OK"() {
        given:
        StringBuilder urlSave = new StringBuilder("http://localhost:");
        urlSave.append(serverPort);
        urlSave.append("/api/prenotazione/");
        String body = "{\"dataInizio\": \"2020-01-01\", \"dataFine\": \"2021-01-01\"}";

        StringBuilder urlUpdate = new StringBuilder("http://localhost:");
        urlUpdate.append(serverPort);
        urlUpdate.append("/api/prenotazione/");
        String bodyUpdate = "{\"dataInizio\": \"2021-01-01\", \"dataFine\": \"2022-01-01\"}";

        StringBuilder urlFind = new StringBuilder("http://localhost:");
        urlFind.append(serverPort);
        urlFind.append("/api/prenotazione/");

        StringBuilder urlDelete = new StringBuilder("http://localhost:");
        urlDelete.append(serverPort);
        urlDelete.append("/api/prenotazione/");

        when:
        // Test save
        def responseSave = mvc.perform(
                post(urlUpdate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().response

        Gson g = new Gson()
        PrenotazioneDto prenotazione = g.fromJson(responseSave.getContentAsString(), PrenotazioneDto.class)

        // Test find
        def responseFind = mvc.perform(get(urlFind.append(prenotazione.getId()).toString())).andReturn().response

        // Test update
        def responseUpdate = mvc.perform(
                put(urlUpdate.append(prenotazione.getId()).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyUpdate))
                .andReturn().response

        PrenotazioneDto prenotazioneNew = g.fromJson(responseUpdate.getContentAsString(), PrenotazioneDto.class)

        // Test delete
        def responseDelete = mvc.perform(delete(urlDelete.toString() + "/" + prenotazione.getId())).andReturn().response

        then:
        responseSave.status == HttpStatus.OK.value() &&
                responseFind.status == HttpStatus.OK.value() &&
                responseDelete.status == HttpStatus.OK.value() &&
                responseUpdate.status == HttpStatus.OK.value() &&
                prenotazione.getId() == prenotazioneNew.getId() &&
                new SimpleDateFormat("yyyy-MM-dd").format(prenotazioneNew.getDataInizio()) == "2021-01-01" &&
                new SimpleDateFormat("yyyy-MM-dd").format(prenotazioneNew.getDataFine()) == "2022-01-01"
    }
}
