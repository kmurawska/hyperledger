package com.kmurawska.hyperledger;

class Query {
    private final String chaincode, function;
    private final String[] args;

    Query(String chaincode, String function, String[] args) {
        this.chaincode = chaincode;
        this.function = function;
        this.args = args;
    }

    String getChaincode() {
        return chaincode;
    }

    String getFunction() {
        return function;
    }

    String[] getArgs() {
        return args;
    }
}
