Nume: Țălină Laura-Florina
Grupa: 334CB

TEMA2 APD
Proiectul are in componenta urmatoarele clase:
-Tema2
-Map
-MapResult
-Reduce
-ReduceResult
-FinalInformation

Tema2
In cadrul main-ului se citesc informatiile necesare din fisier, dupa care se
creeaza un ForkJoinPool pentru executarea operatiilor de tip Map, rezultatul
task-urilor de tip Map fiind pastrate intr-o lista "mapResults". Pentru a fi
siguri ca task-urile de tip Map se termina inainte de inceperea oricarui task
Reduce, pentru task-urile de tip Reduce se va creea un al doilea ForkJoinPool.
Rezultatele vor fi pastrate intr-un HashMap ce va avea ca si cheie numele
documentului, iar ca valori un obiect de tip ReduceResult (va fi detaliat
mai jos).

Map
Pentru a putea returna un obiect, clasa Map definita extinde RecursiveTask<Map
Result>

In cadrul metodei Compute, avem la dispozitie deja cuvintele corespunzatoare
fragmentului, prin apelarea functiei "getFragmentWords", urmand ca pentru
fiecare cuvant sa adaugam/sau sa updatam o intrare, in care drept cheie va
fi lungimea cuvantului, iar ca valoare vom avea numarul de cuvinte cu acea
lungime. De asemenea, intr-un ArrayList "maxLenWords" se vor pastra cuvintele
de lungime maxima pentru fragment. Functia de Compute va returna un obiect
ded tip MapResult, ce va contine numele documentului, hashMap-ul cu lungimile
si numarul de aparitii, si ArrayList-ul cu cuvintele de lungime maxima.

O metoda importanta este "getFragmentWords" in cadrul careia se citeste
fragmentul prin pozitionarea in fisier la offsetul corespunzator si
citirea intr-un vector de dimensiunea specificata in fisierul de intrare.
Se verifica apoi daca fragmentul incepe sau se termina in mijlocul unui
cuvant si se retin aceste informatii. Se vor separa cuvintele dupa
separatori, folosind functia "split", iar cuvintele de tipul "" vor fi
eliminate. Dupa aceasta, se regleaza si capetele fragmentului.
In final. metoda returneaza un ArrayList continand toate cuvintele
fragmentului.
Aceasta metoda mi s-a parut cea mai complicata din intreaga tema,
motiv pentru care am adaugat in cod comentarii sugestive care urmaresc
logica implemntarii.


MapResult
Aceasta clasa defineste rezultatul unui task de tip Map, avand numele
documentului, un HashMap ce contine drept cheie lungimea cuvintelor
si drept valoare numarul de cuvinte cu respectiva lungime, si un ArrayList
cu toate cuvintele de lungime maximala.


Reduce
Metoda "Combine" unifica HashMap-urile rezultate din procesarea fragmentelor
unui document, si pastreaza intr-un ArrayList cuvintele de lungime maxima
din tot documentul. Rezultatul intors este tot un obiect de tipul MapResult
avand ca atribute numele documentului si HashMap-ul si ArrayList-ul descris
anterior.
Tot in aceasta metoda, se returneaza prin efect lateral si numarul de cuvinte
maximale, precum si dimensiunea lor.

Metoda "Process" returneaza rangul documentului. Deoarece in urma impartirii
fragmentului in cuvinte, au rezultat si cuvinte de tipul "", exista o conditie
pentru ca cuvintele de dimensiune 0 sa nu fie luate in calcul.


ReduceResult
Clasa modeleaza rezultatul unui task de tip Reduce, avand ca atribute
rangul, dimensiunea cuvintelor maximale si numarul lor.


FinalInformation
Avand calculate rang-ul fiecarui fisier, precum si numarul si dimensiunea
cuvintelor maximale, pentru scrierea in fisier a rezultatelor, acestea
au trebuit sa fie sortate in functie de rang, iar pentru fisierele
cu acelasi rang a trebuit sa se tina cont de ordinea in care apar in
fisierul de intrare.
Pentru aceasta, am definit o noua clasa "FinalInformation" in care
pastrez numele documentului, index-ul (reprezentand indexul pe care
apare in fisierul de intrare) si un obiect de tip ReduceResult, care
contine la randul sau dimensiunea si numarul cuvintelor maximale. Acestea
sunt de fapt informatiile care trebuiesc afisate si sortate.
Am implementat metoda compareTo pentru aceasta clasa, tinand cont
de specificatiile din enuntul temei, pentru ca a putea apoi in main,
pentru fiecare fisier sa creez un astfel de obiect "FinalInformation",
pe care l-am adaugat intr-un ArrayList "finalInfo". Am utilizat apoi
"Collections.sort" pentru sortarea corespunzatoare a informatiilor.

* De asemenea, am definit peste tot "documentName", insa in final mi-am
dat seama ca aceasta este de fapt calea spre fisier, descrisa in fisierul
de intrare. Motiv pentru care, atunci cand scriu in fisierul de output,
selectez din aceasta cale cuvantul care apare dupa ultimul "/", acesta
fiind de fapt numele fisierului.