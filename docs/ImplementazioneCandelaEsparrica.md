## GameView
GameView è l’implementazione concreta del trait View, ed è responsabile dell’interfaccia grafica del gioco. Estende JFrame ed è scritta in Java, pur interagendo fortemente con elementi scritti in Scala.

Durante l’inizializzazione, crea una griglia di pulsanti (JButton) per ogni cella del labirinto (Maze). Ogni pulsante è associato a una posizione e pubblica un evento CellClickEvent sul EventBus quando viene premuto.

Contiene inoltre due etichette (JLabel) nella parte superiore per mostrare le vite e il punteggio del giocatore. Il metodo updateView legge lo stato corrente del modello e aggiorna i colori, simboli e abilità dei pulsanti in base ai guardiani, al giocatore, e al tipo di cella.

Include anche metodi per:

- showPuzzle: mostra una finestra con un puzzle;


- showFightChoice: mostra una finestra con la scelta tra logica e fortuna;


- showEndGameMenu: mostra il menu di fine gioco;


- showMessage: mostra un messaggio all’utente.

Le funzioni della vista ricevono come parametri funzioni lambda (Function1<String, BoxedUnit> answer), funzioni che vengono definite e valutate dal Controller, rispettando così l'assegnazione delle responsabilità.

### MenuView

La classe inizializza un'interfaccia grafica con un layout verticale (BoxLayout), inserendo tre pulsanti principali: “Easy”, “Normal” e “Hard”. A ciascun pulsante viene associato un ActionListener che invoca funzioni passate come callback, permettendo al main di reagire alle scelte dell’utente.

Le callback sono passate come parametri al costruttore.

Il metodo initComponents() gestisce la creazione dei componenti, mentre setVisible(true) viene chiamato alla fine del costruttore per mostrare la finestra.

## Maze
La classe Maze contiene una griglia di celle (Vector[Vector[Cell]]) e fornisce metodi per estrarre informazioni e modificarla:

- getCell usa MazeUtils.getCell, mantenendo la logica di lettura separata.


- cellsOfType[A <: Cell] usa ClassTag per ottenere in modo generico tutte le celle di un certo tipo.


- Metodi come doorCells, wallCells, floorCells, isWalkable, isDoor, ecc., permettono di interrogare lo stato della mappa.


- randomFloorCell sceglie una cella a caso tra quelle di tipo FloorCell, utilizzata per stabilire la posizione del giocatore.


- spawnGuardians è una funzione che utilizza using: usa la mappa come parametro implicito per selezionare celle libere in cui posizionare guardiani.


### Maze (companion object)
È responsabile della generazione del labirinto. 

Il metodo generate(size) costruisce una mappa inizialmente piena di WallCell e ne scava i percorsi ricorsivamente con l’algoritmo di recursive backtracking, garantendo un labirinto valido e connesso.

Viene sempre inserita una DoorCell di uscita nella parte inferiore destra, legata a un puzzle preso da PuzzleRepository.


Le Extension methods definiscono operazioni immutabili su oggetti Maze:
updateDoorAt(position)(function) aggiorna una DoorCell applicando una funzione f, e restituendo un nuovo Maze. Le methodi unlockDoorAt, blockDoorAt, decreaseTurnsLockedDoors utilizzano updateDoorAt inviando diverse funzioni lambda corrispondenti ai loro target.

## Cell
Il sealed trait Cell definisce l’interfaccia comune. Le sue implementazioni sono semplici case class immutabili: WallCell, FloorCell e  DoorCell. Questa ultima aggiunge stato (aperta, bloccata, puzzle), con metodi per gestire il blocco, l’apertura, decremento dei turni, ecc.
