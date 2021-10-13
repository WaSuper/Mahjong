package com.mahjong.common;

public class MjFanType {

	/************************************* 一番 *************************************/
	//public static final int LiZhi = 1;	
	public static final String[] LiZhiText = {"立直", "门前清状态听牌即可立直", "门前清",
		"1:v,2:v,3:v,14:v,14:v,14:v,25:v,26:v,27:v,31:v,31:v,31:v,32:v,w:32"};
	
	//public static final int YiFa = 2;
	public static final String[] YiFaText = {"一发", "立直后，无人鸣牌的状态下一巡内和牌(不能是岭上开花,抢杠时杠牌不成立但不会破坏一发)", "",
		"1:v,2:v,2:v,14:v,14:v,14:v,25:v,26:v,27:v,31:v,31:v,31:v,32:v,w:32"};
	
	//public static final int ZiMo = 3;
	public static final String[] ZiMoText = {"门前清自摸", "门前清状态下自摸和牌", "门前清",
		"1:v,1:v,1:v,2:v,3:v,4:v,13:v,13:v,13:v,27:v,28:v,29:v,34:v,w:34"};
	
	//public static final int PingHe = 4;
	public static final String[] PingHeText = {"平和", "三组顺子+非役牌的雀头+一组两面听的顺子", "门前清",
		"1:v,2:v,3:v,5:v,6:v,7:v,12:v,13:v,14:v,26:v,27:v,29:v,29:v,w:25"};
	
	//public static final int QiangKong = 5;
	public static final String[] QiangKongText = {"抢杠", "别家加杠的时候荣和(国士无双可以抢暗杠)", "",
		"11:v,11:v,11:v,w:11"};
	
	//public static final int YiPaiSelf = 6;
	public static final String[] YiPaiSelfText = {"役牌：自风牌", "包含自风刻子或杠子", "",
		"3:v,3:v,3:v,14:v,15:v,16:v,27:v,28:v,29:v,t:u{33:v-33:v-33:v},21:v,w:21"};
	
	//public static final int YiPaiGround = 7;
	public static final String[] YiPaiGroundText = {"役牌：场风牌", "包含场风刻子或杠子", "",
		"3:v,3:v,3:v,14:v,15:v,16:v,27:v,28:v,29:v,t:u{31:v-31:v-31:v},21:v,w:21"};
	
	//public static final int YiPaiSanYuan = 8;
	public static final String[] YiPaiSanYuanText = {"役牌：三元牌", "包含中、白、发的刻子或杠子", "",
		"3:v,3:v,3:v,14:v,15:v,16:v,27:v,28:v,29:v,t:u{35:v-35:v-35:v},21:v,w:21"};
	
	//public static final int DuanYaoJiu = 9;
	public static final String[] DuanYaoJiuText = {"断幺九", "手牌中不含幺九牌(19万,19筒,19索,字牌)", "",
		"2:v,3:v,4:v,5:v,6:v,7:v,13:v,13:v,13:v,24:v,25:v,26:v,28:v,w:28"};
	
	//public static final int YiBeiKou = 10;
	public static final String[] YiBeiKouText = {"一杯口", "2组完全相同的顺子", "门前清",
		"1:v,1:v,2:v,2:v,3:v,3:v,14:v,14:v,14:v,17:v,18:v,19:v,23:v,w:23"};
	
	//public static final int LingShangKaiHua = 11;
	public static final String[] LingShangKaiHuaText = {"岭上开花", "用摸到的岭上牌和牌", "",
		"2:v,3:v,4:v,15:v,15:v,15:v,23:v,24:v,25:v,26:v,e:u{28:v-28:v-28:v-28:v},w:23"};

	//public static final int HaiDiLaoYue = 12;
	public static final String[] HaiDiLaoYueText = {"海底捞月", "最后一张牌自摸和牌", "",
		"2:v,2:v,2:v,14:v,15:v,16:v,23:v,23:v,23:v,29:v,29:v,29:v,31:v,w:31"};

	//public static final int HeDiMoYu = 13;
	public static final String[] HeDiMoYuText = {"河底摸鱼", "最后一张牌荣和", "",
		"2:v,2:v,2:v,14:v,15:v,16:v,23:v,23:v,23:v,29:v,29:v,29:v,31:v,w:31"};
	
	//public static final int Dora = 14;
	public static final String[] DoraText = {"宝牌", "宝牌指示牌的下一张牌", "不是役",
		"23:v,0:v,0:v,0:v,0:v,w:24"};

	//public static final int DoraIn = 15;
	public static final String[] DoraInText = {"里宝牌", "立直和牌后所有宝牌下面牌的下一张牌", "不是役",
		"23:v,0:v,0:v,0:v,0:v,t:u{24:v},e:u{11:v-0:v-0:v-0:v-0:v},w:12"};

	//public static final int DoraRed = 16;
	public static final String[] DoraRedText = {"赤宝牌", "红5万，红5筒，红5索", "不是役",
		"10:v,t:u{20:v},w:30"};
	
	/************************************* 二番 *************************************/
	//public static final int DoubleLiZhi = 21;	
	public static final String[] DoubleLiZhiText = {"双立直", "轮到自己之前无人鸣牌下第一巡立直", "门前清",
		"1:v,2:v,3:v,14:v,14:v,14:v,25:v,26:v,27:v,35:v,35:v,35:v,36:v,w:36"};
	
	//public static final int QiDuiZi = 22;	
	public static final String[] QiDuiZiText = {"七对子", "7组不同的对子（不再计算一杯口）", "门前清",
		"1:v,1:v,2:v,2:v,13:v,13:v,14:v,14:v,16:v,16:v,27:v,27:v,34:v,w:34"};
	
	//public static final int HunQuanDaiYao = 23;	
	public static final String[] HunQuanDaiYaoText = {"混全带幺", "所有的顺子、刻子、杠子、对子都含有幺九牌(必须有顺子和字牌)", "副露减1",
		"1:v,2:v,3:v,7:v,8:v,9:v,11:v,12:v,13:v,31:v,31:v,31:v,36:v,w:36"};
	
	//public static final int YiQiTongGuan = 24;	
	public static final String[] YiQiTongGuanText = {"一气通贯", "同种数牌组成123,345,789的顺子", "副露减1",
		"1:v,2:v,3:v,4:v,5:v,6:v,7:v,8:v,9:v,11:v,11:v,11:v,31:v,w:31"};
	
	//public static final int SanSeTongShun = 25;	
	public static final String[] SanSeTongShunText = {"三色同顺", "万、筒、索都有相同数字的顺子", "副露减1",
		"1:v,2:v,3:v,11:v,12:v,13:v,21:v,22:v,23:v,26:v,26:v,26:v,34:v,w:34"};
	
	//public static final int SanSeTongKe = 26;	
	public static final String[] SanSeTongKeText = {"三色同刻", "万、筒、索都有相同数字的刻子或杠子", "",
		"3:v,3:v,3:v,13:v,13:v,13:v,23:v,23:v,23:v,25:v,26:v,27:v,37:v,w:37"};

	//public static final int SanGangZi = 27;	
	public static final String[] SanGangZiText = {"三杠子", "一人开杠三次", "",
		"25:v,26:v,27:v,37:v,e:u{3:v-3:v-3:v-3:v},e:u{13:v-13:v-13:v-13:v},e:u{23:v-23:v-23:v-23:v},w:37"};

	//public static final int DuiDuiHe = 26;	
	public static final String[] DuiDuiHeText = {"对对和", "拥有四组刻子或杠子", "",
		"t:u{3:v-3:v-3:v},t:u{14:v-14:v-14:v},t:u{23:v-23:v-23:v},31:v,31:v,31:v,32:v,w:32"};
	
	//public static final int SanAnKe = 27;	
	public static final String[] SanAnKeText = {"三暗刻", "牌中有三组暗刻或暗杠", "",
		"1:v,1:v,1:v,12:v,12:v,12:v,21:v,21:v,21:v,23:v,24:v,25:v,31:v,w:31"};
	
	//public static final int XiaoSanYuan = 28;	
	public static final String[] XiaoSanYuanText = {"小三元", "凑齐中、发、白中两种的刻子或杠子和另一种的对子", "",
		"t:u{35:v-35:v-35:v},t:u{36:v-36:v-36:v},2:v,3:v,4:v,15:v,16:v,17:v,37:v,w:37"};
	
	//public static final int HunLaoTou = 29;	
	public static final String[] HunLaoTouText = {"混老头", "所有的牌均为幺九牌", "",
		"1:v,1:v,1:v,9:v,9:v,9:v,11:v,11:v,11:v,21:v,21:v,21:v,31:v,w:31"};
	
	/************************************* 三番 *************************************/
	//public static final int ErBeiKou = 31;	
	public static final String[] ErBeiKouText = {"二杯口", "包含2组一杯口（不再计算7对子）", "门前清",
		"21:v,21:v,22:v,22:v,23:v,23:v,12:v,12:v,13:v,13:v,14:v,14:v,31:v,w:31"};
	
	//public static final int ChunQuanDaiYaoJiu = 32;	
	public static final String[] ChunQuanDaiYaoJiuText = {"纯全带幺九", "所有的面子、对子都含有老头牌（有顺子）", "副露减1",
		"1:v,2:v,3:v,7:v,8:v,9:v,11:v,12:v,13:v,29:v,29:v,29:v,21:v,w:21"};
	
	//public static final int HunYiSe = 33;	
	public static final String[] HunYiSeText = {"混一色", "所有数牌均为同一花色，有字牌", "副露减1",
		"1:v,1:v,1:v,2:v,3:v,4:v,5:v,6:v,7:v,32:v,32:v,32:v,35:v,w:35"};
	
	/************************************* 六番 *************************************/
	//public static final int QingYiSe = 41;	
	public static final String[] QingYiSeText = {"清一色", "所有数牌均为同一花色，没有字牌", "副露减1",
		"s:u{1:v-2:v-3:v},s:u{2:v-3:v-4:v},s:u{3:v-4:v-5:v},t:u{6:v-6:v-6:v},t:u{9:v-9:v}"};
	
	/*********************************** 满贯（五番） **********************************/
	//public static final int LiuJuManGuan = 51;	
	public static final String[] LiuJuManGuanText = {"流局满贯", "自己的舍张全是幺九牌并且没有被他家吃碰杠的状态下荒牌流局", "",
		"9:v,1:v,31:v,31:v,32:v,32:v,37:v,32:v,33:v,33:v,33:v,19:v,19:v,19:v,29:v,36:v,34:v,9:v"};
	
	/********************************** 役满（十三番） **********************************/
	//public static final int TianHe = 61;	
	public static final String[] TianHeText = {"天和", "庄家第一巡和牌(不能开暗杠)", "庄家限定",
		"1:v,1:v,1:v,14:v,14:v,14:v,25:v,26:v,27:v,31:v,31:v,31:v,32:v,32:v"};
	
	//public static final int DiHe = 62;	
	public static final String[] DiHeText = {"地和", "子家在没有人鸣牌的情况下在第一巡自摸和牌", "子家限定",
		"1:v,1:v,1:v,14:v,14:v,14:v,25:v,26:v,27:v,31:v,31:v,31:v,32:v,w:32"};
	
	//public static final int RenHe = 63;	
	public static final String[] RenHeText = {"人和(古役)", "子家在没有人鸣牌的情况下在第一巡荣和", "子家限定",
		"1:v,1:v,1:v,14:v,14:v,14:v,25:v,26:v,27:v,31:v,31:v,31:v,32:v,w:32"};
	
	//public static final int DaCheLun = 64;	
	public static final String[] DaCheLunText = {"大车轮(古役)", "手牌由2~8筒的七对子组成", "门前清",
		"12:v,12:v,13:v,13:v,14:v,14:v,15:v,15:v,16:v,16:v,17:v,17:v,18:v,w:18"};
	
	//public static final int GuoShiWuShuang = 65;	
	public static final String[] GuoShiWuShuangText = {"国士无双", "凑齐12种幺九牌，其中1张成对，听第13种", "门前清",
		"1:v,9:v,11:v,19:v,19:v,21:v,29:v,31:v,32:v,33:v,34:v,35:v,36:v,w:37"};
	
	//public static final int JiuLianBaoDeng = 66;	
	public static final String[] JiuLianBaoDengText = {"九莲宝灯", "和牌型为清一色的1112345678999+任意一张", "门前清",
		"1:v,1:v,1:v,1:v,2:v,3:v,4:v,5:v,6:v,7:v,8:v,9:v,9:v,w:9"};
	
	//public static final int SiAnKe = 67;	
	public static final String[] SiAnKeText = {"四暗刻", "门前的对对和自摸", "门前清",
		"1:v,1:v,1:v,2:v,2:v,2:v,13:v,13:v,13:v,14:v,14:v,14:v,25:v,w:25"};
	
	//public static final int DaSanYuan = 68;	
	public static final String[] DaSanYuanText = {"大三元", "包含白、发、中的三组刻子或杠子", "",
		"s:u{21:v-22:v-23:v},t:u{35:v-35:v-35:v},t:u{36:v-36:v-36:v},t:u{37:v-37:v-37:v},t:u{29:v-29:v}"};
	
	//public static final int SiGangZi = 69;	
	public static final String[] SiGangZiText = {"四杠子", "1人开杠四次", "",
		"e:u{1:v-1:v-1:v-1:v},e:u{12:v-12:v-12:v-12:v},e:u{23:v-23:v-23:v-23:v},e:u{31:v-31:v-31:v-31:v},t:u{36:v-36:v}"};
	
	//public static final int LvYiSe = 70;	
	public static final String[] LvYiSeText = {"绿一色", "只包含索子的23468以及发", "无发也可以",
		"t:u{22:v-22:v-22:v},t:u{23:v-23:v-23:v},t:u{24:v-24:v-24:v},t:u{26:v-26:v-26:v},t:u{36:v-36:v}"};
	
	//public static final int ZiYiSe = 71;	
	public static final String[] ZiYiSeText = {"字一色", "只包含字牌", "",
		"t:u{31:v-31:v-31:v},t:u{32:v-32:v-32:v},t:u{33:v-33:v-33:v},t:u{35:v-35:v-35:v},t:u{36:v-36:v}"};
	
	//public static final int QingLaoTou = 72;	
	public static final String[] QingLaoTouText = {"清老头", "手牌中只有老头牌（19万，19筒，19索）", "",
		"t:u{1:v-1:v-1:v},t:u{9:v-9:v-9:v},t:u{11:v-11:v-11:v},t:u{19:v-19:v-19:v},t:u{21:v-21:v}"};
	
	//public static final int XiaoSiXi = 73;	
	public static final String[] XiaoSiXiText = {"小四喜", "包含三种风牌的刻子或杠子+剩下一种风牌的雀头", "",
		"s:u{1:v-2:v-3:v},t:u{31:v-31:v-31:v},t:u{32:v-32:v-32:v},t:u{33:v-33:v-33:v},t:u{34:v-34:v}"};
	
	//public static final int BaLianZhuang = 74;	
	public static final String[] BaLianZhuangText = {"八连庄(古役)", "亲家无流局连庄8次则在八本场之后按照自摸役满计算", "",
		""};
	
	/************************************ 双倍役满 ************************************/
	//public static final int SiAnKe_Single = 81;	
	public static final String[] SiAnKe_SingleText = {"四暗刻单骑", "四暗刻最后单骑听牌和牌", "门前清",
		"1:v,1:v,1:v,12:v,12:v,12:v,15:v,15:v,15:v,27:v,27:v,27:v,34:v,w:34"};
	
	//public static final int GuoShiWuShuang_Thirteen = 82;	
	public static final String[] GuoShiWuShuang_ThirteenText = {"国士无双十三面", "国士无双最后13面听牌和牌", "门前清",
		"1:v,9:v,11:v,19:v,21:v,29:v,31:v,32:v,33:v,34:v,35:v,36:v,37:v,w:1"};
	
	//public static final int JiuLianBaoDeng_Pure = 83;	
	public static final String[] JiuLianBaoDeng_PureText = {"纯正九莲宝灯", "九莲宝灯最后9面听牌和牌", "门前清",
		"1:v,1:v,1:v,2:v,3:v,4:v,5:v,6:v,7:v,8:v,9:v,9:v,9:v,w:5"};
	
	//public static final int DaSiXi = 84;	
	public static final String[] DaSiXiText = {"大四喜", "包含四种风牌的刻子或杠子", "",
		"31:v,31:v,31:v,32:v,32:v,32:v,33:v,33:v,33:v,34:v,34:v,34:v,15:v,w:15"};
	
	/************************************* 流局 *************************************/
	public static final int SiFengLianDa = 91;	
	public static final String[] SiFengLianDaText = {"四风连打", "无人鸣牌下4人在第一巡打出同一种风牌", "",
		"t:u{34:v},t:u{34:v},t:u{34:v},t:u{34:v}"};
	
	public static final int SiGangSanLe = 92;	
	public static final String[] SiGangSanLeText = {"四杠散了", "2人以上合计开杠4次", "",
		"e:u{1:v-1:v-1:v-1:v},e:u{12:v-12:v-12:v-12:v},e:u{15:v-15:v-15:v-15:v},e:u{19:v-19:v-19:v-19:v}"};
	
	public static final int JiuPaiJiuZhong = 93;	
	public static final String[] JiuPaiJiuZhongText = {"九牌九种", "轮到自己之前无人鸣牌下拥有9种及以上的幺九牌", "",
		"1:v,9:v,11:v,18:v,19:v,23:v,23:v,24:v,27:v,29:v,31:v,33:v,36:v,w:37"};
	
	public static final int SiJiaLiZhi = 94;	
	public static final String[] SiJiaLiZhiText = {"四家立直", "4人全部宣告立直成功", "",
		""};
	
	public static final int SanJiaHeLiao = 95;	
	public static final String[] SanJiaHeLiaoText = {"三家和了", "在对局过程中，某人的舍牌同时放铳其他三家", "",
		""};
	
}
