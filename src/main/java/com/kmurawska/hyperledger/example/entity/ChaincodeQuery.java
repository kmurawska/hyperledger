package com.kmurawska.hyperledger.example.entity;

public class ChaincodeQuery {
    private final String chaincode, function;
    private final String[] args;

    public ChaincodeQuery(String chaincode, String function, String[] args) {
        this.chaincode = chaincode;
        this.function = function;
        this.args = args;
    }

    public String getChaincode() {
        return chaincode;
    }

    public String getFunction() {
        return function;
    }

    public String[] getArgs() {
        return args;
    }
}
