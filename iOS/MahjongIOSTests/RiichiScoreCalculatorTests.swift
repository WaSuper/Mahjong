import XCTest
@testable import MahjongIOS

final class RiichiScoreCalculatorTests: XCTestCase {
    func testNonDealerThirtyFuOneHanRon() { XCTAssertEqual(RiichiScoreCalculator.calculate(han: 1, fu: 30, dealer: false, tsumo: false).ron, 1_000) }
    func testDealerThirtyFuThreeHanRon() { XCTAssertEqual(RiichiScoreCalculator.calculate(han: 3, fu: 30, dealer: true, tsumo: false).ron, 5_800) }
    func testNonDealerManganTsumo() {
        let value = RiichiScoreCalculator.calculate(han: 5, fu: 30, dealer: false, tsumo: true)
        XCTAssertEqual(value.fromDealer, 4_000); XCTAssertEqual(value.fromNonDealer, 2_000)
    }
    func testDealerYakumanRon() { XCTAssertEqual(RiichiScoreCalculator.calculate(han: 13, fu: 30, dealer: true, tsumo: false).ron, 48_000) }
}

