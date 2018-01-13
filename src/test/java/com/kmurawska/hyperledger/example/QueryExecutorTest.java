package com.kmurawska.hyperledger.example;

import com.kmurawska.hyperledger.example.entity.User;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.junit.Test;

import static com.kmurawska.hyperledger.example.HyperledgerNetwork.*;
import static org.junit.Assert.assertEquals;

public class QueryExecutorTest {

    @Test
    public void run() throws Exception {
        User user = new UserEnroller(CA_SERVICE_LOCATION, USER_STORE_PATH)
                .registerAndEnroll("user01", MSP_ID, AFFILIATION);

        ProposalResponse proposalResponse = new QueryExecutor(user)
                .onChannel(CHANNEL_NAME, PEER_LOCATION, ORDERER_LOCATION)
                .execute("colors", "get", "green");

        System.out.println(proposalResponse.toString());
        assertEquals("apple", new String(proposalResponse.getChaincodeActionResponsePayload()));
    }
}