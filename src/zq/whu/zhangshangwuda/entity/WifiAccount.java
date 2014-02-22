package zq.whu.zhangshangwuda.entity;

public class WifiAccount {

	private int id;
	private String username;
	private String password;

	public WifiAccount(int id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public WifiAccount(String username, String password) {
		super();
		this.id = -1;
		this.username = username;
		this.password = password;
	}

	public WifiAccount() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
