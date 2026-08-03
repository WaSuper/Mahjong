import Foundation

struct RiichiPayment: Equatable {
    let ron: Int?
    let fromDealer: Int?
    let fromNonDealer: Int?
}

enum RiichiScoreCalculator {
    static func calculate(han: Int, fu: Int, dealer: Bool, tsumo: Bool) -> RiichiPayment {
        precondition(han > 0 && fu >= 20)
        let base: Int
        switch han {
        case 13...: base = 8_000
        case 11...12: base = 6_000
        case 8...10: base = 4_000
        case 6...7: base = 3_000
        case 5: base = 2_000
        default:
            let raw = fu * (1 << (han + 2))
            base = min(raw, raw >= 2_000 ? 2_000 : raw)
        }
        func rounded(_ value: Int) -> Int { ((value + 99) / 100) * 100 }
        if !tsumo {
            return .init(ron: rounded(base * (dealer ? 6 : 4)), fromDealer: nil, fromNonDealer: nil)
        }
        if dealer {
            let each = rounded(base * 2)
            return .init(ron: nil, fromDealer: nil, fromNonDealer: each)
        }
        return .init(ron: nil, fromDealer: rounded(base * 2), fromNonDealer: rounded(base))
    }
}

