import Foundation
import Observation

@MainActor @Observable
final class AppStore {
    private(set) var data = AppData()
    private(set) var errorMessage: String?
    private let repository: any AppDataRepository

    init(repository: any AppDataRepository) { self.repository = repository }

    func load() async {
        do { data = try await repository.load() }
        catch { errorMessage = error.localizedDescription }
    }

    func addPlayer(name: String) {
        let trimmed = name.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        data.players.append(Player(name: trimmed)); persist()
    }

    func deletePlayers(at offsets: IndexSet) {
        let inUse = Set(data.matches.flatMap(\.playerIDs))
        for index in offsets.sorted(by: >) where !inUse.contains(data.players[index].id) { data.players.remove(at: index) }
        persist()
    }

    func startMatch(playerIDs: [UUID]) -> UUID? {
        guard playerIDs.count == 4, Set(playerIDs).count == 4 else { return nil }
        let match = MatchRecord(playerIDs: playerIDs)
        data.matches.insert(match, at: 0); persist(); return match.id
    }

    func addRound(to matchID: UUID, changes: [UUID: Int], note: String) -> Bool {
        guard changes.values.reduce(0, +) == 0,
              let index = data.matches.firstIndex(where: { $0.id == matchID }) else { return false }
        let count = data.matches[index].rounds.count
        data.matches[index].rounds.append(RoundRecord(wind: count < 4 ? .east : .south, hand: count % 4 + 1, pointChanges: changes, note: note))
        persist(); return true
    }

    func finishMatch(_ id: UUID) {
        guard let index = data.matches.firstIndex(where: { $0.id == id }) else { return }
        data.matches[index].status = .completed; data.matches[index].endedAt = Date(); persist()
    }

    func player(_ id: UUID) -> Player? { data.players.first { $0.id == id } }
    func match(_ id: UUID) -> MatchRecord? { data.matches.first { $0.id == id } }

    private func persist() {
        let snapshot = data
        Task { do { try await repository.save(snapshot) } catch { errorMessage = error.localizedDescription } }
    }
}
