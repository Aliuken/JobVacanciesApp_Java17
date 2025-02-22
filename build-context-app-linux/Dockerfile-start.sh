sudo docker build -t job-vacancies-app --build-arg PRINT_ARG=new_value -f Dockerfile .
sudo docker network create external-net-app

sudo docker container run -d --name tomcat-app --network external-net-app -p 9080:8080 -v /AppData_Java17/JobVacanciesApp:/AppData_Java17/JobVacanciesApp job-vacancies-app
