## GameView

GameView rappresenta la view principale del gioco, e implementa il trait View tramite una classe Java.
All’interno della view:

- Ogni cella del labirinto è rappresentata da un JButton, organizzato in una JPanel con GridLayout;


- I bottoni sono memorizzati in una mappa Map<Pair, JButton> per facilitare l’accesso e aggiornamento;


- I colori e testi dei bottoni vengono aggiornati dinamicamente in updateView() in base al contenuto della cella (giocatore, guardian, porta, muro);


Il collegamento tra i pulsanti del Maze e il Controller è realizzato tramite il pattern Observer, sfruttando il EventBus per notificare gli eventi (CellClickEvent). Ogni click su una cella viene pubblicato sull'EventBus, a cui è sottoscritto il Controller. Questo design rende GameView completamente disaccoppiata dal controller.

![UML Diagram GameView](Images/MazeEscape%20-%20GameView.png)

### MenuView

La classe MenuView rappresenta la schermata iniziale del gioco, responsabile della visualizzazione del menù principale, dove viene scelto il livello di difficoltà del gioco. Estende JFrame.

Questa classe è pensata per separare la logica di presentazione (View) dal resto della logica del gioco, secondo il pattern MVC. La struttura è semplice e facilmente estendibile per aggiungere opzioni future.

![UML Diagram MenuView](Images/MazeEscape%20-%20MenuView.png)

## Maze

Il pacchetto model.map modella la mappa del gioco "Maze Escape". L’elemento centrale è la classe Maze, che rappresenta la griglia bidimensionale del labirinto ed espone operazioni per accedere e modificare la mappa. L’organizzazione segue il principio di separazione delle responsabilità:

- Maze è la classe principale che gestisce lo stato e la logica della mappa.


- Cell è un sealed trait che definisce un’interfaccia comune per le celle. Le sue sottoclassi concrete sono WallCell, FloorCell, DoorCell, che rappresentano i tre tipi di celle del labirinto. La classe DoorCell dispone anche di metodi per modellare apertura, blocco e gestione del tempo di blocco delle porte. 	


- MazeUtils è un oggetto helper che fornisce operazioni per manipolare la griglia.


- Il companion object Maze gestisce la generazione del labirinto e delle porte tramite l’algoritmo di carving. Utilizza funzioni ausiliarie come isInBounds e carve per modellare il percorso.


Le extension methods su Maze (come unlockDoorAt, blockDoorAt, decreaseTurnsLockedDoors) estendono il comportamento della classe senza modificarne direttamente la definizione, in linea con la filosofia Scala.

![UML Diagram Maze](Images/MazeEscape%20-%20Maze.png)