param([string] $version)

if(-not($version)) { Throw "You must supply a value for chaincode version" }

docker exec cli peer chaincode install -n colors -v $version -p "github.com/chaincode" -l "golang"

docker exec cli peer chaincode upgrade -o orderer.example.com:7050 -n colors -v $version -C rainbow -c '{\"args\":[\"green\",\"apple\"]}'