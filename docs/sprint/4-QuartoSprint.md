---
layout: default
title: Quarto Sprint
---

Per tornare alla [home](../index.md)

# Terzo Sprint - 28/07/2025

## Obiettivi Sprint
 
Obiettivo di questo sprint sarà innanzitutto quello di ultimare l'implementazione del sistema per garantire un
funzionamento corretto quando la difficoltà viene impostata a "normal" o "hard". Sarà inoltre necessario rilasciare la
versione di gioco finale, soddisfacendo tutte le richieste del committente e conseguentemente sviluppare e correggere la
documentazione relativa al sistema.

Il committente dopo la revisione ha richiesto un'estensione del sistema. In particolare vorrebbe che il giocatore
possieda un inventario di strumenti generici che lo possano aiutare nel fuggire dal labirinto. Non è stato definito come
questi oggetti siano recuperati dal giocatore, lasciando l'iniziativa al team di sviluppo.

---

## Planning & Comunicazione

- Incontri frequenti con cadenza giornaliera.
- Collaborazione diretta per la revisione del codice e delle decisioni progettuali.
- Comunicazione continua e confronto immediato.

Deadline sprint: 04/08/2025

---

## Divisione del lavoro

**Michael Rubri:**
- Revisione modulo **Game** per garantire il funzionamento corretto dell'applicativo.
- Implementazione **Inventory** e **Item**
- Revisione del sistema a seguito di modifiche.

**Candela Esparrica Torrecilla:**
- Revisione modulo **Controller** per garantire il funzionamento corretto dell'applicativo.
- Revisione modulo **Maze** per garantire il funzionamento corretto dell'applicativo.
- Bilanciamento delle impostazioni di gioco in base alla difficoltà.
- Revisione del sistema a seguito di modifiche.

## Revisione dello sprint - 04/08/2025

Il team ha raggiunto con successo tutti gli obiettivi prefissati. Il sistema risulta attualmente giocabile e con le
modifiche sostanziali apportate in questo sprint si procederà con il rilascio della versione 1.0.1.

**N.B.** Attualmente è stato implementato solo il modulo **Inventory**, con tutti i metodi necessari per la gestione
degli oggetti al suo interno, e il modulo **Item**, che modella il singolo oggetto. Eventuali evoluzioni future
permetteranno di definire come avvenga il recupero di tali oggetti. Una soluzione molto semplice sarebbe quella di
generali su alcune celle calpestabili del labirinto oppure introdurre nuove entità in grado di fornire questo oggetti
a seguito di meccaniche di gioco ben definite.