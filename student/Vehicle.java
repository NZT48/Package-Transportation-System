package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;


public class Vehicle implements VehicleOperations {

    @Override
    public boolean insertVehicle(String string, int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        String query="insert into Vehicle (RegNum, FuelType, Consumption) values(?, ?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setBigDecimal(3, bd);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;

    }

    @Override
    public int deleteVehicles(String... strings) {
        Connection conn=DB.getInstance().getConnection();
        String query = "";
        int counter = 0;
        for(int i = 0; i < strings.length; i++){
            query="delete from Vehicle where RegNum ='" + strings[i] +"'";
            try (
                Statement stmt = conn.createStatement();)
                {
                int rc = stmt.executeUpdate(query);

                if(rc > 0) {
                   counter++;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return counter;

    }

    @Override
    public List<String> getAllVehichles() {
        Connection conn=DB.getInstance().getConnection();
        List<String> vehicles = new ArrayList<>();
        String query="select RegNum from Vehicle";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                String RegNum = rs.getString("RegNum");
                vehicles.add(RegNum);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return vehicles;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        
        Connection conn = DB.getInstance().getConnection();
        try(
		PreparedStatement stmt = conn.prepareStatement(
			"select * from Vehicle where RegNum='"+ string + "'", 
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery();
	) {

		while(rs.next()){
			rs.updateInt(2, i);
			rs.updateRow(); 
                        return true;
		}
                return false;
	} catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        
        Connection conn = DB.getInstance().getConnection();
        try(
		PreparedStatement stmt = conn.prepareStatement(
			"select * from Vehicle where RegNum='"+ string + "'", 
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery();
	) {

		while(rs.next()){
			rs.updateBigDecimal("Consumption", bd);
			rs.updateRow(); 
                        return true;
		}
                return false;
	} catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
}
