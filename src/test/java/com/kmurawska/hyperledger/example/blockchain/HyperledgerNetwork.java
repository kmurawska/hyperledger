package com.kmurawska.hyperledger.example.blockchain;

class HyperledgerNetwork {
    static final String USER_STORE_PATH = "D:\\IT\\workspace\\hyperledger-example\\hyperledger\\users-store";
    static final String CA_SERVICE_LOCATION = "http://localhost:7054";
    static final String PEER_LOCATION = "grpc://localhost:7051";
    static final String ORDERER_LOCATION = "grpc://localhost:7050";
    static final String EVENT_HUB_LOCATION = "grpc://localhost:7053";
    static final String MSP_ID = "Org1MSP";
    static final String AFFILIATION = "org1.department1";
    static final String CHANNEL_NAME = "rainbow";
}
