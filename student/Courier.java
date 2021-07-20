
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
import rs.etf.sab.operations.CourierOperations;


public class Courier implements CourierOperations {
    
    private class CustomPair {
        private String courier;
        private BigDecimal profit;

        public String getCourier() {
            return courier;
        }

        public void setCourier(String courier) {
            this.courier = courier;
        }

        public BigDecimal getProfit() {
            return profit;
        }

        public void setProfit(BigDecimal profit) {
            this.profit = profit;
        }

        
    }

    @Override
    public boolean insertCourier(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        String query="insert into Courier (Username, RegNum) values(?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setString(2, string1);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

    @Override
    public boolean deleteCourier(String string) {
        Connection conn=DB.getInstance().getConnection();
        String query="delete from Courier where Username='" + string + "'";
        try (
            Statement stmt = conn.createStatement();)
            {
            int rc = stmt.executeUpdate(query);
            
            if(rc > 0) {
               return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        Connection conn=DB.getInstance().getConnection();
        List<String> couriers = new ArrayList<>();
        String query="select Username from Courier where Status=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                String courier = rs.getString("Username");
                couriers.add(courier);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return couriers;
    }
    

    @Override
    public List<String> getAllCouriers() {
        Connection conn=DB.getInstance().getConnection();
        List<CustomPair> couriers = new ArrayList<>();
        
        String query="select Username, Profit from Courier";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                CustomPair courier = new CustomPair();
                courier.setCourier(rs.getString("Username"));
                courier.setProfit(rs.getBigDecimal("Profit"));
                couriers.add(courier);
            }
        } catch (SQLException ex) {
            
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        List<String> courierNames = new ArrayList<>();
        
        couriers.sort((CustomPair o1, CustomPair o2) -> o1.getProfit().compareTo(o2.getProfit()));
        
        for(int i = couriers.size()-1; i >= 0 ; i--) {
            CustomPair cour = couriers.get(i);
            courierNames.add(cour.getCourier());
        }
        
        return courierNames;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {

        Connection conn=DB.getInstance().getConnection();
        BigDecimal allProfit = BigDecimal.ZERO;
        BigDecimal numOfCouriers = BigDecimal.ZERO;
        String query="select Profit from Courier where NumDeliveredPackets>=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                BigDecimal prof = rs.getBigDecimal(1);
                numOfCouriers = numOfCouriers.add(BigDecimal.ONE);
                allProfit = allProfit.add(prof);
            }
                        
            if(numOfCouriers.compareTo(BigDecimal.ZERO) != 0)
                return allProfit.divide(numOfCouriers);
                
        } catch (SQLException ex) {
            Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return BigDecimal.ZERO;
    }
    
}
