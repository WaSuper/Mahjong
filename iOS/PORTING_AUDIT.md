# Android-to-iOS porting audit

## Repository

The retained Android application is an Eclipse/Ant-style Java project (`AndroidManifest.xml`, `src`, `res`, `assets`, `libs`) rather than Gradle or a cross-platform framework. Activities and custom Views implement the UI. The major feature areas are four-player, three-player, and 17-step games, history, ranking, players, calculator, emoticons, and sound boxes.

## License

The repository root contains Eclipse Public License 2.0. The iOS port remains in the same repository with the license intact. Distribution must continue to satisfy EPL-2.0 source-availability and notice requirements. This is an engineering note, not legal advice.

The repository also contains anime character portraits, backgrounds, emoticons, and audio-oriented features without a complete per-asset provenance manifest. None of those assets are included in the MVP target. Their rights must be established before App Store distribution.

## Core Android architecture

- `src/com/mahjong/control`: `BaseManager` plus `Game4pManager`, `Game3pManager`, and `Game17sManager`; match state, round transitions, point changes, placement, and uma.
- `src/com/mahjong/model`: mutable Java records including `Player`, `MjAction`, `MjDetail`, `MjResult`, and ranking/audio models.
- `src/com/mahjong/item`: scoring and result value objects.
- `src/com/mahjong/tools/CalculateJpnUtil.java`: Japanese point calculation helpers.
- `src/com/mahjong/data` and `SqliteTool`: local data access objects and SQLite helpers for players, matches, details, and auxiliary data.
- `src/com/mahjong/activity`, `fragment`, `dialog`, and `ui`: Android-specific presentation and custom table-oriented controls.

The Android model mixes persistence, mutable match state, and presentation conventions. The Swift port separates value models, a scoring service, a file repository, and SwiftUI screens.

## MVP mapping

| Android concept | iOS MVP |
| --- | --- |
| `Player` | Codable `Player` value model |
| `MjResult` / `MjDetail` | `MatchRecord` / `RoundRecord` |
| `CalculateJpnUtil` | Pure `RiichiScoreCalculator` |
| SQLite helpers | Actor-isolated atomic JSON `LocalStore` |
| Four-player activities | `NewMatchView` and `MatchDetailView` |
| Player activity | `PlayersView` |
| History activity | `HistoryView` |

## Deferred work and risks

- Full yaku detection, tile parsing, exhaustive fu derivation, pao, abortive draws, chombo, multiple ron, bankruptcy, west-round continuation, configurable oka/uma, and detailed dealer/honba transitions.
- Three-player and 17-step modes, ranks/statistics, import of the legacy SQLite database, sound boxes, emoticons, and custom character art.
- Exact behavioral parity needs golden fixtures captured from the Android app. The old project cannot be built in this Windows workspace without its historical Android SDK/toolchain.
- Xcode compilation and simulator UI verification require macOS. The project structure and Swift source can be reviewed here, but the final Apple toolchain check remains required.
