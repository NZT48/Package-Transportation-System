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
import rs.etf.sab.operations.CityOperations;

public class City implements CityOperations {

    @Override
    public int insertCity(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        
        String selectByNameQuery="select CityId from City where Name='"+ string + "'";
        try (
            PreparedStatement stmt=conn.prepareStatement(selectByNameQuery);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next()){
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        String selectByPostalCode="select CityId from City where PostalCode='"+ string1 + "'";
        try (
            PreparedStatement stmt=conn.prepareStatement(selectByPostalCode);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next()){
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        String query="insert into City (Name, PostalCode) values(?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int deleteCity(String... strings) {
        Connection conn=DB.getInstance().getConnection();
        String query = "";
        int counter = 0;
        for(int i = 0; i < strings.length; i++){
            
            String selectByNameQuery="select CityId from City where Name='"+  strings[i]  + "'";
            try (
                PreparedStatement stmt=conn.prepareStatement(selectByNameQuery);
                ResultSet rs=stmt.executeQuery()){
                if(!rs.next()){
                    continue;
                }
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            query="delete from City where Name = '" + strings[i] + "'";
            try (
                Statement stmt = conn.createStatement();)
                {
                int rc = stmt.executeUpdate(query);

                if(rc > 0) {
                   counter++;
                }
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return counter;
    }

    @Override
    public boolean deleteCity(int i) {
        Connection conn=DB.getInstance().getConnection();

        
        String selectByIdQuery="select CityId from City where CityId="+ i;
        try (
            PreparedStatement stmt=conn.prepareStatement(selectByIdQuery);
            ResultSet rs=stmt.executeQuery()){
            if(!rs.next()){
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String query="delete from City where CityId = " + i;
        try (
            Statement stmt = conn.createStatement();)
            {
            int rc = stmt.executeUpdate(query);
            
            if(rc > 0) {
               return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> cities = new ArrayList<>();
        String query="select CityId from City";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                int CityId = rs.getInt("CityId");
                cities.add(CityId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cities;
    }
    
}
