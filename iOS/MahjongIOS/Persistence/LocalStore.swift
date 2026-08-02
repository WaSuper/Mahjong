import Foundation

protocol AppDataRepository: Sendable {
    func load() async throws -> AppData
    func save(_ data: AppData) async throws
}

actor LocalStore: AppDataRepository {
    private let fileURL: URL

    init(fileURL: URL? = nil) {
        if let fileURL { self.fileURL = fileURL; return }
        let base = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
        self.fileURL = base.appending(path: "MahjongIOS/data.json")
    }

    func load() throws -> AppData {
        guard FileManager.default.fileExists(atPath: fileURL.path) else { return AppData() }
        return try JSONDecoder().decode(AppData.self, from: Data(contentsOf: fileURL))
    }

    func save(_ data: AppData) throws {
        try FileManager.default.createDirectory(at: fileURL.deletingLastPathComponent(), withIntermediateDirectories: true)
        let encoded = try JSONEncoder().encode(data)
        try encoded.write(to: fileURL, options: .atomic)
    }
}

