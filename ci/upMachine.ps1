$machineName = "hyperledger"

$dockerMachines = docker-machine ls 
if ($dockerMachines -Like "*hyperledger*") {
	echo "Starting docker machine..."	
	
	echo "Adding shared folders..."
	$hyperledger = [System.IO.Path]::GetFullPath((Join-Path (pwd) '..\hyperledger'))
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" sharedfolder add $machineName --name "/etc/dupa" --hostpath "$hyperledger" --automount
	echo "Shared folders added."
    echo ""
	
	docker-machine start $machineName
	docker-machine env $machineName
	&docker-machine env $machineName | Invoke-Expression
	echo "Docker machine started."
} else {
	echo "Creating docker machine..." 
	docker-machine create --driver virtualbox --virtualbox-disk-size "30000" --virtualbox-memory "4096" --virtualbox-cpu-count "2" --virtualbox-hostonly-cidr "192.168.90.1/24" $machineName 
	docker-machine start $machineName 
	docker-machine env $machineName
	&docker-machine env $machineName | Invoke-Expression
	echo "Docker machine created."
	
	echo "Fixing incorrect network adapter type..."
	docker-machine stop $machineName
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" modifyvm hyperledger --nictype1 Am79C973
	echo "Network adapter type fixed."

	echo "Setting ports forwarding on Oracle VirtualBox machine..."
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "CA,tcp,,7054,,7054"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "orderer,tcp,,7050,,7050"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "peer,tcp,,7051,,7051"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "peer_events,tcp,,7053,,7053"
	echo "Setting ports forwarding finished."
	
    echo "Adding shared folders..."
	$hyperledger = [System.IO.Path]::GetFullPath((Join-Path (pwd) '..\hyperledger'))
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" sharedfolder add $machineName --name "/etc/hyperledger" --hostpath "$hyperledger" --automount
	echo "Shared folders added."
    echo ""

	docker-machine start $machineName
}