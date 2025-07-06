---
layout: default
title: Requisiti
---

Per tornare alla [home](index.md)

# Requisiti

## Requisiti di business

Il sistema dovrà permettere all'utente di completare il gioco **Maze Escape**. In particolare sono stati individuati i
seguenti di business:
1) Possibilità di giocare una partita intera a Maze Escape.
2) L'utente deve poter visualizzare il menu di inizio e fine gioco.
3) Visualizzazione della mappa di gioco, delle statistiche della partita e del giocatore.
4) L'utente deve poter interagire con le relative componentistiche.
5) Deve essere possibile muoversi liberamente all'interno della mappa senza eccedere i confini prestabiliti.
6) Il gioco deve includere delle sfide con i guardiani che ostacolano il percorso del giocatore.

---

## Requisiti funzionali

Il gioco **Maze Escape** si compone di un insieme di entità e regole ben definite. Di seguito sono elencati i principali
requisiti funzionali:
1) Entità in gioco: Utente e Guardiani (NPC - Non-playable-character).
2) La mappa di gioco è strutturata tramite l'utilizzo di una griglia in cui sono presenti l'utente, le mura, le porte
per accedere al nuovo livello o per uscire dal labirinto e i guardiani.
3) Il gioco si basa su turni e ogni turno ha due fasi: una fase in cui l'utente si muove o interagisce con la porta con
l'obiettivo di superare la sfida; una fase in cui il guardiano cerca di intercettare l'utente.
4) Per sbloccare una porta l'utente deve superare un problema di natura logica.
5) Per vincere uno scontro con un guardiano l'utente può selezionare due diverse modalità:
   - Logica: deve superare un problema logico come nel caso relativo alla porta.
   - Fortuna: sia l'utente che il guardiano lanciano uno o più dadi, vince chi ottiene il punteggio più alto.
6) L'utente ha un numero finito di vite e ogni esito negativo dopo uno scontro con un guardiano comporta la perdita di
una vita.
7) Il gioco termina con successo se l'utente riesce a uscire dal labirinto, altrimenti termina se perde tutte le vite o
se il tempo si esaurisce.

---

## Requisiti utente

L'utente deve poter essere in grado di:
1) Lanciare l'applicativo per poter iniziare la partita.
2) Selezionare il livello di difficoltà.
3) Possibilità di navigare liberamente all'interno del labirinto e interagire con gli elementi di gioco.
4) Sfidare i guardiani risolvendo un enigma logico o tramite lancio dei dadi.
5) Visualizzare le statistiche della partita in corso.
6) Riavviare la partita a fine gioco.

---

## Requisiti non funzionali

1) Software in grado di essere facilmente estendibile con nuove funzionalità e componentistiche.
2) Interfaccia minimale che sia intuitiva per l'utente.

---

## Requisiti implementativi

1) Scala versione 3.x.
2) JUnit e ScalaTest.
3) GitHub e GitHub Actions.
4) IntelliJ IDEA e SBT.