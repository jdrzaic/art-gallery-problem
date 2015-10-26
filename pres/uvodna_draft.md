# Opis problema
* Art Gallery Problem se svodi na minimiziranje broja stražara/kamera potrebih za osiguravanje
  galerije.

* Dan je tlocrt galerije u obliku poligona kao unos u algoritam. Rješenje se sastoji od niza
  koordinata na koje treba postaviti kamere tako da je svaki dio galerije pokriven barem jednim
  vidnim poljem neke kamere.

* Od dobrog algoritma se ocekuje da minimizira ukupan broj potrebnih kamera.

# Vizualizacije
* <ubaciti sliku s wikipedije mozda?>

# Varijante
* Poznato je da je problem NP-tezak.

* Postoje razne varijacije. Primjerice, kamere mogu biti ogranicene tako da ih mozemo postavljati
  samo na kutove u tlocrtu.
  Za taj problem, iako je i dalje NP-tezak, postoje relativno efikasni algoritmi koji pronalaze
  optimalna rjesenja.

* Mi rjesavamo tezi oblik u kojem polozaj kamera moze biti bilo gdje u tlocrtu (kamere su recimo
  fiksirane na strop).

# Prijasnja istrazivanja
* Problem je popularan zbog dosta direktnih primjena pa postoje moga rjesenja s razlicitim
  pristupima.

* <tu molim te navedi jedan od onih koje si nasla i mozda jednu recenicu o njihovom pristupu>

# Pristup
* Ideja je testirati nekoliko klasicnih meta-heuristika i utvrditi koja daje bolje rezultate.

* Provjeriti moze li se dobiti optimalnije rjesenje (u smislu broja kamera) ako modificiramo
  uvjete problema (recimo, ne zahtjevamo 100% pokrivenost)

* Provjeriti moze li se postici bolje rjesenje ako kombiniramo vise algoritama

* Rjesenja testiramo na prethodno generiranim primjercima s
  http://www.ic.unicamp.br/~cid/Problem-instances/Art-Gallery/AGPPG/index.html

# Implementacija
* Plan je koristiti programske jezike Java i Haskell u kombinaciji.

* Sucelje i neke od algoritama implementirali bi u Javi i barem jedan algoritam u Haskellu
  <mozda glupo i nepotrebno za spominjati> 
