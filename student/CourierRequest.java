package student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

public class CourierRequest implements CourierRequestOperation {

    @Override
    public boolean insertCourierRequest(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        String query="insert into CourierRequest (Username, RegNum) values(?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
        
    }

    @Override
    public boolean deleteCourierRequest(String string) {
        Connection conn=DB.getInstance().getConnection();
        String query="delete from CourierRequest where Username='" + string + "'";
        try (
            Statement stmt = conn.createStatement();)
            {
            int rc = stmt.executeUpdate(query);
            
            if(rc > 0) {
               return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;

    }

    @Override
    public boolean changeVehicleInCourierRequest(String string, String string1) {
        
        Connection conn = DB.getInstance().getConnection();
        try(
		PreparedStatement stmt = conn.prepareStatement(
			"select * from CourierRequest where Username='"+ string + "'", 
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery();
	) {

		while(rs.next()){
			rs.updateString(2, string1);
			rs.updateRow();
                        return true;
		}
                return false;
	} catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    
    }

    @Override
    public List<String> getAllCourierRequests() {
        Connection conn=DB.getInstance().getConnection();
        List<String> potentialCouriers = new ArrayList<>();
        String query="select Username from CourierRequest";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                String courier = rs.getString("Username");
                potentialCouriers.add(courier);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return potentialCouriers;
    }

    @Override
    public boolean grantRequest(String string) {
        
        Connection conn=DB.getInstance().getConnection();
        String query = "{ call [dbo].[spGRANT_COURIER_REQUEST] (?, ?) }";
        
        try (
            CallableStatement proc = conn.prepareCall(query);
                ){
            proc.setString(1, string);
            proc.registerOutParameter(2, Types.INTEGER);
            proc.execute();
            int returnValue = proc.getInt(2);
            if(returnValue == 0)
                return false;
            else return true;
        } catch (SQLException ex) {
            return false;
        }
    

    }
    
}
