---
layout: default
title: Testing
---

Per tornare alla [home](index.md)

# Testing

## Tecnologie utilizzate

Il sistema è stato testato utilizzando JUnit come framework principale. Quest'ultimo è pienamente compatibile con sbt e
ha consentito lo sviluppo di test unitari completi e di integrazione non particolarmente approfonditi.

---

## Metodologia seguita

È stato adottato, in linea di massima, un approccio **TDD (Test-Driven Development)** durante lo sviluppo dei principali
componenti.
- La definizione di un modulo è stato accompagnato dalla scrittura di test che ne riflettano l'implementazione;
- I test hanno servito come specifica del comportamento atteso;
- L’approccio ha favorito maggiore stabilità, sicurezza nella rifattorizzazione e chiarezza nel design.

---

## Esempi di codice

**Esempio di JUnit test** nel file `test/scala/model/entities/PLayer.scala`

```scala
@Test
def testUseItemNotAvailable(): Unit =
  val player = Player(Position(0, 0), 1, 0)
  val item = InvisibilityPotion()
  player.useItem(item) match
    case Left(PlayerError.ItemNotFound(name)) =>
      assertEquals(item.name, name, "Error must report missing item")
    case Left(other) =>
      fail(s"Expected ItemNotFound error but got: $other")
    case Right(_) =>
      fail("Expected useItem to fail when item is missing")
```
