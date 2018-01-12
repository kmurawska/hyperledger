#!/usr/bin/env bash
rm -rf crypto-config

export FABRIC_CFG_PATH=$PWD

./bin/cryptogen generate --config=./crypto-config.yaml
./bin/configtxgen -profile ColorsOrdererGenesis -outputBlock ./channel-artifacts/genesis.block
./bin/configtxgen -profile RainbowChannel -outputCreateChannelTx ./channel-artifacts/channel.tx -channelID rainbow
./bin/configtxgen -profile RainbowChannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors.tx -channelID rainbow -asOrg Org1MSP