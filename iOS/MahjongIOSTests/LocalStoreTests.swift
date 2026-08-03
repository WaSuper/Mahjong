import XCTest
@testable import MahjongIOS

final class LocalStoreTests: XCTestCase {
    func testRoundTrip() async throws {
        let url = FileManager.default.temporaryDirectory.appending(path: UUID().uuidString)
        let store = LocalStore(fileURL: url)
        let value = AppData(players: [Player(name: "東")])
        try await store.save(value)
        XCTAssertEqual(try await store.load(), value)
        try? FileManager.default.removeItem(at: url)
    }
}
