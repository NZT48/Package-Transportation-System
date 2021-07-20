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
import rs.etf.sab.operations.DistrictOperations;

public class District implements DistrictOperations {

    @Override
    public int insertDistrict(String string, int i, int i1, int i2) {

        Connection conn = DB.getInstance().getConnection();
        String query="insert into District (Name, CityId, x, y) values(?, ?, ?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setInt(3, i1);
            ps.setInt(4, i2);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return -1;
    }

    @Override
    public int deleteDistricts(String... strings) {
        Connection conn=DB.getInstance().getConnection();
        String query = "";
        int counter = 0;
        for(int i = 0; i < strings.length; i++){
            query="delete from District where Name = '" + strings[i] + "'";
            try (
                Statement stmt = conn.createStatement();)
                {
                int rc = stmt.executeUpdate(query);

                if(rc > 0) {
                   counter++;
                }
            } catch (SQLException ex) {
                Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return counter;

    }

    @Override
    public boolean deleteDistrict(int i) {
        Connection conn=DB.getInstance().getConnection();
        String query="delete from District where DistrictId = " + i;
        try (
            Statement stmt = conn.createStatement();)
            {
            int rc = stmt.executeUpdate(query);
            
            if(rc > 0) {
               return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

    @Override
    public int deleteAllDistrictsFromCity(String string) {
        Connection conn=DB.getInstance().getConnection();
        
        String getCityIdQuery="select CityId from City where Name ='" + string +"'";
        int CityId = -1;
        try (
            PreparedStatement stmt=conn.prepareStatement(getCityIdQuery);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                CityId = rs.getInt("CityId");
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        String deleteDistrictQuery= "delete from District where District.CityId=" + CityId;
        try (
            Statement stmt = conn.createStatement();)
            {
            int rc = stmt.executeUpdate(deleteDistrictQuery);
            
            return rc;
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int i) {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> districts = new ArrayList<>();
        String query="select DistrictId from District where District.CityId="+i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                int DistrictId = rs.getInt("DistrictId");
                districts.add(DistrictId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(districts.isEmpty()) return null;
        return districts;  

    }

    @Override
    public List<Integer> getAllDistricts() {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> districts = new ArrayList<>();
        String query="select DistrictId from District";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                int DistrictId = rs.getInt("DistrictId");
                districts.add(DistrictId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return districts;    
    }
    
}