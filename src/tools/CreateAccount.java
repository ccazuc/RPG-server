package tools;

import java.sql.SQLException;
import java.util.Scanner;

import jdo.JDO;
import jdo.JDOStatement;
import jdo.wrapper.MariaDB;
import net.game.manager.AccountMgr;
import net.game.manager.DatabaseMgr;
import net.utils.Hash;


public class CreateAccount {
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your account name.");
		String accountName = sc.nextLine();
		System.out.println("Enter your password.");
		String password = sc.nextLine();
		System.out.println("Enter your email.");
		String email = sc.nextLine();
		System.out.println(accountName + " " + password + " " + email);
		JDO jdo = new MariaDB("127.0.0.1", DatabaseMgr.PORT, DatabaseMgr.TABLE_NAME, DatabaseMgr.USER_NAME, DatabaseMgr.PASSWORD);
		JDOStatement checkAccountExists = jdo.prepare("SELECT COUNT(*) FROM `account` WHERE `name` = ?");
		checkAccountExists.clear();
		checkAccountExists.putString(accountName);
		int amount = 0;
		checkAccountExists.execute();
		if (checkAccountExists.fetch() && (amount = checkAccountExists.getInt()) > 0)
		{
			System.out.println("This account name is already taken.");
			return;
		}
		System.out.println("-----------------------------");
		JDOStatement checkEmailExists = jdo.prepare("SELECT COUNT(*) FROM `account` WHERE `email` = ?");
		checkEmailExists.clear();
		checkEmailExists.putString(email);
		checkEmailExists.execute();
		if (checkEmailExists.fetch() && (amount = checkEmailExists.getInt()) > 0)
		{
			System.out.println("This email is already taken.");
			return;
		}
		if (!checkAccountName(accountName))
		{
			System.out.println("Invalid account name.");
			return;
		}
		String salt = Hash.generateSalt(9);
		password = Hash.hash(password, "");
		String finalPassword = Hash.hash(password, salt);
		JDOStatement createAccount = jdo.prepare("INSERT INTO `account` (`name`, `password`, `salt`, `email`, `rank`) VALUES (?, ?, ?, ?, 1)");
		createAccount.putString(accountName);
		createAccount.putString(finalPassword);
		createAccount.putString(salt);
		createAccount.putString(email);
		createAccount.execute();
		System.out.println("Account successfully created.");
	}
	
	private static boolean checkAccountName(String name)
	{
		return (true);
	}
}