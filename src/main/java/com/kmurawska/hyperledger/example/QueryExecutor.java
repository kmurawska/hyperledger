package com.kmurawska.hyperledger.example;

import com.kmurawska.hyperledger.example.entity.ChaincodeQuery;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

class QueryExecutor {
    private final HFClient client;
    private Channel channel;

    QueryExecutor(com.kmurawska.hyperledger.example.entity.User user) throws CryptoException, InvalidArgumentException {
        this.client = createHFClient();
        this.client.setUserContext(user);
    }

    private HFClient createHFClient() throws CryptoException, InvalidArgumentException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return client;
    }

    QueryExecutor onChannel(String channelName, String peerLocation, String ordererLocation) throws TransactionException, InvalidArgumentException {
        channel = createAndInitializeChannel(channelName, peerLocation, ordererLocation);
        return this;
    }

    private Channel createAndInitializeChannel(String channelName, String peerLocation, String ordererLocation) throws InvalidArgumentException, TransactionException {
        Channel channel = client.newChannel(channelName);
        channel.addPeer(client.newPeer("peer", peerLocation));
        channel.addOrderer(client.newOrderer("orderer", ordererLocation));
        channel.initialize();
        return channel;
    }

    ProposalResponse execute(String chaincode, String functionToExecute, String... functionArguments) throws ProposalException, InvalidArgumentException {
        ChaincodeQuery query = new ChaincodeQuery(chaincode, functionToExecute, functionArguments);
        return channel.queryByChaincode(createQueryByChaincodeRequest(query)).stream().findFirst().orElse(null);
    }

    private QueryByChaincodeRequest createQueryByChaincodeRequest(ChaincodeQuery query) {
        QueryByChaincodeRequest req = client.newQueryProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(query.getChaincode()).build();
        req.setChaincodeID(cid);
        req.setFcn(query.getFunction());
        req.setArgs(query.getArgs());
        return req;
    }

}
