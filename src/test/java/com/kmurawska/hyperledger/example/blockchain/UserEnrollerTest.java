package com.kmurawska.hyperledger.example.blockchain;

import com.kmurawska.hyperledger.example.blockchain.entity.User;
import org.junit.Test;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

public class UserEnrollerTest {

    @Test
    public void run() throws Exception {
        User user = new UserEnroller(HyperledgerNetwork.CA_SERVICE_LOCATION, HyperledgerNetwork.USER_STORE_PATH)
                .registerAndEnroll("user" + randomUUID().toString(), HyperledgerNetwork.MSP_ID, HyperledgerNetwork.AFFILIATION);

        assertNotNull(user);
        assertNotNull(user.getEnrollmentSecret());
        assertNotNull(user.getEnrollment());
    }
}