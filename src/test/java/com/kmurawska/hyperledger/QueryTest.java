package com.kmurawska.hyperledger;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

public class QueryTest {
    private static final String CA_SERVICE_LOCATION = "http://localhost:7054";
    private static final String PEER_LOCATION = "grpc://localhost:7051";
    private static final String ORDERER_LOCATION = "grpc://localhost:7050";
    private static final String USER_STORE = "D:\\IT\\workspace\\hyperledger-example\\hyperledger\\users-store";
    private static final String MSP_ID = "Org1MSP";

    private final UsersStore usersStore = new UsersStore(USER_STORE);
    private HFCAClient caClient;
    private HFClient client;

    @Test
    public void query() throws Exception {
        caClient = createHFCAClient();
        User admin = enrollAdmin("admin", "adminpw");
        User user = registerAndEnrollUser(admin, "user1", MSP_ID, "org1.department1");

        client = createHFClient(user);

        Channel channel = createAndInitializeChannel("rainbow", PEER_LOCATION, ORDERER_LOCATION);
        Query query = new Query("colors", "get", new String[]{"green"});
        ProposalResponse proposalResponse = executeQueryOn(channel, query);

        assertEquals("apple", new String(proposalResponse.getChaincodeActionResponsePayload()));
    }

    private HFCAClient createHFCAClient() throws MalformedURLException {
        HFCAClient caClient = HFCAClient.createNewInstance(CA_SERVICE_LOCATION, null);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return caClient;
    }

    private User enrollAdmin(String name, String enrollmentSecret) throws IOException, ClassNotFoundException {
        return usersStore.load("admin")
                .orElseGet(() -> {
                    try {
                        User admin = User.admin(name, enrollmentSecret, MSP_ID);
                        Enrollment enrollment = caClient.enroll(name, admin.getEnrollmentSecret());
                        admin.setEnrollment(enrollment);
                        return admin;
                    } catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private User registerAndEnrollUser(User registrar, String name, String mspId, String affiliation) throws Exception {
        return usersStore.load(name).orElseGet(() -> {
            User user = User.user(name, mspId, affiliation);
            try {
                user = registerUser(registrar, user);
                user = enrollUser(user);
                usersStore.save(user);
                return user;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private User registerUser(User registrar, User user) throws Exception {
        RegistrationRequest rr = new RegistrationRequest(user.getName(), user.getAffiliation());
        String enrollmentSecret = caClient.register(rr, registrar);
        user.setEnrollmentSecret(enrollmentSecret);

        return user;
    }

    private User enrollUser(User user) throws Exception {
        Enrollment enrollment = caClient.enroll(user.getName(), user.getEnrollmentSecret());
        user.setEnrollment(enrollment);
        return user;
    }

    private HFClient createHFClient(User user) throws CryptoException, InvalidArgumentException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        client.setUserContext(user);
        return client;
    }

    private Channel createAndInitializeChannel(String channelName, String peerLocation, String ordererLocation) throws InvalidArgumentException, TransactionException {
        Channel channel = client.newChannel(channelName);
        channel.addPeer(client.newPeer("peer", peerLocation));
        channel.addOrderer(client.newOrderer("orderer", ordererLocation));
        channel.initialize();
        return channel;
    }

    private ProposalResponse executeQueryOn(Channel channel, Query query) throws ProposalException, InvalidArgumentException {
        return channel.queryByChaincode(createQueryByChaincodeRequest(query)).stream().findFirst().orElse(null);
    }

    private QueryByChaincodeRequest createQueryByChaincodeRequest(Query query) {
        QueryByChaincodeRequest req = client.newQueryProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(query.getChaincode()).build();
        req.setChaincodeID(cid);
        req.setFcn(query.getFunction());
        req.setArgs(query.getArgs());
        return req;
    }
}