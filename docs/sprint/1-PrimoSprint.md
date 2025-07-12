---
layout: default
title: Primo Sprint
---

Per tornare alla [home](../index.md)

# Primo Sprint - 26/06/2025

In questo incontro il team ha revisionato gli obiettivi generali del sistema da conseguire e sono state dunque assegnate
le varie task ai membri del gruppo di sviluppo al fine di implementare nel più breve tempo possibile un sistema
funzionante, da poter consegnare al committente in modo che possa verificare concretamente l'evoluzione e l'andamento
del suo investimento. Per questo motivo nel primo rilascio saranno sviluppate parzialmente alcune caratteristiche per
rispettare le scadenze temporali e alcune saranno aggiunte solo successivamente come estensione a un sistema funzionante.

## Architettura

Data la natura del problema il team ha scelto MVC (Model-View-Controller) come architettura, in quanto supporta
totalmente l'idea del progetto.

## Obiettivi Sprint

- Inizializzazione del progetto:
  - Creazione del repository su GitHub.
  - Creazione delle GitHub Pages.
  - Implementazione delle GitHub Actions.
  - Configurazione dell'IDE IntelliJ scelto come ambiente di sviluppo.
  - Configurazione del tool di automazione SBT.
- Implementazione del sistema base:
  - Labirinto generato proceduralmente.
  - Entità in gioco: Player e Guardian.
  - Stato della partita.
- Implementazione del controller per gestire l'input dell'utente e propagare gli eventi generati da quest'ultimo.
- Implementazione della view.

## Planning & Comunicazione

- Incontri frequenti con cadenza giornaliera.
- Collaborazione diretta per la revisione del codice e delle decisioni progettuali.
- Comunicazione continua e confronto immediato.

Deadline sprint: 11/07/2025

## Divisione del lavoro

**Michael Rubri:**
- Implementazione delle entità Player e Guardian.
- Implementazione dello stato di gioco.
- Implementazione delle azioni eseguibili dall'utente.
- Sviluppo di un modulo per la generazione degli enigmi.

**Candela Esparrica Torrecilla:**
- Sviluppo del labirinto e delle sue componentistiche.
- Studio degli algoritmi per la generazione procedurale della mappa.
- Sviluppo del game controller.
- Sviluppo della GUI.

## Revisione dello sprint - 11/07/2025

I seguenti obiettivi sono stati conseguiti come preventivato:
1) Setup del progetto completato
2) Implementazione GitHub Actions per automatizzare i test. 
3) Generazione entità Player e Guardian. 
4) Implementazione stato di gioco. 
5) Definizione delle azioni eseguibili dall'utente
6) Implementazione del modulo relativo agli enigmi.
7) Sviluppo e generazione del labirinto.
8) Implementazione del game controller.

L'ultimo obiettivo relativo allo sviluppo della GUI non è stato conseguito totalmente. Il progetto allo stato attuale
presenta un'interfaccia grafica sviluppata in Java ma il progetto non risulta essere compilabile a causa di alcune
incompatibilità tra la GUI e la generazione della mappa. Il team ha dunque valutato di posticipare tale obiettivo nello
sprint successivo e migrare l'interfaccia da JavaFx a ScalaFx.