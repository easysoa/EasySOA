package org.easysoa.proxy.app.api;

public class User {

    private String login;
	private String username;
	private String password;
	private String mail;

	public User() {
    }

    public User(String login, String username, String password, String mail) {
    	this();
        this.login = login;
        this.username = username;
        this.password = password;
        this.mail = mail;
    }

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
    
}
