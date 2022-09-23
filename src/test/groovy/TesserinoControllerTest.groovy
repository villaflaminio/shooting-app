import com.google.gson.Gson
import org.rconfalonieri.nzuardi.shootingapp.ShootingAppApplication
import org.rconfalonieri.nzuardi.shootingapp.model.dto.TesserinoDto
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
class TesserinoControllerTest extends Specification{
    @Autowired
    protected MockMvc mvc

    // Could be autowired using @Value
    private String serverPort = "8080";

    /**
     * Test che controlla se cercando un tesserino tramite id che non esiste viene ritornato un errore 404
     */
    def "se cerco un tesserino che non viene trovata, allora ritorna NOT FOUND"() {
        given:
        StringBuilder url = new StringBuilder("http://localhost:");
        url.append(serverPort);
        url.append("/api/tesserino/-1");

        when:
        def response = mvc.perform(get(url.toString())).andReturn().response

        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    /**
     * Test che crea un tesserino, la salva nel database, la aggiorna e poi la elimina.
     * In questo modo effettuo il testing per quanto riguarda creazione, aggiornamento, cancellazione e ricerca di un tesserino.
     */
    def "se creo un tesserino, la ricerco, la aggiorno e poi la elimino, mi ritorna OK"() {
        given:
        StringBuilder urlSave = new StringBuilder("http://localhost:");
        urlSave.append(serverPort);
        urlSave.append("/api/tesserino/");
        String body = "{\"dataScadenza\": \"2020-01-01\", \"dataRilascio\": \"2021-01-01\", \"qrCode\": \"stringQR\"}";

        StringBuilder urlUpdate = new StringBuilder("http://localhost:");
        urlUpdate.append(serverPort);
        urlUpdate.append("/api/tesserino/");
        String bodyUpdate = "{\"dataScadenza\": \"2021-01-01\", \"dataRilascio\": \"2022-01-01\", \"qrCode\": \"stringQR2\"}";

        StringBuilder urlFind = new StringBuilder("http://localhost:");
        urlFind.append(serverPort);
        urlFind.append("/api/tesserino/");

        StringBuilder urlDelete = new StringBuilder("http://localhost:");
        urlDelete.append(serverPort);
        urlDelete.append("/api/tesserino/");

        when:
        // Test save
        def responseSave = mvc.perform(
                post(urlUpdate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().response

        Gson g = new Gson()
        TesserinoDto tesserino = g.fromJson(responseSave.getContentAsString(), TesserinoDto.class)

        // Test find
        def responseFind = mvc.perform(get(urlFind.append(tesserino.getId()).toString())).andReturn().response

        // Test update
        def responseUpdate = mvc.perform(
                put(urlUpdate.append(tesserino.getId()).toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyUpdate))
                .andReturn().response

        TesserinoDto tesserinoNew = g.fromJson(responseUpdate.getContentAsString(), TesserinoDto.class)

        // Test delete
        def responseDelete = mvc.perform(delete(urlDelete.toString() + "/" + tesserino.getId())).andReturn().response

        then:
        responseSave.status == HttpStatus.OK.value() &&
                responseFind.status == HttpStatus.OK.value() &&
                responseDelete.status == HttpStatus.OK.value() &&
                responseUpdate.status == HttpStatus.OK.value() &&
                tesserino.getId() == tesserinoNew.getId() &&
                new SimpleDateFormat("yyyy-MM-dd").format(tesserinoNew.getDataScadenza()) == "2021-01-01" &&
                new SimpleDateFormat("yyyy-MM-dd").format(tesserinoNew.getDataRilascio()) == "2022-01-01" &&
                tesserinoNew.getQrCode() == "stringQR2"
    }
}
