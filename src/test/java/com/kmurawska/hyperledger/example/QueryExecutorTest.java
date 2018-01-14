package com.kmurawska.hyperledger.example;

import com.kmurawska.hyperledger.example.entity.User;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static com.kmurawska.hyperledger.example.HyperledgerNetwork.*;
import static org.junit.Assert.assertEquals;

public class QueryExecutorTest {
    private static User user;

    @BeforeClass
    public static void init() throws Exception {
        user = new UserEnroller(CA_SERVICE_LOCATION, USER_STORE_PATH)
                .registerAndEnroll("user1", MSP_ID, AFFILIATION);
    }

    @Test
    @Ignore //where is commit ?
    public void runInitLedger() throws Exception {
        ProposalResponse proposalResponse = new QueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION)
                .execute("colors", "initLedger", "")
                .getResponse();

        System.out.println(new String(proposalResponse.getChaincodeActionResponsePayload()));
    }

    @Test
    public void runAddNewColor() throws Exception {
        ProposalResponse proposalResponse = new QueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION)
                .execute("colors", "addNewColor", "blue", "powderblue", "rgb(176,224,230)", "#B0E0E6", "sky")
                .commit()
                .getResponse();

        System.out.println(new String(proposalResponse.getChaincodeActionResponsePayload()));
    }

    @Test
    public void runShowColorWithName() throws Exception {
        ProposalResponse proposalResponse = new QueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION)
                .execute("colors", "showColorWithName", "blue")
                .getResponse();

        System.out.println(new String(proposalResponse.getChaincodeActionResponsePayload()));
    }
}