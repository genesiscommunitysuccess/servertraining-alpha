# servertraining-alpha

Answer-key of the Server Developer Training. From the [servertraining-seed](https://github.com/genesiscommunitysuccess/servertraining-seed), this is the complete application built during the [server developer training](https://docs.genesis.global/secure/getting-started/server_training/ssdt-intro/) and can be used as a reference for it.

# Building, Running and Testing
From the command line, cd into the root directory of the project and then follow these steps.

## Build
```shell
./gradlew assemble
```

## Run
* Make sure Docker is running on your machine and run:
```shell
docker-compose build
docker-compose up -d
```

* Attach a terminal to the `gsf` Docker container:
```shell
docker exec -it gsf bash
```
> Alternatively, you can use Docker Desktop Integrated Terminal for the containers you have just created (as explained [here](https://www.docker.com/blog/integrated-terminal-for-running-containers-extended-integration-with-containerd-and-more-in-docker-desktop-4-12/))

* Now try logging in as alpha 
```shell
su - alpha
```

* Check if all services are running:
```shell
mon
```
> Feel free to keep running `mon` until all services are RUNNING.

## Test
Allow up to 5 mins for all the services to be up and running, then open your browser and navigate to http://localhost:6060

# License

This is free and unencumbered software released into the public domain.

For full terms, see [LICENSE](./LICENSE)

**NOTE** This project uses licensed components listed in the next section, thus licenses for those components are required during development.

## Licensed components
Genesis low-code platform
