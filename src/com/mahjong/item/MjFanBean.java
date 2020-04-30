package com.mahjong.item;

import java.util.ArrayList;
import java.util.List;

import com.mahjong.common.MjCard;
import com.mahjong.common.MjCardPairs;
import com.mahjong.common.MjDir;
import com.mahjong.common.MjPairType;

public class MjFanBean {

	private String leftText;
	private String centerText;
	private String rightText;
	private String mjText;

	private List<MjCard> cardList;
	private List<MjCardPairs> pairsList;
	private MjCard winCard;
	private boolean canShowSpectrum;

	public MjFanBean(String left, String center, String right, String mj) {
		this.leftText = left;
		this.centerText = center;
		this.rightText = right;
		this.mjText = mj;
		this.canShowSpectrum = analysisMjString(mj);
	}

	public String getLeftText() {
		return leftText;
	}

	public String getCenterText() {
		return centerText;
	}

	public String getRightText() {
		return rightText;
	}

	public String getMjText() {
		return mjText;
	}

	public List<MjCard> getCardList() {
		return cardList;
	}

	public List<MjCardPairs> getPairsList() {
		return pairsList;
	}

	public MjCard getWinCard() {
		return winCard;
	}
	
	public boolean canShowSpectrum() {
		return canShowSpectrum;
	}

	public boolean analysisMjString(String mjText) {
		if (mjText == null || mjText.isEmpty()) {
			return false;
		}
		if (cardList == null)
			cardList = new ArrayList<MjCard>();
		else
			cardList.clear();
		if (pairsList == null)
			pairsList = new ArrayList<MjCardPairs>();
		else
			pairsList.clear();
		String[] split = mjText.split(",");
		if (split == null || split.length == 0) {
			return false;
		}
		for (int i = 0; i < split.length; i++) {
			String tmpString = split[i];
			if (tmpString != null && tmpString != "") {
				int j = 0;
				char tmpChar = tmpString.charAt(j);
				if (isNumber(tmpChar)) {
					MjCard card = analysisMjCard(tmpString);
					if (card != null) {
						cardList.add(card);
					}
				} else if (isLetter(tmpChar) && tmpChar != 'w') {
					MjCardPairs pairs = analysisMjCardPairs(tmpString);
					if (pairs != null) {
						pairsList.add(pairs);
					}
				} else if (tmpChar == 'w' && isSpliter(tmpString.charAt(++j))) {
					tmpChar = tmpString.charAt(++j);
					if (isNumber(tmpChar)) {
						int num = tmpChar - '0';
						while (++j < tmpString.length() && isNumber(tmpChar = tmpString.charAt(j))) {
							num = num * 10 + (tmpChar - '0');
						}
						if (winCard == null) winCard = new MjCard();
						winCard.setData(num);
					}
				}
			
			}
		}

		return true;
	}
	
	private MjCard analysisMjCard(String tmpString) {
		int index = 0;
		char tmpChar = tmpString.charAt(index);
		if (isNumber(tmpChar)) {
			int num = tmpChar - '0';
			while (isNumber(tmpChar = tmpString.charAt(++index))) {
				num = num * 10 + (tmpChar - '0');
			}
			if (isSpliter(tmpChar)) {
				int dir = tmpString.charAt(++index) == 'v' ? MjDir.vertical
						: MjDir.horizontal;
				return new MjCard(num, dir);
			}
		} 
		return null;
	}

	private MjCardPairs analysisMjCardPairs(String tmpString) {
		int index = 0;
		char tmpChar = tmpString.charAt(index);
		if (isLetter(tmpChar)) {
			MjCardPairs pairs = new MjCardPairs();
			switch (tmpChar) {
			case 't':
				pairs.setType(MjPairType.Triplet);
				break;
			case 's':
				pairs.setType(MjPairType.Sequence);
				break;
			case 'a':
				pairs.setType(MjPairType.AdditionKong);
				break;
			case 'e':
				pairs.setType(MjPairType.ExposedKong);
				break;
			case 'c':
				pairs.setType(MjPairType.ConcealedKong);
				break;
			default:
				pairs.setType(MjPairType.Unknown);
				break;
			}
			if (isSpliter(tmpString.charAt(++index))) {
				tmpChar = tmpString.charAt(++index);
				if (isLetter(tmpChar)) {
					switch (tmpChar) {
					case 'l':
						pairs.setDir(MjDir.left);
						break;
					case 'c':
						pairs.setDir(MjDir.center);
						break;
					case 'r':
						pairs.setDir(MjDir.right);
						break;
					default:
						pairs.setDir(MjDir.unknown);
						break;
					}
					if (tmpString.charAt(++index) == '{' && tmpString.endsWith("}")) {
						String[] split = tmpString
								.substring(index + 1, tmpString.length() - 1)
								.split("-");
						for (int i = 0; i < split.length; i++) {
							if (split[i] != null && split[i] != "") {
								MjCard card = analysisMjCard(split[i]);
								if (card != null) {
									pairs.addCard(card);
								}
							}
						}
					}
				}
			}
			return pairs;
		}
		
		return null;
	}
	
	public static String createMjString(List<MjCard> cardList,
			List<MjCardPairs> pairsList, MjCard winCard) {
		StringBuffer mjText = new StringBuffer();
		if (cardList != null) {
			for (MjCard card : cardList) {
				mjText.append(card.num);
				if (card.dir == MjDir.vertical) {
					mjText.append(":v,");
				} else {
					mjText.append(":h,");
				}
			}
		}
		if (pairsList != null) {
			for (MjCardPairs pairs : pairsList) {
				switch (pairs.getType()) {
				case MjPairType.Triplet:
					mjText.append("t");
					break;
				case MjPairType.Sequence:
					mjText.append("s");
					break;
				case MjPairType.AdditionKong:
					mjText.append("a");
					break;
				case MjPairType.ExposedKong:
					mjText.append("e");
					break;
				case MjPairType.ConcealedKong:
					mjText.append("c");
					break;
				default:
					mjText.append("u");
					break;
				}
				mjText.append(":");
				switch (pairs.getDir()) {
				case MjDir.left:
					mjText.append("l");
					break;
				case MjDir.center:
					mjText.append("c");
					break;
				case MjDir.right:
					mjText.append("r");
					break;
				default:
					mjText.append("u");
					break;
				}
				mjText.append("{");
				for (MjCard card : pairs.mCardList) {
					mjText.append(card.num);
					if (card.dir == MjDir.vertical) {
						mjText.append(":v-");
					} else {
						mjText.append(":h-");
					}
				}
				mjText.append("},");
			}
		}
		if (winCard != null) {
			mjText.append("w:");
			mjText.append(winCard.num);
		}
		return mjText.toString();
	}

	public boolean isNumber(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}

	public boolean isLetter(char c) {
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
			return true;
		}
		return false;
	}

	public boolean isSpliter(char c) {
		if (c == ':') {
			return true;
		}
		return false;
	}
}
