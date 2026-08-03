import Foundation

struct Player: Identifiable, Codable, Hashable {
    var id = UUID()
    var name: String
    var createdAt = Date()
}

enum Wind: String, Codable, CaseIterable { case east, south, west, north }

struct RoundRecord: Identifiable, Codable, Hashable {
    var id = UUID()
    var wind: Wind
    var hand: Int
    var pointChanges: [UUID: Int]
    var note: String
    var createdAt = Date()
}

struct MatchRecord: Identifiable, Codable, Hashable {
    enum Status: String, Codable { case active, completed }
    var id = UUID()
    var playerIDs: [UUID]
    var startingPoints = 25_000
    var rounds: [RoundRecord] = []
    var status: Status = .active
    var startedAt = Date()
    var endedAt: Date?

    func score(for playerID: UUID) -> Int {
        startingPoints + rounds.reduce(0) { $0 + ($1.pointChanges[playerID] ?? 0) }
    }
}

struct AppData: Codable, Equatable {
    var players: [Player] = []
    var matches: [MatchRecord] = []
}

