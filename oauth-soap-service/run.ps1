docker stop soap-server
docker rm soap-server
docker run -d -p 8080:8080 --name soap-server soap-server:latest
docker logs -f soap-server