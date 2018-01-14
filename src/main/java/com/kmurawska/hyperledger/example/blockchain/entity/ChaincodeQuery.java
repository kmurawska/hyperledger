package com.kmurawska.hyperledger.example.blockchain.entity;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;

import javax.json.JsonObject;

public class ChaincodeQuery {
    private final String chaincode, functionName;
    private final String[] payload;
    private String identifier;

    public ChaincodeQuery(String chaincode, String functionName, String identifier, JsonObject payload) {
        this.chaincode = chaincode;
        this.functionName = functionName;
        this.identifier = identifier;
        this.payload = new String[]{identifier, payload.toString()};
    }

    public ChaincodeQuery(String chaincode, String functionName, String[] args) {
        this.chaincode = chaincode;
        this.functionName = functionName;
        this.payload = args;
    }

    public String getChaincode() {
        return chaincode;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String[] getQueryPayload() {
        return payload;
    }

    public QueryByChaincodeRequest forChaincodeRequest(HFClient client) {
        QueryByChaincodeRequest req = client.newQueryProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName(chaincode).build();
        req.setChaincodeID(cid);
        req.setFcn(functionName);
        req.setArgs(payload);
        return req;
    }
}