clear

runas /user:Usuario "C:\Programacion\git\JobVacanciesApp_Java17\build-context-app\docker-compose-stop.bat"
runas /user:Usuario "C:\Programacion\git\JobVacanciesApp_Java17\build-context-elk\docker-compose-stop.bat"

docker image prune -a -f
docker volume prune -a -f

docker network rm "external-net-app"
docker network rm "external-net-elk"

docker system prune -a -f
