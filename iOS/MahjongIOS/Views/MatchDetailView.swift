import SwiftUI

struct MatchDetailView: View {
    @Environment(AppStore.self) private var store
    let matchID: UUID
    @State private var showRound = false
    var body: some View {
        Group {
            if let match = store.match(matchID) {
                List {
                    Section("目前點數") { ForEach(match.playerIDs, id: \.self) { id in HStack { Text(store.player(id)?.name ?? "未知玩家"); Spacer(); Text(match.score(for: id).formatted()) } } }
                    Section("局紀錄") {
                        ForEach(match.rounds) { round in VStack(alignment: .leading) { Text("\(round.wind.rawValue.capitalized) \(round.hand)局"); Text(round.note.isEmpty ? "手動調整" : round.note).font(.caption).foregroundStyle(.secondary) } }
                    }
                }
                .toolbar {
                    if match.status == .active {
                        Button("記錄一局") { showRound = true }
                        Button("結束") { store.finishMatch(matchID) }
                    }
                }
                .sheet(isPresented: $showRound) { RoundEntryView(matchID: matchID, playerIDs: match.playerIDs) }
            } else { ContentUnavailableView("找不到對局", systemImage: "exclamationmark.triangle") }
        }.navigationTitle("四人對局")
    }
}

private struct RoundEntryView: View {
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    let matchID: UUID
    let playerIDs: [UUID]
    @State private var values: [UUID: Int] = [:]
    @State private var note = ""
    var sum: Int { values.values.reduce(0, +) }
    var body: some View {
        NavigationStack {
            Form {
                ForEach(playerIDs, id: \.self) { id in Stepper("\(store.player(id)?.name ?? "玩家"): \(values[id, default: 0])", value: Binding(get: { values[id, default: 0] }, set: { values[id] = $0 }), in: -100_000...100_000, step: 100) }
                TextField("備註（和了、流局等）", text: $note)
                LabeledContent("合計", value: sum.formatted()).foregroundStyle(sum == 0 ? .primary : .red)
            }
            .navigationTitle("記錄點數變動")
            .toolbar { Button("儲存") { if store.addRound(to: matchID, changes: values, note: note) { dismiss() } }.disabled(sum != 0) }
        }
    }
}

