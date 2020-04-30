package com.mahjong.data.jpn;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * 回溯方法
 */
public class AgariBacktrack {

    static final int[] n_zero;
    static {
        n_zero = new int[34];
        Arrays.fill(n_zero, 0);
    }
    
    // 牌の種類ごとの個数を求める
    static int[] analyse(int[] hai) {
        int[] n = n_zero.clone();

        for (int i : hai) {
            n[i]++;
        }
        return n;
    }

    // バックトラック法で雀頭と面子の組み合わせを求める
    static List<Integer[][]> agari(int[] n) {
        List<Integer[][]> ret = new ArrayList<Integer[][]>();
        
        for (int i = 0; i < 34; i++) {
            for (int kotsu_first = 0; kotsu_first < 2; kotsu_first++) {
                Integer[] janto = new Integer[1];
                ArrayList<Integer> kotsu = new ArrayList<Integer>();
                ArrayList<Integer> shuntsu = new ArrayList<Integer>();
                
                int[] t = n.clone();
                if (t[i] >= 2) {
                    // 雀頭をはじめに取り出す
                    t[i] -= 2;
                    janto[0] = i;

                    if (kotsu_first == 0) {
                        // 刻子を先に取り出す
                        for (int j = 0; j < 34; j++) {
                            if (t[j] >= 3) {
                                t[j] -= 3;
                                kotsu.add(j);
                            }
                        }
                        // 順子を後に取り出す
                        for (int a = 0; a < 3; a++) {
                            for (int b = 0; b < 7;) {
                                if (t[9*a+b] >= 1 &&
                                    t[9*a+b+1] >= 1 &&
                                    t[9*a+b+2] >= 1) {
                                    t[9*a+b]--;
                                    t[9*a+b+1]--;
                                    t[9*a+b+2]--;
                                    shuntsu.add(9*a+b);
                                } else {
                                    b++;
                                }
                            }
                        }
                    } else {
                        // 順子を先に取り出す
                        for (int a = 0; a < 3; a++) {
                            for (int b = 0; b < 7;) {
                                if (t[9*a+b] >= 1 &&
                                    t[9*a+b+1] >= 1 &&
                                    t[9*a+b+2] >= 1) {
                                    t[9*a+b]--;
                                    t[9*a+b+1]--;
                                    t[9*a+b+2]--;
                                    shuntsu.add(9*a+b);
                                } else {
                                    b++;
                                }
                            }
                        }
                        // 刻子を後に取り出す
                        for (int j = 0; j < 34; j++) {
                            if (t[j] >= 3) {
                                t[j] -= 3;
                                kotsu.add(j);
                            }
                        }
                    }

                    // 和了の形か調べる
                    if (Arrays.equals(t, n_zero)) {
                        ret.add(new Integer[][] {janto, kotsu.toArray(new Integer[0]), shuntsu.toArray(new Integer[0])});
                    }
                }
            }
        }
        return ret;
    }

    public static void done() {
        int[] hai = {
        		JpnSetting.MAN1, JpnSetting.MAN1, JpnSetting.MAN1,
        		JpnSetting.MAN2, JpnSetting.MAN3, JpnSetting.MAN4,
        		JpnSetting.MAN6, JpnSetting.MAN7, JpnSetting.MAN8,
        		JpnSetting.TON, JpnSetting.TON, JpnSetting.TON,
        		JpnSetting.SHA, JpnSetting.SHA};

        int[] n = null;
        List<Integer[][]> ret = null;
        
        long time = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
            n = analyse(hai);
            ret = agari(n);
//        }
        System.out.println(System.currentTimeMillis() - time);
        
        for (Integer[][] r : ret) {
            System.out.print("雀頭=");
            System.out.println(r[0][0]);
            for (Integer kotsu : r[1]) {
                System.out.print("刻子=");
                System.out.println(kotsu);
            }
            for (Integer shuntsu : r[2]) {
                System.out.print("順子=");
                System.out.println(shuntsu);
            }
        }
    }
}
