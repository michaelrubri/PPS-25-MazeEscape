## Controller
Il trait UserActionHandler rappresenta l’interfaccia comune per tutti i gestori di eventi dell’utente. Controller ne è l’unica implementazione ed è responsabile della logica di controllo della partita, secondo il pattern MVC, in cui la view e il modello sono separati.

All’interno del controller viene gestita tutta la logica di flusso, mentre la logica di dominio è delegata alla classe Game. Le azioni dell’utente sono modellate tramite un sealed trait UserAction, seguendo il pattern Command. L’integrazione con GameView avviene tramite metodi come updateView, showPuzzle, showMessage e showFightChoice.

L’utilizzo del EventBus implementa un pattern Observer, permettendo di decoppiare la view dal controller. Ogni clic dell’utente sulla mappa viene trasformato in un CellClickEvent, che viene poi interpretato e processato.

![UML Diagram Controller](Images/MazeEscape%20-%20Controller.png)

## Prolog

Il package model.strategies contiene la logica strategica che guida il comportamento dei guardiani del gioco.
In esso troviamo la classe GuardianStrategy, che incapsula una strategia di movimento basata su un codice Prolog.

La classe accetta nel costruttore una funzione di tipo Term => LazyList[Term], ottenuta tramite il metodo Scala2Prolog.mkPrologEngine(), che genera dinamicamente un interprete Prolog con una teoria basata sulla struttura del labirinto (Maze).

Tale teoria è costruita da MazePrologTheory, che genera fatti walkable(X,Y). per ogni cella accessibile del labirinto, e regole per trovare percorsi validi e la prossima mossa ottimale.

Questo design permette di mantenere disaccoppiata la logica dei movimenti dei guardiani (scritta in linguaggio dichiarativo - Prolog) dal resto del programma, che segue una logica imperativa. Inoltre, facilita la sostituzione della strategia attuale con qualsiasi altra.

![UML Diagram Prolog](Images/MazeEscape%20-%20Prolog.png)