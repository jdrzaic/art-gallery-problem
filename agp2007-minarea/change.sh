#!/bin/sh

aux=8
while [ $aux -le 200 ]; do
   for file in `ls -1 ./rand-$aux-*.pol`; do
      tmp=`echo $file | sed -e "s/\.\/rand-//g" -e 's/.pol//g' -e's/-/ /g'`
      vertex=`echo $tmp | { read r1 r2; echo "$r1"; }`
      it=`echo $tmp | { read r1 r2; echo "$r2"; }`
      mv $file min-$vertex-1.pol
   done

   aux=`expr $aux + 2`
done
