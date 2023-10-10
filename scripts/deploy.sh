#!/bin/bash

# Parsear los argumentos
while getopts s:u:p:f:l: option
do
case "${option}"
in
s) SERVER=${OPTARG};;
u) USER=${OPTARG};;
p) PASS=${OPTARG};;
f) FIRST=${OPTARG};;
l) LAST=${OPTARG};;
esac
done

# Validar los argumentos
if [ -z "$SERVER" ] || [ -z "$USER" ] || [ -z "$PASS" ] || [ -z "$FIRST" ] || [ -z "$LAST" ]
then
    echo "Faltan argumentos. Uso: deploy.sh -s server -u user -p pass -f first -l last"
    exit 1
fi

if [ $FIRST -gt 30 ] || [ $FIRST -lt 1 ] || [ $LAST -gt 30 ] || [ $LAST -lt 1 ]
then
    echo "Los números de los computadores deben estar entre 1 y 30"
    exit 1
fi

if [ $FIRST -gt $LAST ]
then
    echo "El primer computador debe ser menor o igual que el último"
    exit 1
fi

# Definir la ruta del archivo jar de Ice en tu computador
LOCAL_ICE_JAR=/Users/juanjose/.gradle/caches/modules-2/files-2.1/com.zeroc/ice/3.7.6/6c7f86b7f9e5f0804a7307077e6c058d8a93fa70/ice-3.7.6.jar

# Definir la ruta del archivo jar de Ice en los computadores remotos
REMOTE_ICE_JAR=~/Documents/ice-3.7.6.jar

# Copiar el archivo server.jar al servidor
echo "Copiando el archivo server.jar y Ice.jar al servidor $SERVER"
scp -o StrictHostKeyChecking=no server/build/libs/server.jar /Users/juanjose/.gradle/caches/modules-2/files-2.1/com.zeroc/ice/3.7.6/6c7f86b7f9e5f0804a7307077e6c058d8a93fa70/ice-3.7.6.jar $USER@$SERVER:~/Documents


# Compilar el archivo server.jar en el servidor
echo "Compilando el archivo server.jar en el servidor $SERVER"
ssh -o StrictHostKeyChecking=no $USER@$SERVER " cd Documents && java -cp './*' Server &"

# Copiar el archivo client.jar a los clientes
for i in $(seq $FIRST $LAST)
do
    echo "Copiando el archivo client.jar al cliente xhgrid$i"
    scp -o StrictHostKeyChecking=no client/build/libs/client.jar /Users/juanjose/.gradle/caches/modules-2/files-2.1/com.zeroc/ice/3.7.6/6c7f86b7f9e5f0804a7307077e6c058d8a93fa70/ice-3.7.6.jar $USER@xhgrid$i:~/Documents
done


# Ejecutar el archivo client.jar en los clientes
for i in $(seq $FIRST $LAST)
do
    echo "Ejecutando el archivo client.jar en el cliente xhgrid$i"
     ssh -o StrictHostKeyChecking=no $USER@xhgrid$i "cd Documents && java -cp './*' Client &"
done

echo "Eliminando los archivos server.jar, client.jar y Ice.jar de los computadores remotos"

# Eliminar los archivos del servidor
echo "Eliminando los archivos del servidor $SERVER"
ssh -o StrictHostKeyChecking=no $USER@$SERVER "rm ~/Documents/server.jar ~/Documents/ice-3.7.6.jar"

# Eliminar los archivos de los clientes
for i in $(seq $FIRST $LAST)
do
    echo "Eliminando los archivos del cliente xhgrid$i"
    ssh -o StrictHostKeyChecking=no $USER@xhgrid$i "rm ~/Documents/client.jar ~/Documents/ice-3.7.6.jar"
done

echo "El deployment ha terminado exitosamente"

