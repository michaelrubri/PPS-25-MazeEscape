## CONTROLLER
Controller è l’implementazione di UserActionHandler, ed è responsabile della gestione delle azioni dell’utente all’interno del gioco. Viene istanziato all’avvio della partita e collega la logica del gioco (Game) con l’interfaccia grafica (GameView). All’interno del costruttore, viene inizializzata la view e viene sottoscritto un listener al EventBus per ricevere eventi di clic sulla griglia.

All’interno della classe vengono gestite tutte le azioni rappresentate tramite il sealed trait UserAction, tra cui:

- ClickCell: intercetta il clic dell’utente su una cella e determina la logica da seguire in base al tipo di cella;

- AttemptMove: tenta di muovere il giocatore, aggiorna lo stato del gioco e apre eventualmente il menu di combattimento o fine partita;

- AttemptOpenDoor: permette al giocatore di provare a risolvere un puzzle e aprire una porta;

- FightLogic e FightLuck: gestiscono i due tipi di combattimento;

- Restart: riavvia la partita.

Le UserAction vengono ricevute dal metodo onAction, il quale decide come procedere in base al tipo di azione. Il metodo handleClick gestisce specificamente i clic sulle diverse celle, invocando il metodo onAction con l'azione corrispondente come parametro.

## Strategies e Prolog
### GuardianStrategy
La classe GuardianStrategy contiene il metodo nextMove, che:
1. Crea dinamicamente una query Prolog con i parametri di posizione del guardiano e del giocatore: next_move(gx, gy, px, py, NX, NY).


2. Usa l’engine Prolog passato come parametro per risolvere la query.


3. Se esiste una soluzione, estrae i valori NX e NY dalla struttura Prolog risultante e ritorna un Position(nx, ny).


4. Se non c’è soluzione, il guardiano rimane fermo.


Questa implementazione sfrutta funzioni avanzate di Scala come pattern matching su Option.

### Scala2Prolog
Questo oggetto è destinato a tradurre Scala in Prolog. Usiamo il pattern given Conversion per convertire String e Seq[_] in Term, abbiamo un metodo extractTerm per estrarre un termine da una stringa di stringhe; e abbiamo il metodo fondamentale, chiamato mkPrologEngine, che fa quanto segue:
1. Riceve un numero variabile di stringhe String*, corrispondenti alla teoria Prolog restituita da MazePrologTheory.


2. Crea un oggetto Prolog e vi carica una Theory.


3. Ritorna una funzione che, dato un Term (query), restituisce un LazyList[Term] (evitando overhead eccessivo) con tutte le possibili soluzioni, usando un Iterable personalizzato. Il meccanismo interno gestisce il backtracking di Prolog tramite solve, hasOpenAlternatives e solveNext.



### MazePrologTheory
La classe MazePrologTheory è un object Scala che funge da generatore di teorie Prolog a partire dallo stato corrente del labirinto. Prende come input un oggetto Maze e costruisce una stringa contenente i fatti e le regole Prolog necessarie per determinare percorsi e mosse valide all’interno del labirinto.

L’implementazione si suddivide in due parti principali:

1. Fatti dinamici (walkable(X,Y).):
viene generata una sequenza di fatti walkable(x, y) per ogni cella camminabile nella griglia del labirinto, verificando tramite maze.isWalkable(...). Questi fatti indicano le coordinate accessibili.


2. Regole Prolog:
contiene una serie di regole statiche definite come stringa multilinea:


    neighbor(...) definisce celle adiacenti e camminabili.


    path(...) e bfs(...) implementano una ricerca in ampiezza (BFS) per trovare un percorso tra due celle.


    next_move(...) estrae il primo passo del percorso, utile per muovere i guardiani.


Infine, tutti i fatti e le regole vengono concatenati in un’unica stringa e restituiti al chiamante, che potrà passarla al motore Prolog (usato nella classe GuardianStrategy).

