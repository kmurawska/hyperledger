package main

import (
    "fmt"
    "github.com/hyperledger/fabric/core/chaincode/shim"
    "github.com/hyperledger/fabric/protos/peer"
)

type Color struct {
}

func (t *Color) Init(transactionProposal shim.ChaincodeStubInterface) peer.Response {
	transactionProposalArguments := transactionProposal.GetStringArgs()				
	if len(transactionProposalArguments) != 2 {
		return shim.Error("Incorrect arguments. Expecting a key and a value")
	}
	
    result := transactionProposal.PutState(transactionProposalArguments[0], []byte(transactionProposalArguments[1]))
    
	if result != nil {
		return shim.Error(fmt.Sprintf("Failed to create asset: %s", transactionProposalArguments[0]))
	}
	return shim.Success(nil)
}

func (t *Color) Invoke(transactionProposal shim.ChaincodeStubInterface) peer.Response {
	functionName, functionParameters := transactionProposal.GetFunctionAndParameters()

	var result string

    if functionName == "set" {
        result = set(transactionProposal, functionParameters)
    } else {
        result = get(transactionProposal, functionParameters)
    }

    return shim.Success([]byte(result))
}


func set(stub shim.ChaincodeStubInterface, args []string) (string) {
    if len(args) != 2 {
		return "Incorrect arguments. Expecting a key and a value"
    }

    err := stub.PutState(args[0], []byte(args[1]))
    if err != nil {
		return "Failed to set asset: " + args[0]
    }
    return args[1]
}

func get(stub shim.ChaincodeStubInterface, args []string) (string) {
    if len(args) != 1 {
        return "Incorrect arguments. Expecting a key"
    }

    value, err := stub.GetState(args[0])
    if err != nil {
        return "Failed to get asset: " + args[0]
    }
    if value == nil {
        return "Asset not found:" + args[0]
    }
    return string("apple")
}

func main() {
        if err := shim.Start(new(Color)); err != nil {
                fmt.Printf("Error starting Color chaincode: %s", err)
        }
    }