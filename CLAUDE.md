# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

chess4j is an XBoard/WinBoard-compatible Java chess engine. It communicates with a GUI over stdin/stdout using the
XBoard protocol (`XBoardHandler`), or can be run in several standalone batch modes (search test suites, book
building, FEN labeling, eval tuning).

## Build & test

Requires JDK 24+ and Maven.

```
mvn clean install                 # build; runs tests; produces target/chess4j-<version>-uber.jar
mvn test                          # run the test suite only
mvn test -Dtest=ClassName         # run a single test class
mvn test -Dtest=ClassName#method  # run a single test method
```

Tests use JUnit 4 (`org.junit.Test`), not JUnit 5/Jupiter. Mockito is available for mocking.

Surefire excludes `**/Perf*`, `**/SQLiteBook*`, and `**/LogisticRegressionTuner*` from the default `mvn test` run
(they are slow/long-running or require external resources) — run these explicitly with `-Dtest=...` when needed.

Smoke-test a built jar against a search test suite:

```
java -jar target/chess4j-<version>-uber.jar -mode test -epd src/test/resources/suites/wac2.epd
```

Main entry point / CLI flags are defined in `App.java` (`-mode normal|bookbuild|label|test|tune`, plus `-depth`,
`-nodes`, `-time`, `-hash`, `-phash`, `-book`, `-eval`, `-nn`, `-native`, etc.).

## Architecture

### Core flow

`App.main` parses CLI options, applies them to process-wide state in `Globals` (board, opening book, eval weights,
neural network), then either runs one of the batch modes or enters `repl()` — a read-eval-print loop that feeds each
line of stdin to `XBoardHandler.parseAndDispatch`. `XBoardHandler` (`io/`) implements the XBoard/WinBoard protocol:
it owns game state transitions and drives the search via `SearchIterator`.

### Search

- `SearchIterator` / `SearchIteratorImpl` (`search/`) implement iterative deepening with aspiration windows
  (triggered starting after depth 1), time/node limits, and async search via `CompletableFuture`
  (`findPvFuture`).
- `Search` / `AlphaBetaSearch` implement the actual alpha-beta search (PVS-style) called at each depth.
- Move ordering: `MoveOrderer`, `MVVLVA` (captures), `KillerMoves`/`KillerMovesStore`, `SEE` (static exchange
  evaluation), `Prune`.
- Transposition tables: `hash/` — `TranspositionTable` (main) and `PawnTranspositionTable` (pawn structure cache),
  both managed through `TTHolder` (singleton, resizable via `-hash`/`-phash`). `Zobrist` provides hash keys.

### Board representation & move generation

- `board/` — `Board` is the core mutable position representation (bitboard-based, see `Bitboard`), with
  `Undo`/make-move/unmake-move semantics used heavily by search. `board/squares/` defines square/file/rank/direction
  types.
- `movegen/` — `MagicBitboardMoveGenerator` (magic bitboards for sliding pieces via `Magic`), `AttackDetector`,
  `Mobility`.
- `pieces/` — per-piece-type logic (`Pawn`, `Knight`, `Bishop`, `Rook`, `Queen`, `King`) plus helper utils
  (`BishopUtils`, `KnightUtils`, `PawnUtils`).

### Evaluation

Two evaluation paths, selectable at runtime, both wired through `Globals`:

- **Handcrafted evaluation** (`eval/`) — the default. `Eval` combines material (`EvalMaterial`), piece-square/
  positional terms per piece type (`EvalPawn`, `EvalKnight`, `EvalBishop`, `EvalRook`, `EvalQueen`, `EvalKing`,
  `EvalMajorOn7th`), and tapered eval between midgame/endgame (`EvalTaper`). Tunable weights live in `EvalWeights`
  (loadable/storable via `EvalWeightsUtil`, `-eval <propsFile>`).
- **Neural network evaluation** (`nn/`) — opt-in via `-nn <weightsFile>`. `NeuralNetwork` is a small NNUE-style
  net (768 inputs → `NN_SIZE_L1` → `NN_SIZE_L2`) with `NnueAccumulators` for incremental updates during
  make/unmake. `FENLabeler` generates labeled training data.
- `tuner/` (`LogisticRegressionTuner`, `Gradient`, `CostFunction`, `Hypothesis`, `MatrixUtils`) tunes handcrafted
  eval weights against labeled FEN data via `-mode tune`.

### Native engine (FFM)

`nativelib/` (`NativeLibraryLoader`, `NativeEngineLib`) optionally loads a native library (`libprophetlib.so`, the
"Prophet" engine) via the Java Foreign Function & Memory API, enabled with `-native`. This replaced an older JNI
integration; the native engine's source is not part of this repo. Several core classes (`Eval`, `NeuralNetwork`,
`SearchIteratorImpl`) call `NativeLibraryLoader.init()` in a static block so native calls can be dispatched when
`-native` is set, but the engine runs fully in pure Java otherwise.

### I/O and data formats

`io/` handles all external formats: `MoveParser`/`DrawBoard`/`FENBuilder` (FEN), `EPDParser`/`EPDOperation` (EPD test
suites), `PGNGameParser`/`PGNIterator`/`PGNMoveTextTokenizer` (PGN), `CutechessNagParser`, `FENCSVUtils`/`FENRecord`
(CSV label files for tuning), and `XBoardHandler` (protocol). `utils/TestSuiteProcessor` drives `-mode test` (EPD
suite solving) and `utils/Perft` provides move-generator perft testing.

### Opening book

`book/` — `OpeningBook` interface, `SQLiteBook` (SQLite-backed, built via `-mode bookbuild` from a PGN file) and
`InMemoryBook`.

## Notes

- `Globals` holds process-wide mutable engine state (current game board/undo history, active eval weights, opening
  book, neural network). It's a static singleton-style holder, not a DI container — most engine code reads from it
  directly.
- Search correctness is validated with `assert` statements gated behind assertions being enabled (see
  `Eval.eval`'s `assert(verify(...))` and `App.showDebugMode()`), so behavior can differ with `-ea` on/off.
