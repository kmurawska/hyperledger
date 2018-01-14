package com.kmurawska.hyperledger.example.blockchain;

import com.kmurawska.hyperledger.example.blockchain.entity.User;
import com.kmurawska.hyperledger.example.colors.entity.colors.Color;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.Json;
import java.io.StringReader;
import java.util.Optional;

import static com.kmurawska.hyperledger.example.blockchain.HyperledgerNetwork.*;
import static org.junit.Assert.assertEquals;

public class QueryExecutorTest {
    private static final String FAMILY = "blue";
    private static final String NAME = "powderblue";
    private static final String RGB = "rgb(230,230,250)";
    private static final String HEX = "#B0E0E6";
    private static final String EXAMPLE = "sky";

    private static User user;

    @BeforeClass
    public static void init() throws Exception {
        user = new UserEnroller(CA_SERVICE_LOCATION, USER_STORE_PATH)
                .registerAndEnroll("user1", MSP_ID, AFFILIATION);
    }

    @Test
    @Ignore //where is commit ?
    public void runInitLedger() throws Exception {
        Optional<ProposalResponse> proposalResponse = new ReadQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "initLedger", "")
                .getProposalResponse();

        System.out.println(readResponsePayload(proposalResponse));
    }

    @Test
    public void runAddNewColor() throws Exception {
        Color color = new Color(FAMILY, NAME, RGB, HEX, EXAMPLE);

        Optional<ProposalResponse> proposalResponse = new WriteQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "addNewColor", color.getIdentifier(), color.toJson())
                .commit()
                .getProposalResponse();

        System.out.println(readResponsePayload(proposalResponse));
    }

    @Test
    public void runShowColorWithName() throws Exception {
        Optional<ProposalResponse> proposalResponse = new ReadQueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION, EVENT_HUB_LOCATION)
                .execute("colors", "showColorWithName", HEX)
                .getProposalResponse();

        String result = readResponsePayload(proposalResponse);
        System.out.println(result);

        Color color = new Color(Json.createReader(new StringReader(result)).readObject());
        assertEquals(FAMILY, color.getFamily());
        assertEquals(NAME, color.getName());
        assertEquals(RGB, color.getRgb());
        assertEquals(HEX, color.getHex());
        assertEquals(EXAMPLE, color.getExample());
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