import com.google.gson.Gson
import org.rconfalonieri.nzuardi.shootingapp.ShootingAppApplication
import org.rconfalonieri.nzuardi.shootingapp.model.dto.ServizioDto
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
class ServizioControllerTest extends Specification{
    @Autowired
    protected MockMvc mvc

    // Could be autowired using @Value
    private String serverPort = "8080";

    /**
     * Test che controlla se cercando un servizio tramite id che non esiste viene ritornato un errore 404
     */
    def "se cerco un servizio che non viene trovata, allora ritorna NOT FOUND"() {
        given:
        StringBuilder url = new StringBuilder("http://localhost:");
        url.append(serverPort);
        url.append("/api/servizio/-1");

        when:
        def response = mvc.perform(get(url.toString())).andReturn().response

        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    /**
     * Test che crea un servizio, la salva nel database, la aggiorna e poi la elimina.
     * In questo modo effettuo il testing per quanto riguarda creazione, aggiornamento, cancellazione e ricerca di un servizio.
     */
    def "se creo un servizio, la ricerco, la aggiorno e poi la elimino, mi ritorna OK"() {
        given:
        StringBuilder urlSave = new StringBuilder("http://localhost:");
        urlSave.append(serverPort);
        urlSave.append("/api/servizio/");
        String body = "{\"nome\": \"tappi\", \"foto\": \"esempio_foto\", \"prezzo\": \"10.1\", \"quantita\" : \"2\"}";

        StringBuilder urlUpdate = new StringBuilder("http://localhost:");
        urlUpdate.append(serverPort);
        urlUpdate.append("/api/servizio/");
        String bodyUpdate = "{\"nome\": \"caricatore extra\", \"foto\": \"esempio_foto2\", \"prezzo\": \"11.1\", \"quantita\" : \"5\"}";

        StringBuilder urlFind = new StringBuilder("http://localhost:");
        urlFind.append(serverPort);
        urlFind.append("/api/servizio/");

        StringBuilder urlDelete = new StringBuilder("http://localhost:");
        urlDelete.append(serverPort);
        urlDelete.append("/api/servizio/");

        when:
        // Test save
        def responseSave = mvc.perform(
                post(urlUpdate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().response

        Gson g = new Gson()
        ServizioDto servizio = g.fromJson(responseSave.getContentAsString(), ServizioDto.class)

        // Test find
        def responseFind = mvc.perform(get(urlFind.append(servizio.getId()).toString())).andReturn().response

        // Test update
        def responseUpdate = mvc.perform(
                put(urlUpdate.append(servizio.getId()).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyUpdate))
                .andReturn().response

        ServizioDto servizioNew = g.fromJson(responseUpdate.getContentAsString(), ServizioDto.class)

        // Test delete
        def responseDelete = mvc.perform(delete(urlDelete.toString() + "/" + servizio.getId())).andReturn().response

        then:
        responseSave.status == HttpStatus.OK.value() &&
                responseFind.status == HttpStatus.OK.value() &&
                responseDelete.status == HttpStatus.OK.value() &&
                responseUpdate.status == HttpStatus.OK.value() &&
                servizio.getId() == servizioNew.getId() &&
                servizioNew.getNome() == "caricatore extra" &&
                servizioNew.getFoto() == "esempio_foto2" &&
                servizioNew.getPrezzo() == 11.1F &&
                servizioNew.getQuantita() == 5
    }
}
