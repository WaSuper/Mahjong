package com.mahjong.tools;

import java.util.List;

import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;

public class CalculateJpnUtil {

	public enum CalcuResult {
		OK,				// 正确
		Error_Blank,	// 有空牌
		Error_NoMatch,	// 无听牌
		Error_NoFan,	// 无番
	}
	
	public static CalcuResult calculate(List<MjCard> cardList, List<MjCardPairs> pairsList, MjCard winCard) {
		if (checkHasBlank(cardList, pairsList, winCard)) {
			return CalcuResult.Error_Blank;
		}
		
		
		return CalcuResult.OK;
	}
	
	
	public static boolean checkHasBlank(List<MjCard> cardList, List<MjCardPairs> pairsList, MjCard winCard) {
		if (cardList != null && cardList.size() > 0) {
			for (MjCard card : cardList) {
				if (card.isBlank()) {
					return true;
				}
			}
		}
		if (pairsList != null && pairsList.size() > 0) {
			for (MjCardPairs pairs : pairsList) {
				if (pairs.mCardList != null && pairs.mCardList.size() > 0) {
					for (MjCard card : pairs.mCardList) {
						if (card.isBlank()) {
							return true;
						}
					}
				}
			}
		}
		if (winCard != null && winCard.isBlank()) {
			return true;
		}
		
		return false;
	}
	
}
