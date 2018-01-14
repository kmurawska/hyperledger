package com.kmurawska.hyperledger.example.blockchain;

import com.kmurawska.hyperledger.example.blockchain.entity.ChaincodeQuery;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import javax.json.JsonObject;
import java.util.Collection;
import java.util.Optional;

class WriteQueryExecutor {
    private final HFClient client;
    private Channel channel;
    private Collection<ProposalResponse> proposalResponse;

    WriteQueryExecutor(User user) throws CryptoException, InvalidArgumentException {
        this.client = createHFClient();
        this.client.setUserContext(user);
    }

    private HFClient createHFClient() throws CryptoException, InvalidArgumentException {
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        return client;
    }

    WriteQueryExecutor onChannel(String channelName, String peerLocation, String ordererLocation, String eventHubLocation) throws TransactionException, InvalidArgumentException {
        channel = createAndInitializeChannel(channelName, peerLocation, ordererLocation, eventHubLocation);
        return this;
    }

    private Channel createAndInitializeChannel(String channelName, String peerLocation, String ordererLocation, String eventHubLocation) throws InvalidArgumentException, TransactionException {
        Channel channel = client.newChannel(channelName);
        channel.addPeer(client.newPeer("peer", peerLocation));
        channel.addOrderer(client.newOrderer("orderer", ordererLocation));
        channel.addEventHub(client.newEventHub("eventhub", eventHubLocation));
        channel.initialize();
        return channel;
    }

    WriteQueryExecutor execute(String chaincode, String functionToExecute, String identifier, JsonObject payload) throws ProposalException, InvalidArgumentException {
        ChaincodeQuery query = new ChaincodeQuery(chaincode, functionToExecute, identifier, payload);
        proposalResponse = channel.queryByChaincode(query.forChaincodeRequest(client));
        return this;
    }

    WriteQueryExecutor commit() throws ProposalException, InvalidArgumentException {
        channel.sendTransaction(proposalResponse);
        return this;
    }

    Optional<ProposalResponse> getProposalResponse() {
        return proposalResponse.stream().findFirst();
    }
}