import SwiftUI

struct PlayersView: View {
    @Environment(AppStore.self) private var store
    @State private var name = ""
    var body: some View {
        List {
            Section("新增玩家") {
                HStack { TextField("名稱", text: $name); Button("加入") { store.addPlayer(name: name); name = "" }.disabled(name.trimmingCharacters(in: .whitespaces).isEmpty) }
            }
            Section("玩家") {
                ForEach(store.data.players) { player in Label(player.name, systemImage: "person.crop.circle") }
                    .onDelete(perform: store.deletePlayers)
            }
        }.navigationTitle("玩家管理")
    }
}

