
param1=$0
param2=$1

echo "Parametro 1 $param1 Parametro2 $param2"

# Así se asigna una variable
# var=$(ls)


#Con este comando se puede ejecutar el script por debajo del sistema, es decir que si se cierra la consola todavía sigue corriendo el scrip
nohup java -jar server/build/libs/server.jar &

#Este comando me lista los procesos que está corriendo el pc
#ps -ef | grep #usuario

