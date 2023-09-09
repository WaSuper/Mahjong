package com.mahjong.activity;

import com.mahjong.R;
import com.mahjong.anime.SwingAnimation;
import com.mahjong.tools.FileTools;
import com.mahjong.tools.GetSize;
import com.mahjong.tools.PackageUtils;
import com.mahjong.ui.CommonDialog;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private ImageView backView;
	private TextView versionView;
	private RelativeLayout mFunctionLayout;
	private RelativeLayout mFeedbackLayout;
	private RelativeLayout mUpgradeLayout;
	private ImageView mAppView;
	private SwingAnimation mSwingAnimation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initUI();
	}
	
	private void initUI() {
		backView = (ImageView) findViewById(R.id.about_back);
		versionView = (TextView) findViewById(R.id.about_tv_version);
		mFunctionLayout = (RelativeLayout) findViewById(R.id.about_rl_function_introduce);
		mFeedbackLayout = (RelativeLayout) findViewById(R.id.about_rl_feedback);
		mUpgradeLayout = (RelativeLayout) findViewById(R.id.about_rl_upgrade);
		mAppView = (ImageView) findViewById(R.id.about_img_app);
		
		backView.setOnClickListener(this);
		mFunctionLayout.setOnClickListener(this);
		mFeedbackLayout.setOnClickListener(this);
		mUpgradeLayout.setOnClickListener(this);
		
		versionView.setText(getString(R.string.version, PackageUtils.getVersionName(this)));
		
		mSwingAnimation = new SwingAnimation(
                0f, 30f, -30f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
        mSwingAnimation.setDuration(3000);     //动画持续时间
        mSwingAnimation.setInterpolator(new LinearInterpolator());
        mSwingAnimation.setRepeatCount(Animation.INFINITE);  //动画重播次数
        mSwingAnimation.setFillAfter(false);  //是否保持动画结束画面
        mSwingAnimation.setStartOffset(0);   //动画播放延迟
        mAppView.setAnimation(mSwingAnimation);
        mAppView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.about_back:
			finish();
			break;
		case R.id.about_img_app:
			FileTools.getDatabaseFiles(this);
			break;
		case R.id.about_rl_function_introduce:
			showIntroduceDialog();
			break;
		case R.id.about_rl_feedback:
			showFeedbackDialog();
			break;
		case R.id.about_rl_upgrade:
			showUpgradeDialog();
			break;
		default:
			break;
		}
	}
	
	private void showIntroduceDialog() {
		final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle, 0);		
		dialog.addView(R.layout.item_text);
		dialog.titleTextView.setText(R.string.function_introduce);
		TextView content = (TextView) dialog.getContentView().findViewById(R.id.item_text);
		content.setGravity(Gravity.LEFT);
		content.setTextSize(16);
		content.setMovementMethod(new ScrollingMovementMethod());
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("这个作者很懒，什么都没说，只能靠同志们自己去探索→_→");
		content.setText(sBuffer.toString());
		dialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void showFeedbackDialog() {
		final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle, 0);		
		dialog.addView(R.layout.item_text);
		dialog.titleTextView.setText(R.string.feedback);
		TextView content = (TextView) dialog.getContentView().findViewById(R.id.item_text);
		content.setGravity(Gravity.LEFT);
		content.setTextSize(14);
		content.setAutoLinkMask(Linkify.WEB_URLS);
		content.setMovementMethod(LinkMovementMethod.getInstance());
		content.setText("源代码地址：\nhttps://github.com/WaSuper/Mahjong\n有关应用功能性问题及相关建议请在上面提出");
		dialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void showUpgradeDialog() {
		final CommonDialog dialog = new CommonDialog(this, R.style.MyDialogStyle, 0);		
		dialog.addView(R.layout.item_text);
		dialog.titleTextView.setText(R.string.upgrade);
		TextView content = (TextView) dialog.getContentView().findViewById(R.id.item_text);
		content.setHeight(GetSize.dip2px(this, 350));
		content.setGravity(Gravity.LEFT);
		content.setTextSize(16);
		content.setMovementMethod(new ScrollingMovementMethod());
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("230909更新：\n1.修复特殊情况无法南入/西入问题；\n2.修复玩家太多选择框无法确认问题；\n3.修复流局终局时立直棒归属问题；\n4.修复无击飞模式负分无法立直问题；\n5.增加个人数据平均打点和平均铳点；\n6.增加终局时末位玩家第一无限连庄；\n7.增加正方形模式；");
		sBuffer.append("\n\n230415更新：\n1.补全三麻包牌功能；\n2.修复三麻摇骰子功能；\n3.增加切上满贯设置；\n4.增加无击飞设置；");
		sBuffer.append("\n\n230322更新：\n1.增加三麻、十七步玩法；\n2.全新开场动画；\n3.修复自风场风符数出错问题；\n4.增加5本场以上中间计数盘颜色变化；\n5.修复一系列Bug；");
		sBuffer.append("\n\n220831更新：\n1.增加新玩法《十七步》（暂时只有计算器...）；\n2.增加横屏模式；\n3.修复部分手机立直崩溃问题；\n4.修复部分番种出错问题；\n5.新增崩溃记录（记录在/Mahjong/crash文件夹中，有问题可发送此文件给我）");
		sBuffer.append("\n\n220429更新：\n1.增加自定义返点、基本分；\n2.增加部分非主流役种（可自行开启或关闭）；\n3.修复部分情况下没有计算平和；");
		sBuffer.append("\n\n211230更新：\n1.增加文件按修改时间排序；\n2.增加批量导入表情包功能；\n3.将音频包功能独立出来，增加部分音效；\n4.导出玩家时包含音频包及表情包数据；");
		sBuffer.append("\n\n210317更新：\n1.增加对战界面玩家排位显示；\n2.增加个人音频设置中历史音频选项；\n3.增加全局设置音频，一个音频可设置选择多位玩家；\n4.修改文件排序；\n5.修复部分牌型（大四喜、三暗刻、一杯口、二杯口）识别出错；");
		sBuffer.append("\n\n200618更新：\n1.更新计算器显示效果；\n2.增加排行榜个人统计数据；\n3.增加对局时查看分差；\n4.修改部分UI；");
		sBuffer.append("\n\n200430更新：\n1.添加计分结算明细；\n2.增加表情包功能（清除旧式头像）；\n3.更新玩家选择界面；\n4.增加简易排行榜功能；");
		sBuffer.append("\n\n191111更新：\n1.更换app图标，并更名为“日麻Saki”；\n2.增加雀魂头像；\n3.可以为每局添加标题和备注；\n4.升级抽卡功能；\n5.结算时增加点数方向指示；");
		sBuffer.append("\n\n191012更新：\n1.第一次发布版本。");
		sBuffer.append("\n");
		content.setText(sBuffer.toString());
		dialog.ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
}
