import SwiftUI

struct HistoryView: View {
    @Environment(AppStore.self) private var store
    var body: some View {
        List(store.data.matches) { match in NavigationLink(value: match.id) { MatchRow(match: match) } }
            .overlay { if store.data.matches.isEmpty { ContentUnavailableView("尚無對局紀錄", systemImage: "clock") } }
            .navigationTitle("歷史戰績")
            .navigationDestination(for: UUID.self) { MatchDetailView(matchID: $0) }
    }
}

