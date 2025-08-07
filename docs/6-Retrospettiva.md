---
layout: default
title: Retrospettiva
---

Per tornare alla [home](index.md)

# Retrospettiva

I documenti relativi alle varie iterazioni (sprint) sono consultabili nelle varie pagine del sito. In questa sezione
vengono condivise l'analisi retrospettiva del processo di sviluppo del progetto, descrivendone l’evoluzione, i punti di
forza e le difficoltà incontrate.

## Processo di sviluppo

### Incontri regolari

Gli incontri a cadenza giornaliera si sono dimostrati particolarmente efficaci per supportare un design incrementale e
mantenere un buon coordinamento tra i membri del team. Con il progredire del progetto e l’evoluzione dell’organizzazione
del gruppo, i daily meeting si sono progressivamente ridotti. La frequenza degli incontri è infatti diminuita a favore
di un confronto più informale, favorito dalla modularità e flessibilità dei componenti. Questo cambio di direzione non
ha compromesso la qualità della collaborazione, grazie alla possibilità di confrontarci frequentemente e alla dimensione
contenuta del team, che ha reso superflua un’organizzazione troppo rigida.

### Pair Programming

È stato adottato un approccio Agile, focalizzato sulla produttività e sull’efficienza comunicativa. La definizione
esaustiva del comportamento del sistema e dei requisiti, nella fase iniziale di sviluppo, ha garantito una certa
asincronia nell'implementazione dei vari moduli del sistema. Allo stesso tempo, molte delle funzionalità sono state
sviluppate e modificate in corsa tramite incontri e sessioni online, principalmente tramite l'utilizzo dell'applicativo
Microsoft Teams, assimilabili a momenti di pair programming. Questo tipo di interazione ha garantito rapidità decisionale
e condivisione continua, particolarmente utile in un gruppo ristretto, dove la comunicazione diretta è risultata molto 
più efficace rispetto a processi formali e burocratici. In sintesi, è stato trovato un equilibrio tra struttura e
agilità, evitando carichi organizzativi superflui e privilegiando strumenti semplici e momenti di confronto efficaci.

### Commenti finali

Il problema principale del Product Owner è stata direzionare e organizzare in modo efficace l'assegnamento delle varie
task tra i membri del team. In particolare non sono state calcolate con rigore le tempistiche associate allo sviluppo
dei vari moduli. Detto questo è stato riconosciuto dai membri del team come il linguaggio Scala si presti in modo
naturale a una metodologia di sviluppo Agile. La ricchezza delle sue astrazioni, unite a una forte espressività, ha
favorito un processo iterativo basato su refactoring costanti e scelte progettuali eleganti. Nel complesso lo stato del
sistema sembra essere a un livello adeguato considerando che tutti gli obiettivi, sia quelli prefissati che quelli
forniteci in corso d'opera, risultano essere stati soddisfatti. L'unico aspetto oggettivamente negativo del sistema 
risulta essere l'implementazione della classe **Game** che poco ha a che vedere con il resto dei moduli, caratterizzati
da strutture idiomatiche a Scala 3 e paradigmi funzionali.