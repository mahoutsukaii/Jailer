package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alec
 */
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
//import java.util.ArrayList;
import me.mahoutsukaii.plugins.jailer.Jailer;

import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")

public class MySQLDatabase{

	Jailer plugin;
	public String mysqlTable;

	public static Connection getSQLConnection() {
		Configuration Config = new Configuration(new File("plugins/Jailer/config.yml"));
		Config.load();
		String mysqlDatabase = Config.getNode("MySQL").getString("database","jdbc:mysql://localhost:3306/minecraft");
		String mysqlUser = Config.getNode("MySQL").getString("user","root");
		String mysqlPassword = Config.getNode("MySQL").getString("password","root");

		try {

			return DriverManager.getConnection(mysqlDatabase + "?autoReconnect=true&user=" + mysqlUser + "&password=" + mysqlPassword);
		} catch (SQLException ex) {
			Jailer.log.log(Level.SEVERE, "Unable to retreive connection", ex);
		}
		return null;
	}

	public void initialise(Jailer plugin){
		this.plugin = plugin;
		Connection conn = getSQLConnection();
		this.mysqlTable = plugin.getConfiguration().getNode("MySQL").getString("table","banlist");
		plugin.jailedPlayers.clear();
		plugin.jailTimes.clear();
		if (conn == null) {
			Jailer.log.log(Level.SEVERE, "[Jailer] Could not establish SQL connection. Disabling Jailer");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		} else {

			PreparedStatement ps = null;
			ResultSet rs = null;
			try {

			ps = conn.prepareStatement("SELECT * FROM " + mysqlTable);
			rs = ps.executeQuery();
				while (rs.next()){
					plugin.jailedPlayers.add(rs.getString("name").toLowerCase());
					plugin.jailTimes.add(rs.getLong("timeleft"));
					
				}
			} catch (SQLException ex) {
				Jailer.log.log(Level.SEVERE, "[Jailer] Couldn't execute MySQL statement: ", ex);
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					Jailer.log.log(Level.SEVERE, "[Jailer] Failed to close MySQL connection: ", ex);
				}
			}	

			try {
				conn.close();
				Jailer.log.log(Level.INFO, "[Jailer] MySQL connection initialised." );
			} catch (SQLException e) {
				e.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
	}


	public long getTimeRemaining(String player)
	{
		

		Connection conn = getSQLConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM " + mysqlTable + " WHERE name = ?");
			ps.setString(1, player);
			rs = ps.executeQuery();
			while (rs.next()){
				return rs.getLong("timeleft");
			}
		} catch (SQLException ex) {
			Jailer.log.log(Level.SEVERE, "[Jailer] Couldn't execute MySQL statement: ", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				Jailer.log.log(Level.SEVERE, "[Jailer] Failed to close MySQL connection: ", ex);
			}
		}
		return 0;

	}
	
	public void jailPlayer(String player, long timeRemaining)
	{

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + mysqlTable + " (name,timeleft) VALUES(?,?)");

			ps.setString(1, player);
			ps.setLong(2, timeRemaining);
			ps.executeUpdate();
		} catch (SQLException ex) {
			Jailer.log.log(Level.SEVERE, "[Jailer] Couldn't execute MySQL statement: ", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				Jailer.log.log(Level.SEVERE, "[Jailer] Failed to close MySQL connection: ", ex);
			}
		}

	}
	
	public void setTimeRemaining(String player, long timeRemaining)
	{
		Connection conn = getSQLConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + mysqlTable + " SET timeleft = ? WHERE name = ?");
			ps.setLong(1, timeRemaining);
			ps.setString(2, player);
			ps.executeUpdate();

		} catch (SQLException ex) {
			Jailer.log.log(Level.SEVERE, "[Jailer] Couldn't execute MySQL statement: ", ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				Jailer.log.log(Level.SEVERE, "[Jailer] Failed to close MySQL connection: ", ex);
			}
		}

	}

	public boolean bail(String player)
	{

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("DELETE FROM " + mysqlTable + " WHERE name = ? LIMIT 1");
			ps.setString(1, player);
			ps.executeUpdate();
		} catch (SQLException ex) {
			Jailer.log.log(Level.SEVERE, "[Jailer] Couldn't execute MySQL statement: ", ex);
			return false;
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				Jailer.log.log(Level.SEVERE, "[Jailer] Failed to close MySQL connection: ", ex);
			}
		}
		
		return true;

	}
	
}
