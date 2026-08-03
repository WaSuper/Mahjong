import SwiftUI

struct CalculatorView: View {
    @State private var han = 1
    @State private var fu = 30
    @State private var dealer = false
    @State private var tsumo = false
    var payment: RiichiPayment { RiichiScoreCalculator.calculate(han: han, fu: fu, dealer: dealer, tsumo: tsumo) }
    var body: some View {
        Form {
            Section("和牌條件") {
                Stepper("翻數：\(han)", value: $han, in: 1...13)
                Picker("符數", selection: $fu) { ForEach([20, 25, 30, 40, 50, 60, 70, 80, 90, 100, 110], id: \.self) { Text("\($0)符").tag($0) } }
                Toggle("莊家", isOn: $dealer); Toggle("自摸", isOn: $tsumo)
            }
            Section("支付") {
                if let ron = payment.ron { LabeledContent("榮和", value: ron.formatted()) }
                if let dealerPay = payment.fromDealer { LabeledContent("莊家支付", value: dealerPay.formatted()) }
                if let nonDealer = payment.fromNonDealer { LabeledContent(dealer ? "每位閒家支付" : "每位閒家支付", value: nonDealer.formatted()) }
            }
        }.navigationTitle("日麻計分器")
    }
}

