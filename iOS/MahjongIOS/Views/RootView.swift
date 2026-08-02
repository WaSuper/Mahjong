import SwiftUI

struct RootView: View {
    @Environment(AppStore.self) private var store

    var body: some View {
        TabView {
            NavigationStack { DashboardView() }.tabItem { Label("對局", systemImage: "square.grid.2x2") }
            NavigationStack { PlayersView() }.tabItem { Label("玩家", systemImage: "person.3") }
            NavigationStack { HistoryView() }.tabItem { Label("歷史", systemImage: "clock.arrow.circlepath") }
            NavigationStack { CalculatorView() }.tabItem { Label("計分", systemImage: "function") }
        }
        .alert("儲存失敗", isPresented: .constant(store.errorMessage != nil)) { Button("好") {} } message: { Text(store.errorMessage ?? "") }
    }
}

private struct DashboardView: View {
    @Environment(AppStore.self) private var store
    @State private var showNewMatch = false
    var active: [MatchRecord] { store.data.matches.filter { $0.status == .active } }

    var body: some View {
        List {
            Section {
                Button { showNewMatch = true } label: { Label("建立四人對局", systemImage: "plus.circle.fill") }
                    .disabled(store.data.players.count < 4)
                if store.data.players.count < 4 { Text("請先建立至少四位玩家").foregroundStyle(.secondary) }
            }
            Section("進行中") {
                if active.isEmpty { ContentUnavailableView("沒有進行中的對局", systemImage: "tablecells") }
                ForEach(active) { match in NavigationLink(value: match.id) { MatchRow(match: match) } }
            }
        }
        .navigationTitle("日麻計分")
        .navigationDestination(for: UUID.self) { MatchDetailView(matchID: $0) }
        .sheet(isPresented: $showNewMatch) { NavigationStack { NewMatchView() } }
    }
}

struct MatchRow: View {
    @Environment(AppStore.self) private var store
    let match: MatchRecord
    var body: some View {
        VStack(alignment: .leading, spacing: 5) {
            Text(match.playerIDs.compactMap { store.player($0)?.name }.joined(separator: " · ")).font(.headline)
            Text("\(match.rounds.count) 局 · \(match.startedAt.formatted(date: .abbreviated, time: .shortened))").font(.caption).foregroundStyle(.secondary)
        }
    }
}

private struct NewMatchView: View {
    @Environment(AppStore.self) private var store
    @Environment(\.dismiss) private var dismiss
    @State private var selection = Set<UUID>()
    var body: some View {
        List(store.data.players) { player in
            Button { selection.formSymmetricDifference([player.id]) } label: {
                HStack { Text(player.name); Spacer(); if selection.contains(player.id) { Image(systemName: "checkmark.circle.fill") } }
            }.disabled(selection.count == 4 && !selection.contains(player.id))
        }
        .navigationTitle("選擇四位玩家")
        .toolbar { ToolbarItem(placement: .confirmationAction) { Button("開始") { if store.startMatch(playerIDs: Array(selection)) != nil { dismiss() } }.disabled(selection.count != 4) } }
    }
}

