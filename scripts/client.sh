#!/bin/bash


NUM_CLIENTES=20


mkdir resultado_clientes

for ((i=1; i<=$NUM_CLIENTES; i++)); do
  ./prueba.sh  $i &
done

wait

echo "Prueba terminada"
