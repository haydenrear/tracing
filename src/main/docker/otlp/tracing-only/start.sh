docker-compose down
#docker stop  fluentbit-container
#docker stop  jaeger-container
#docker stop  int-postgres
#docker stop  otlp-collector
docker container rm  fluentbit-container
docker container rm  jaeger-container
docker container rm  int-postgres
docker container rm  otlp-collector
docker-compose up -d