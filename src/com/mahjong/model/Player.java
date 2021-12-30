package com.mahjong.model;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.mahjong.R;

@Table(name = "Player")
public class Player extends Model {

	public static final String Col_Uuid 		= "Uuid";
	public static final String Col_Name 		= "Name";
	public static final String Col_NickName 	= "NickName";
	public static final String Col_Sex 			= "Sex";
	public static final String Col_Sign 		= "Sign";
	public static final String Col_Icon 		= "Icon";
	public static final String Col_CharacterId 	= "CharacterId";
	
	public static final String[] Columns = {
		Col_Uuid, Col_Name, Col_NickName, Col_Sex, Col_Sign, Col_Icon, Col_CharacterId
	};
	
	public static final int NPCsize = 6;
	
	@Column(name = "Uuid", unique = true)
	private String uuid;		// id
	
	@Column(name = "Name")
	private String name; 		// 姓名
	
	@Column(name = "NickName")
	private String nick_name;	// 昵称
	
	@Column(name = "Sex")
	private char sex; 			// M：男，F：女
	
	@Column(name = "Sign")
	private String sign; 		// 个性签名
	
	@Column(name = "Icon")
	private String icon;		// 头像
		
	@Column(name = "CharacterId")
	private long character_id;	// 玩家角色
	
	@Column(name = "SoundBoxIcon")
	private String soundbox_icon;	// 音频头像
	
	@Column(name = "SoundBoxId")
	private long soundbox_id;	// 玩家音频
	
	public Player() {
		super();
	}
	
	public Player(String uuid, String name, String nick_name, char sex, 
			String sign, String icon) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.nick_name = nick_name;
		this.sex = sex;
		this.sign = sign;
		this.icon = icon;
		this.character_id = -1;
	}
	
	public Player(String uuid, String name, String nick_name, char sex, 
			String sign, String icon, long character_id, long soundbox_id) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.nick_name = nick_name;
		this.sex = sex;
		this.sign = sign;
		this.icon = icon;
		this.character_id = character_id;
		this.soundbox_id = soundbox_id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNickName(String nickName) {
		this.nick_name = nickName;
	}
	
	public void setSex(char sex) {
		this.sex = sex;
	}
	
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public void setCharacter(Character character) {
		this.character_id = character.getUuid();
		this.icon = character.getDefaultIcon();
	}
	
	public void setCharacterId(long character_id) {
		this.character_id = character_id;
	}
	
	public void setSoundBox(SoundBox soundbox) {
		this.soundbox_id = soundbox.getUuid();
		this.soundbox_icon = soundbox.getDefaultIcon();
	}
	
	public void setSoundBoxId(long soundbox_id) {
		this.soundbox_id = soundbox_id;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNickName() {
		return nick_name;
	}
	
	public char getSex() {
		return sex;
	}
	
	public String getSign() {
		return sign;
	}
	
	public String getIcon() {
		return icon;
	}
		
	public long getCharacterId() {
		return character_id;
	}
		
	public String getSoundBoxIcon() {
		return soundbox_icon;
	}
	
	public long getSoundBoxId() {
		return soundbox_id;
	}
	
	public static List<Player> getAllPlayer() {
		List<Player> list = new Select().from(Player.class).execute();
		return list;
	}
	
	public static Player getPlayer(String uuid) {
		Player player = new Select().from(Player.class).where(Player.Col_Uuid + "=?", uuid).executeSingle();
		return player;
	}
	
	public static Player[] getNPCPlayers() {
		Player[] mNPCs = new Player[NPCsize];
		mNPCs[0] = new Player("pc-saki", "宫永咲", "宫永咲", 'F', "岭上开火，打麻将真TM开心！", 
				"drawable://" + R.drawable.head_pc_saki, 0, 0);
		mNPCs[1] = new Player("pc-nodoka", "原村和", "原村和", 'F', "企鹅桑，欣赏下这群弱者的表情！", 
				"drawable://"+ R.drawable.head_pc_nodoka, 0, 0);
		mNPCs[2] = new Player("pc-koromo", "天江衣", "天江衣", 'F', "大海的力量果然深不可测!", 
				"drawable://"+ R.drawable.head_pc_koromo, 0, 0);
		mNPCs[3] = new Player("pc-komaki", "神代小莳", "神代小莳", 'F', "神灵神灵，快快显灵！", 
				"drawable://"+ R.drawable.head_pc_komaki, 0, 0);
		mNPCs[4] = new Player("pc-teru", "宫永照", "宫永照", 'F', "让我来\"慢慢\"打败你们！", 
				"drawable://"+ R.drawable.head_pc_teru, 0, 0);
		mNPCs[5] = new Player("pc-shizuno", "高鸭稳乃", "高鸭稳乃", 'F', "这里不是你的领域了！", 
				"drawable://"+ R.drawable.head_pc_shizuno, 0, 0);
		return mNPCs;
	}
}
