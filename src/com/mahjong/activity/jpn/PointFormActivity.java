package com.mahjong.activity.jpn;

import java.util.ArrayList;
import java.util.List;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellRange;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.mahjong.R;
import com.mahjong.activity.BaseActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PointFormActivity extends BaseActivity implements OnClickListener {

	private ImageView mBack;
	private TextView mDealerView;
	private TextView mPlayerView;
	private SmartTable<FormInfo> mDealerSmartTable;
	private SmartTable<FormInfo> mPlayerSmartTable;
	
	private boolean isDealer = true;
	private TableData<FormInfo> mDealerTable;
	private TableData<FormInfo> mPlayerTable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_form);
		initUI();
	}
	
	@SuppressWarnings("unchecked")
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.form_back);
		mDealerView = (TextView) findViewById(R.id.form_tv_dealer);
		mPlayerView = (TextView) findViewById(R.id.form_tv_player);
		mDealerSmartTable = (SmartTable<FormInfo>) findViewById(R.id.form_smarttable_dealer);
		mPlayerSmartTable = (SmartTable<FormInfo>) findViewById(R.id.form_smarttable_player);
        
		mBack.setOnClickListener(this);
		mDealerView.setOnClickListener(this);
		mPlayerView.setOnClickListener(this);
		
		createDealerTable();
		mDealerSmartTable.setTableData(mDealerTable);
		TableConfig config1 = mDealerSmartTable.getConfig();
		config1.setColumnTitleStyle(new FontStyle(45, Color.DKGRAY));
		config1.setContentStyle(new FontStyle(45, Color.WHITE));
		config1.setFixedTitle(true);
		config1.setShowTableTitle(false);
		config1.setShowXSequence(false);
		config1.setShowYSequence(false);
		
		createPlayerTable();
		mPlayerSmartTable.setTableData(mPlayerTable);
		TableConfig config2 = mPlayerSmartTable.getConfig();
		config2.setColumnTitleStyle(new FontStyle(45, Color.DKGRAY));
		config2.setContentStyle(new FontStyle(45, Color.WHITE));
		config2.setFixedTitle(true);
		config2.setShowTableTitle(false);
		config2.setShowXSequence(false);
		config2.setShowYSequence(false);
		
	}
	
	private void createDealerTable() {
		Column<String> fan = new Column<String>("亲", "fan");
		Column<String> fu20 = new Column<String>("20符", "fu20");
		Column<String> fu25 = new Column<String>("25符", "fu25");
		Column<String> fu30 = new Column<String>("30符", "fu30");
		Column<String> fu40 = new Column<String>("40符", "fu40");
		Column<String> fu50 = new Column<String>("50符", "fu50");
		Column<String> fu60 = new Column<String>("60符", "fu60");
		Column<String> fu70 = new Column<String>("70符", "fu70");
		Column<String> fu80 = new Column<String>("80符", "fu80");
		Column<String> fu90 = new Column<String>("90符", "fu90");
		Column<String> fu100 = new Column<String>("100符", "fu100");
		Column<String> fu110 = new Column<String>("110符", "fu110");
		fan.setAutoMerge(true);
		fan.setFixed(true);
		fan.setWidth(100);
		
		List<FormInfo> infos = new ArrayList<FormInfo>();
		infos.add(new FormInfo("1番", "-", "-", "1500", "2000", "2400", "2900", "3400", "3900", "4400", "4800", "5300"));
		infos.add(new FormInfo("1番", "-", "-", "-500", "-700", "-800", "-1000", "-1200", "-1300", "-1500", "-1600", "(-)"));
		infos.add(new FormInfo("2番", "-", "2400", "2900", "3900", "4800", "5800", "6800", "7700", "8700", "9600", "10600"));
		infos.add(new FormInfo("2番", "-700", "(-)", "-1000", "-1300", "-1600", "-2000", "-2300", "-2600", "-2900", "-3200", "-3600"));
		infos.add(new FormInfo("3番", "-", "4800", "5800", "7700", "9600", "11600", "满贯", "", "", "", ""));
		infos.add(new FormInfo("3番", "-1300", "-1600", "-2000", "-2600", "-3200", "-3900", "12000", "", "", "", ""));
		infos.add(new FormInfo("4番", "-", "9600", "11600", "", "", "", "-4000", "", "", "", ""));
		infos.add(new FormInfo("4番", "-2600", "-3200", "-3900", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("5番", "", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("6番", "跳满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("7番", "18000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "-6000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("8番", "倍满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("9番", "24000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("10番", "-8000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("11番", "三倍满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("12番", "36000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "-12000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "累计役满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "48000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "-16000", "", "", "", "", "", "", "", "", "", ""));
		
		mDealerTable = new TableData<FormInfo>("亲家", infos,
				fan, fu20, fu25, fu30, fu40, fu50, fu60, fu70, fu80, fu90, fu100, fu110);
		
		List<CellRange> userCellRange = new ArrayList<CellRange>();
		userCellRange.add(new CellRange(0, 1, 1, 1));
		userCellRange.add(new CellRange(0, 1, 2, 2));
		userCellRange.add(new CellRange(4, 4, 7, 11));
		userCellRange.add(new CellRange(5, 5, 7, 11));
		userCellRange.add(new CellRange(6, 6, 7, 11));
		userCellRange.add(new CellRange(7, 7, 7, 11));
		userCellRange.add(new CellRange(8, 8, 7, 11));
		userCellRange.add(new CellRange(8, 8, 1, 3));
		userCellRange.add(new CellRange(6, 8, 4, 6));
		userCellRange.add(new CellRange(9, 9, 1, 11));
		userCellRange.add(new CellRange(10, 10, 1, 11));
		userCellRange.add(new CellRange(11, 11, 1, 11));
		userCellRange.add(new CellRange(12, 12, 1, 11));
		userCellRange.add(new CellRange(13, 13, 1, 11));
		userCellRange.add(new CellRange(14, 14, 1, 11));
		userCellRange.add(new CellRange(15, 15, 1, 11));
		userCellRange.add(new CellRange(16, 16, 1, 11));
		userCellRange.add(new CellRange(17, 17, 1, 11));
		userCellRange.add(new CellRange(18, 18, 1, 11));
		userCellRange.add(new CellRange(19, 19, 1, 11));
		userCellRange.add(new CellRange(20, 20, 1, 11));
		
		mDealerTable.setUserCellRange(userCellRange);
		
    }

	private void createPlayerTable() {
		Column<String> fan = new Column<String>("子", "fan");
		Column<String> fu20 = new Column<String>("20符", "fu20");
		Column<String> fu25 = new Column<String>("25符", "fu25");
		Column<String> fu30 = new Column<String>("30符", "fu30");
		Column<String> fu40 = new Column<String>("40符", "fu40");
		Column<String> fu50 = new Column<String>("50符", "fu50");
		Column<String> fu60 = new Column<String>("60符", "fu60");
		Column<String> fu70 = new Column<String>("70符", "fu70");
		Column<String> fu80 = new Column<String>("80符", "fu80");
		Column<String> fu90 = new Column<String>("90符", "fu90");
		Column<String> fu100 = new Column<String>("100符", "fu100");
		Column<String> fu110 = new Column<String>("110符", "fu110");
		fan.setAutoMerge(true);
		fan.setFixed(true);
		fan.setWidth(100);
		
		List<FormInfo> infos = new ArrayList<FormInfo>();
		infos.add(new FormInfo("1番", "-", "-", "1000", "1300", "1600", "2000", "2300", "2600", "2900", "3200", "3600"));
		infos.add(new FormInfo("1番", "-", "-", "(300,", "(400,", "(400,", "(500,", "(600,", "(700,", "(800,", "(800,", "(-)"));
		infos.add(new FormInfo("1番", "-", "-", "500)", "700)", "800)", "1000)", "1200)", "1300)", "1500)", "1600)", ""));
		infos.add(new FormInfo("2番", "-", "1600", "2000", "2600", "3200", "3900", "4500", "5200", "5800", "6400", "7100"));
		infos.add(new FormInfo("2番", "(400,", "(-)", "(500,", "(700,", "(800,", "(1000,", "(1200,", "(1300,", "(1500,", "(1600,", "(1800,"));
		infos.add(new FormInfo("2番", "700)", "", "1000)", "1300)", "1600)", "2000)", "2300)", "2600)", "2900)", "3200)", "3600)"));
		infos.add(new FormInfo("3番", "-", "3200", "3900", "5200", "6400", "7700", "满贯", "", "", "", ""));
		infos.add(new FormInfo("3番", "(700,", "(800,", "(1000,", "(1300,", "(1600,", "(2000,", "8000", "", "", "", ""));
		infos.add(new FormInfo("3番", "1300)", "1600)", "2000)", "2600)", "3200)", "3900)", "(2000,", "", "", "", ""));
		infos.add(new FormInfo("4番", "-", "6400", "7700", "", "", "", "4000)", "", "", "", ""));
		infos.add(new FormInfo("4番", "(1300,", "(1600,", "(2000,", "", ",", "", "", "", "", "", ""));
		infos.add(new FormInfo("4番", "2600)", "3200)", "3900)", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("5番", "", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("6番", "跳满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("7番", "12000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "(3000,", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "6000)", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("8番", "倍满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("9番", "16000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("10番", "(4000,", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "8000)", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("11番", "三倍满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("12番", "24000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "(6000,", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("", "12000)", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "累计役满", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "32000", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "(8000,", "", "", "", "", "", "", "", "", "", ""));
		infos.add(new FormInfo("13番以上", "18000)", "", "", "", "", "", "", "", "", "", ""));
		
		mPlayerTable = new TableData<FormInfo>("子家", infos,
				fan, fu20, fu25, fu30, fu40, fu50, fu60, fu70, fu80, fu90, fu100, fu110);
		
		List<CellRange> userCellRange = new ArrayList<CellRange>();
		userCellRange.add(new CellRange(0, 2, 1, 1));
		userCellRange.add(new CellRange(0, 2, 2, 2));
		userCellRange.add(new CellRange(6, 6, 7, 11));
		userCellRange.add(new CellRange(7, 7, 7, 11));
		userCellRange.add(new CellRange(8, 8, 7, 11));
		userCellRange.add(new CellRange(9, 9, 7, 11));
		userCellRange.add(new CellRange(10, 10, 7, 11));
		userCellRange.add(new CellRange(11, 11, 7, 11));
		userCellRange.add(new CellRange(12, 12, 7, 11));
		userCellRange.add(new CellRange(12, 12, 4, 6));
		userCellRange.add(new CellRange(9, 12, 4, 6));
		userCellRange.add(new CellRange(13, 13, 1, 11));
		userCellRange.add(new CellRange(14, 14, 1, 11));
		userCellRange.add(new CellRange(15, 15, 1, 11));
		userCellRange.add(new CellRange(16, 16, 1, 11));
		userCellRange.add(new CellRange(17, 17, 1, 11));
		userCellRange.add(new CellRange(18, 18, 1, 11));
		userCellRange.add(new CellRange(19, 19, 1, 11));
		userCellRange.add(new CellRange(20, 20, 1, 11));
		userCellRange.add(new CellRange(21, 21, 1, 11));
		userCellRange.add(new CellRange(22, 22, 1, 11));
		userCellRange.add(new CellRange(23, 23, 1, 11));
		userCellRange.add(new CellRange(24, 24, 1, 11));
		userCellRange.add(new CellRange(25, 25, 1, 11));
		userCellRange.add(new CellRange(26, 26, 1, 11));
		userCellRange.add(new CellRange(27, 27, 1, 11));
		userCellRange.add(new CellRange(28, 28, 1, 11));
		
		mPlayerTable.setUserCellRange(userCellRange);
        
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.form_back:
			this.finish();
			break;
		case R.id.form_tv_dealer:
			if (isDealer) return;
			isDealer = true;
			mDealerView.setTextColor(getResources().getColor(R.color.main));
			mPlayerView.setTextColor(getResources().getColor(R.color.white));	
			mDealerSmartTable.setVisibility(View.VISIBLE);
			mPlayerSmartTable.setVisibility(View.GONE);
			break;
		case R.id.form_tv_player:
			if (!isDealer) return;
			isDealer = false;
			mPlayerView.setTextColor(getResources().getColor(R.color.main));
			mDealerView.setTextColor(getResources().getColor(R.color.white));
			mPlayerSmartTable.setVisibility(View.VISIBLE);
			mDealerSmartTable.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	public class FormInfo {
		
		private String fan;
		private String fu20;
		private String fu25;
		private String fu30;
		private String fu40;
		private String fu50;
		private String fu60;
		private String fu70;
		private String fu80;
		private String fu90;
		private String fu100;
		private String fu110;
		
		public FormInfo(String fan, String fu20, String fu25, String fu30, String fu40, String fu50, 
				String fu60, String fu70, String fu80, String fu90, String fu100, String fu110) {
			this.fan = fan;
			this.fu20 = fu20;
			this.fu25 = fu25;
			this.fu30 = fu30;
			this.fu40 = fu40;
			this.fu50 = fu50;
			this.fu60 = fu60;
			this.fu70 = fu70;
			this.fu80 = fu80;
			this.fu90 = fu90;
			this.fu100 = fu100;
			this.fu110 = fu110;
		}
		
	}
	
}
