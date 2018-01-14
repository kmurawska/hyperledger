package com.kmurawska.hyperledger.example.blockchain;

import com.kmurawska.hyperledger.example.blockchain.entity.User;
import com.kmurawska.hyperledger.example.colors.entity.colors.Color;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Optional;

import static com.kmurawska.hyperledger.example.blockchain.HyperledgerNetwork.*;
import static javax.json.Json.createObjectBuilder;
import static org.junit.Assert.assertEquals;

public class QueryExecutorTest {


    private static User user;

    @BeforeClass
    public static void init() throws Exception {
        user = new UserEnroller(CA_SERVICE_LOCATION, USER_STORE_PATH)
                .registerAndEnroll("user1", MSP_ID, AFFILIATION);
    }

    @Test
    public void runAddNewColor() throws Exception {
        Optional<ProposalResponse> proposalResponse = new WriteQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "addNewColor", Colors.POWDERBLUE.getIdentifier(), Colors.POWDERBLUE.toJson())
                .commit()
                .getProposalResponse();

        assertEquals(200, readResponseStatus(proposalResponse));
    }

    @Test
    public void runShowColorWithName() throws Exception {
        Optional<ProposalResponse> proposalResponse = new ReadQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "showColorWithName", Colors.DEEPPINK.getHex())
                .getProposalResponse();

        Color color = new Color(Json.createReader(new StringReader(readResponsePayload(proposalResponse))).readObject());

        assertEquals(Colors.POWDERBLUE.getFamily(), color.getFamily());
        assertEquals(Colors.POWDERBLUE.getName(), color.getName());
        assertEquals(Colors.POWDERBLUE.getRgb(), color.getRgb());
        assertEquals(Colors.POWDERBLUE.getHex(), color.getHex());
        assertEquals(Colors.POWDERBLUE.getExample(), color.getExample());
    }

    @Test
    public void showAllColors() throws Exception {
        String query = createObjectBuilder().build().toString();

        Optional<ProposalResponse> proposalResponse = new ReadQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "executeQuery", query)
                .getProposalResponse();


        Json.createReader(new StringReader(readResponsePayload(proposalResponse))).readArray()
                .stream()
                .map(o -> new Color((JsonObject) o))
                .forEach(c -> System.out.println(c.toJson()));
    }

    @Test
    public void showAllColorsFromBlueFamily() throws Exception {
        String query = createObjectBuilder()
                .add("selector", createObjectBuilder()
                        .add("family", "blue")
                        .build()
                )
                .build().toString();

        Optional<ProposalResponse> proposalResponse = new ReadQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "executeQuery", query)
                .getProposalResponse();

        Json.createReader(new StringReader(readResponsePayload(proposalResponse))).readArray()
                .stream()
                .map(o -> new Color((JsonObject) o))
                .forEach(c -> System.out.println(c.toJson()));
    }

    private int readResponseStatus(Optional<ProposalResponse> proposalResponse) {
        return proposalResponse.map(r -> {
            try {
                return r.getChaincodeActionResponseStatus();
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }).orElse(-1);
    }

    private String readResponsePayload(Optional<ProposalResponse> proposalResponse) {
        return proposalResponse.map(r -> {
            try {
                return new String(r.getChaincodeActionResponsePayload());
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }).orElse("");
    }
}