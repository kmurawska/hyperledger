package main

import (
	"encoding/json"
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

	if functionName == "initLedger" {
		return initLedger(stub)
	} else if functionName == "addNewColor" {
		return addNewColor(stub, functionParameters)
	} else if functionName == "showColorWithName" {
		return showColorWithName(stub, functionParameters)
	}

	return shim.Error("Invalid Smart Contract function name.")
}

func initLedger(stub shim.ChaincodeStubInterface) peer.Response {
	lawngreen := Color{Name: "lawngreen", Rgb: "rgb(124,252,0)", Hex: "#7CFC00", Example: "grass"}
	lawngreenAsBytes, _ := json.Marshal(lawngreen)
	stub.PutState("green", lawngreenAsBytes)
	return shim.Success(nil)
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

func main() {
	if err := shim.Start(new(SmartContract)); err != nil {
		fmt.Printf("Error starting Color chaincode: %s", err)
	}
}
