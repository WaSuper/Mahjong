# Mahjong iOS MVP

This directory contains a native SwiftUI port that coexists with the original Android project.

## Requirements

- Xcode 16 or newer
- iOS / iPadOS 17 or newer

Open `MahjongIOS.xcodeproj`, select the `MahjongIOS` scheme, and run on an iPhone or iPad simulator. No network connection or third-party package is required.

## MVP scope

- Four-player match creation with 25,000 starting points
- Player creation, rename, and deletion
- Manual round result entry with zero-sum validation
- Japanese mahjong han/fu payment calculator for ron and tsumo
- Match history and per-match round details
- JSON-backed offline persistence in Application Support
- Adaptive split-view navigation on iPad and tab navigation on iPhone

The Android source and assets remain unchanged. Character art, sound packs, and backgrounds have not been copied because their third-party provenance needs review.

## Test

In Xcode, choose **Product > Test**. Unit tests cover common ron/tsumo values, limit hands, score conservation, and persistence round-trips.

