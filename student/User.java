package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.UserOperations;

public class User implements UserOperations {

    @Override
    public boolean insertUser(String string, String string1, String string2, String string3) {
        Connection conn = DB.getInstance().getConnection();
        String query="INSERT INTO [dbo].[Users]\n" +
"           ([Username], [Name]\n" +
"           ,[Surname]\n" +
"           ,[Password]\n" +
"           ,[NumSentPackets])\n" +
"     VALUES  (?, ?, ?, ?, 0)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.setString(3, string2);
            ps.setString(4, string3);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

    @Override
    public int declareAdmin(String string) {
        Connection conn=DB.getInstance().getConnection();
                
        String querySelectUser="SELECT Username FROM [dbo].[Users] where Username='" + string + "'";
         try (
            PreparedStatement stmt=conn.prepareStatement(querySelectUser);
            ResultSet rs=stmt.executeQuery()){
            if(!rs.next())
                return 2;
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String query="SELECT Username FROM [dbo].[Administrator] where Username='" + string + "'";
         try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next())
                return 1;
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        String query2="insert into Administrator (Username) values(?)";
        try (PreparedStatement ps2=conn.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps2.setString(1, string);
            ps2.executeUpdate();
            ResultSet rs3=ps2.getGeneratedKeys();
            if(rs3.next())
                return 0;
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
                return 2;
            }
        return 2;
    }

    @Override
    public Integer getSentPackages(String... strings) {
        Connection conn=DB.getInstance().getConnection();
        Integer counter = new Integer(0);
        
        
        for(int i = 0; i < strings.length; i++) {
            String query="SELECT NumSentPackets FROM [dbo].[Users] where Username='" + strings[i] + "'";
            try (
                PreparedStatement stmt=conn.prepareStatement(query);
                ResultSet rs=stmt.executeQuery()){
                
                if(rs.next()){
                    counter += rs.getInt("NumSentPackets");
                } else
                    return null;
                
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return counter;
    }

    @Override
    public int deleteUsers(String... strings) {
        Connection conn=DB.getInstance().getConnection();
        String query = "";
        int counter = 0;
        for(int i = 0; i < strings.length; i++){
            query="DELETE FROM [dbo].[Users] where Username = '" + strings[i] + "'";
            try (
                Statement stmt = conn.createStatement();)
                {
                int rc = stmt.executeUpdate(query);

                if(rc > 0) {
                   counter++;
                }
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return counter;

    }

    @Override
    public List<String> getAllUsers() {

        Connection conn=DB.getInstance().getConnection();
        List<String> users = new ArrayList<>();
        String query="SELECT Username FROM [dbo].[Users]";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                String uname = rs.getString("Username");
                users.add(uname);
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return users;
    }
    
}
