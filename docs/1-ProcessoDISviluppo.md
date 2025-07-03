---
layout: default
title: Primo incontro
---

Per tornare alla [home](index.md)

# Processo di sviluppo

Il processo di sviluppo del sistema si basa su Scrum, in particolare sono stati delineati quattro diversi Sprint per
sviluppare e realizzare progressivamente il progetto in maniera agile. Ogni sprint ha una durata temporale della durata
di circa un paio di settimane, durante il quale si definiscono nuovi obiettivi e si valuta l'andamento della sessione
precedente, si valutano le eventuali richieste del committente e si raffinano i requisiti del sistema. Sarà inoltre
necessario suddividere il carico di lavoro tra i membri del team.

All'interno del team sono stati definiti i ruoli principali: il Product Owner e il Committente.

---

## Meeting
I meeting rappresentano lo strumento fondamentale per sostenere al meglio il processo di sviluppo appena descritto
e sarà necessario che abbiano cadenza giornaliera. Questi incontri permettono di delineare in modo sistematico i
progressi e le eventuali problematiche.

---

## Modalità di divisione dei task

### Definizione di task completato
Una funzionalità di gioco viene definita completata quando, a seguito di una revisione da parte di entrambi i componenti
del team o in pair programming, viene caricata sul branch di sviluppo garantendo il soddisfacimento della suite di test
e che il sistema passi da uno stato stabile a un altro stato stabile.

### Meeting iniziale
Il primo incontro ha avuto lo scopo di definire due importanti fattori:
- Ruoli: Michael Rubri sarà il product owner e Candela Esparrica Torrecilla il committente. Entrambi svolgono inoltre il
ruolo di sviluppatore.
- Specifiche: sono state attentamente valutate le caratteristiche principali del sistema e gli obiettivi da conseguire.

### Sprint planning
Durante ogni sprint si discutono sia i risultati che le problematiche dello sprint precedente e si definiscono
conseguentemente gli obiettivi del successivo. I principali punti di discussione riguardano:
- Definizione degli obiettivi
- Definizione e assegnazione dei task
- Valutazione dell’andamento complessivo del progetto
- Valutazione dello sprint precedente

La dimensione limitata del team, composto da due membri, comporta una durata degli sprint compresa tra la singola ora
e le due ore.

### Divisione dei compiti
L’effettiva divisione dei compiti da eseguire nello sprint successivo, viene fatta contestualmente alla chiusura dello
sprint precedente. La suddivisione terrà conto del carico di lavoro, degli impegni del singolo componente e di eventuale
lavoro incompiuto.

---

## Strumenti di supporto
Per supportare il processo di sviluppo agile, il team utilizza strumenti volti a migliorare l’efficienza dell'intero
processo.

### Automazione
Il progetto ha integrato pratiche moderne per la manutenzione continua del sistema, definita continuous integration
(CI). Il workflow test.yml esegue i test su ogni pull-request aperta o sincronizzata a partire dal branch develop verso
il branch main. Questo flusso garantisce l’integrità del sistema e da la possibilità al proprietario del repository e
agli eventuali collaboratori e sviluppatori di avere una visione completa del progetto e delle eventuali problematiche a
seguito dell'esecuzione della suite di test.