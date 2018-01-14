# hyperledger-example

#### From scratch (Windows)

1. ###### Create a docker machine 

    run `upMachine.ps1` script from ci directory  

2. ###### Pull hyperledger fabric-ca docker images
 
3. ###### Generate cryptographic materials

    run `generate_crypto_materials.sh` script from hyperledger directory

4. ###### Adjust `FABRIC_CA_SERVER_CA_KEYFILE` environment variable for Hyperledger Fabric CA service 

    update `docker-compose.yml` file and set `FABRIC_CA_SERVER_CA_KEYFILE` to file name from crypto-config/peerOrganizations/org1.example.com/ca/
  
5. ###### Start a hyperledger fabric network
   
    run `upNetwork.ps1` script from ci directory
  
6. ###### Create and join peers to a new channel
    
    run `create_and_join_channel.ps1` script from hyperledger directory
    
7. ###### Install and instantiate a chaincode 

    run `install_and_instantiate_chaincode.ps1` script from hyperledger directory

