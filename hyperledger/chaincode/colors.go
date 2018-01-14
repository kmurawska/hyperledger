package main

import (
	"bytes"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

type SmartContract struct {
}

type Color struct {
	Family  string `json:"family"`
	Name    string `json:"name"`
	Rgb     string `json:"rgb"`
	Hex     string `json:"hex"`
	Example string `json:"example"`
}

func (sc *SmartContract) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

func (sc *SmartContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	functionName, functionParameters := stub.GetFunctionAndParameters()

	if functionName == "addNewColor" {
		return addNewColor(stub, functionParameters)
	} else if functionName == "showColorWithName" {
		return showColorWithName(stub, functionParameters)
	} else if functionName == "executeQuery" {
		return executeQuery(stub, functionParameters[0])
	}

	return shim.Error("Invalid Smart Contract function name.")
}

func showColorWithName(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect arguments. Expecting a key.")
	}

	colorWithName, err := stub.GetState(args[0])

	if err != nil {
		return shim.Error("Failed to get asset: " + args[0])
	}

	if colorWithName == nil {
		return shim.Error("Asset not found:" + args[0])
	}
	return shim.Success(colorWithName)
}

func addNewColor(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect arguments. Expecting a key and a value")
	}

	stub.PutState(args[0], []byte(args[1]))

	return shim.Success(nil)
}

func executeQuery(stub shim.ChaincodeStubInterface, query string) peer.Response {
	resultsIterator, err := stub.GetQueryResult(query)
	defer resultsIterator.Close()
	if err != nil {
		return shim.Error("Unable to execute query: " + query)
	}

	var buffer bytes.Buffer
	buffer.WriteString("[")
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error("Invalid response for: " + queryResponse.Key)
		}
		buffer.WriteString(string(queryResponse.Value))
		if resultsIterator.HasNext() {
			buffer.WriteString(",")
		}
	}

	buffer.WriteString("]")

	return shim.Success(buffer.Bytes())
}

func main() {
	if err := shim.Start(new(SmartContract)); err != nil {
		fmt.Printf("Error starting Color chaincode: %s", err)
	}
}
