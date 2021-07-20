package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;


public class Package implements PackageOperations {

    @Override
    public int insertPackage(int i, int i1, String string, int i2, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        String query="insert into Packet (DistrictIdFrom, DistrictIdTo, Username, Type, Weight) values(?, ?, ?, ?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, i);
            ps.setInt(2, i1);
            ps.setString(3, string);
            ps.setInt(4, i2);
            ps.setBigDecimal(5, bd);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next())
                return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return -1;
    }

    @Override
    public int insertTransportOffer(String string, int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();

        
        String querySelectCourier="select Status from Courier where Username='" + string + "'";
        try (
            PreparedStatement stmt=conn.prepareStatement(querySelectCourier);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next()){
                int status = rs.getInt("Status");
                if(status == 1)
                    return -1;
            } else
                return -1;
        } catch (SQLException ex) {
            Logger.getLogger(Courier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BigDecimal pricePercentage = bd;
        if(bd == null) {
            Random rand = new Random();
            double price = rand.nextDouble() * 100;
            pricePercentage = new BigDecimal(price);
        }
        
        String query="insert into OfferToTransport (Username, PacketId, PricePercent) values( ?, ?, ?)";
        try (PreparedStatement ps=conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, string);
            ps.setInt(2, i);
            ps.setBigDecimal(3, pricePercentage);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return -1;
    }

    @Override
    public boolean acceptAnOffer(int i) {
        Connection conn=DB.getInstance().getConnection();
        String query="select * from OfferToTransport where OfferId=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next()){
                int PacketId = rs.getInt("PacketId");
                String courierUsername = rs.getString("Username");
                BigDecimal pricePercent = rs.getBigDecimal("PricePercent");
                
                Date acceptanceTime = new Date(System.currentTimeMillis());
                try(
		PreparedStatement stmtPacket = conn.prepareStatement(
			"select * from Packet where PacketId="+ PacketId, 
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
		ResultSet rsPacket = stmtPacket.executeQuery();
                ) {

                    if(rsPacket.next()){
                        int districtIdFrom = rsPacket.getInt("districtIdFrom");
                        int districtIdTo = rsPacket.getInt("districtIdTo");
                        int type = rsPacket.getInt("type");
                        BigDecimal weight = rsPacket.getBigDecimal("Weight");
                        double distance = getDistanceBetweenDistricts(districtIdFrom, districtIdTo);
                        BigDecimal cost = Util.getPackagePrice(type, weight, distance, pricePercent);
                        rsPacket.updateDate("AcceptanceTime",acceptanceTime);
                        rsPacket.updateBigDecimal("Cost", cost);
                        rsPacket.updateInt("status", 1);
                        rsPacket.updateString("Username", courierUsername);
                        rsPacket.updateRow();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                String query1="delete from OfferToTransport where OfferId=" + i;
                try (
                    Statement stmt1 = conn.createStatement();)
                    {
                    int rc = stmt1.executeUpdate(query1);
                    
                    if(rc == 0) {
                       return false;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
                
                String query2="insert into Transport (Username, PacketId) values (?, ?)";
                try (PreparedStatement ps=conn.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS)){
                    ps.setString(1, courierUsername);
                    ps.setInt(2, PacketId);
                    ps.executeUpdate();
                    ResultSet rs2=ps.getGeneratedKeys();
                    if(rs.next()) {
                        return true;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return false;
    }

    @Override
    public List<Integer> getAllOffers() {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> offers = new ArrayList<>();
        String query="select OfferId from OfferToTransport";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                Integer offer = rs.getInt("OfferId");
                offers.add(offer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return offers;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int i) {
        Connection conn=DB.getInstance().getConnection();
        List<Pair<Integer, BigDecimal>> offers = new ArrayList<>();
        String query="select * from OfferToTransport where PacketId=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                Integer offerId = rs.getInt("OfferId");
                BigDecimal percent = rs.getBigDecimal("PricePercent");
                com.sun.tools.javac.util.Pair<Integer, BigDecimal> offer = new com.sun.tools.javac.util.Pair<>(offerId, percent);
                offers.add((Pair<Integer, BigDecimal>) offer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return offers;
    }

    @Override
    public boolean deletePackage(int i) {
        Connection conn=DB.getInstance().getConnection();
        String query="delete from Packet where CityId = " + i;
        try (
            Statement stmt = conn.createStatement();)
            {
            int rc = stmt.executeUpdate(query);
            
            if(rc > 0) {
               return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return false;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        try(
		PreparedStatement stmt = conn.prepareStatement(
			"select * from Packet where PacketId="+ i,
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery();
	) {

		while(rs.next()){
			rs.updateBigDecimal("Weight", bd);
			rs.updateRow(); // menjamo red u tabeli
                        return true;
		}
                return false;
	} catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean changeType(int i, int i1) {
        Connection conn = DB.getInstance().getConnection();
        try(
		PreparedStatement stmt = conn.prepareStatement(
			"select * from Packet where PacketId="+ i,
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery();
	) {

		while(rs.next()){
			rs.updateInt("Type", i1);
			rs.updateRow(); // menjamo red u tabeli
                        return true;
		}
                return false;
	} catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Integer getDeliveryStatus(int i) {
        Connection conn=DB.getInstance().getConnection();
        String query="select Status from Packet where PacketId=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                return rs.getInt("Status");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        Connection conn=DB.getInstance().getConnection();
        String query="select Cost from Packet where PacketId=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next()){
                return rs.getBigDecimal("Cost");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public Date getAcceptanceTime(int i) {
        Connection conn=DB.getInstance().getConnection();
        String query="select AcceptanceTime from Packet where PacketId=" + i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                return rs.getDate("AcceptanceTime");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> packages = new ArrayList<>();
        String query="select PacketId from Packet where Type="+i;
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                int packet = rs.getInt("PacketId");
                packages.add(packet);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return packages;
    }

    @Override
    public List<Integer> getAllPackages() {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> packages = new ArrayList<>();
        String query="select PacketId from Packet";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                int packet = rs.getInt("PacketId");
                packages.add(packet);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return packages;
    }

    @Override
    public List<Integer> getDrive(String string) {
        Connection conn=DB.getInstance().getConnection();
        List<Integer> packages = new ArrayList<>();
        String query="select PacketId from Transport where Username='" + string + "'";
        try (
            PreparedStatement stmt=conn.prepareStatement(query);
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                int packet = rs.getInt("PacketId");
                String innerQuery="select status from Packet where PacketId=" + packet;
                PreparedStatement innerStmt=conn.prepareStatement(innerQuery);
                ResultSet rsInner=innerStmt.executeQuery();
                if(rsInner.next()){
                    int status = rsInner.getInt("status");
                    if(status > 0 && status < 3){
                        packages.add(packet);  
                    }
                }          
            }
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if(packages.isEmpty()) return null;
        
        return packages;
    }

    @Override
    public int driveNextPackage(String string) {
        
        Connection conn=DB.getInstance().getConnection();
        int status = 0;
        int numOfDelivered = 0;
        String courierName = string;
        BigDecimal profit = BigDecimal.ZERO;

        String queryCourier="select * from Courier where Username='" + courierName + "'";
        try (
            PreparedStatement stmt=conn.prepareStatement(queryCourier, 
			ResultSet.TYPE_FORWARD_ONLY, 
			ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=stmt.executeQuery()){
            if(rs.next()){
                status = rs.getInt("Status");
                profit = rs.getBigDecimal("Profit");
                numOfDelivered = rs.getInt("NumDeliveredPackets");
                int firstPacketId = -1;
                List<Integer> packetsToDeliver = this.getDrive(courierName);
                String queryUpdatePacketStatus = "select * From Packet where PacketId=";

                
                // First packet picked up
                if(status == 0){
                    rs.updateInt("Status", 1);
                    // For every packet in transport Set status of all other packets to delivering (2)
                    
                    for(int i = 0; i < packetsToDeliver.size(); i++) {

                        try(
                            PreparedStatement stmtUpd=conn.prepareStatement(queryUpdatePacketStatus + packetsToDeliver.get(i), 
                                ResultSet.TYPE_FORWARD_ONLY, 
                                ResultSet.CONCUR_UPDATABLE);
                            ResultSet rsUpd = stmtUpd.executeQuery();
                        ){
                            while(rsUpd.next()){
                                if(firstPacketId == -1){
                                    firstPacketId = packetsToDeliver.get(i);
                                    rsUpd.updateInt("status", 3);
                                } else {
                                    rsUpd.updateInt("status", 2);
                                }
                                rsUpd.updateRow();
                                
                            }

                        } catch (SQLException ex) {
                            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    rs.updateInt("NumDeliveredPackets", numOfDelivered+1);
                    rs.updateRow();
                    return firstPacketId;
                    
                } else { 
                    
                    if(packetsToDeliver.isEmpty()) // All packets are delivered
                    {
                        rs.updateInt("Status", 0);
                        return -1;

                    } else {
                        try(
                            PreparedStatement stmtUpd=conn.prepareStatement(queryUpdatePacketStatus + packetsToDeliver.get(0), 
                                ResultSet.TYPE_FORWARD_ONLY, 
                                ResultSet.CONCUR_UPDATABLE);
                            ResultSet rsUpd = stmtUpd.executeQuery();
                        ){
                            // update its status to delivered
                            if(rsUpd.next()){
                                rsUpd.updateInt("status", 3);
                                rsUpd.updateRow();
                            }

                        } catch (SQLException ex) {
                            
                            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
                            return -2;
                        }
                        
                        // last packet is delivered
                        if (packetsToDeliver.size() == 1) { 
                        // get all delivered packets
                            List<Integer> deliveredPackets = new ArrayList<>();
                            String deliveredQuery="select PacketID from Transport where Username='" + string + "'";
                            try (
                                PreparedStatement stmt1=conn.prepareStatement(deliveredQuery);
                                ResultSet rs1=stmt1.executeQuery()){
                                while(rs1.next()){
                                    int packet = rs1.getInt(1);
                                    deliveredPackets.add(packet);                
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            // Calculate gain
                            boolean lastOne = false;
                            for(Integer pId: deliveredPackets) {
                                // Select packet with this id
                                // get gain from packetId
                                if(Objects.equals(pId, deliveredPackets.get(deliveredPackets.size()-1)))
                                    lastOne = true;
                                BigDecimal newProfit = getProfitFromPacketId(pId, courierName, lastOne);

                                profit = profit.add(newProfit);

                            }
                            

                             // add gain to our courir
                             BigDecimal oldProfit = rs.getBigDecimal("Profit");

                             rs.updateBigDecimal("Profit", oldProfit.add(profit));

                            // delete delivered packets by this courier
                            String deleteQuery="delete from Transport where Username='" + courierName +"'";
                            try (
                                Statement stmt3 = conn.createStatement();)
                                {
                                int rc = stmt3.executeUpdate(deleteQuery);


                            } catch (SQLException ex) {
                                Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                            }                     
                        
                        }

                    }
                    
                    rs.updateInt("NumDeliveredPackets", numOfDelivered+1);
                    rs.updateRow();
                    return packetsToDeliver.get(0);
                }
                
            } else
                return -2;
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        }
        
    }
    
    private BigDecimal getProfitFromPacketId(int packetId, String courierName, boolean lastOne) {
        Connection conn=DB.getInstance().getConnection();
        BigDecimal profit = BigDecimal.ZERO;
        
        String queryGetPacketProfit = "select * From Packet where PacketId="+packetId;

        try(
            PreparedStatement stmt=conn.prepareStatement(queryGetPacketProfit, 
                ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery();
        ){
            if(rs.next()){
                BigDecimal cost = rs.getBigDecimal("Cost");
                int districtIdFrom = rs.getInt("DistrictIdFrom");
                int districtIdTo = rs.getInt("DistrictIdTo");
                
                double distanceBetweenDistricts = getDistanceBetweenDistricts(districtIdFrom, districtIdTo);
                
                // Get courier vehicle to get its fuel consumption
                String RegNum = "";
                String queryVehicleIdForCourier="select RegNum from Courier where Username='" + courierName + "'";
                try (
                    PreparedStatement stmt2=conn.prepareStatement(queryVehicleIdForCourier);
                    ResultSet rs2=stmt2.executeQuery()){
                    while(rs2.next()){
                        RegNum = rs2.getString(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                }
                int fuelType = -1;
                BigDecimal consumption = BigDecimal.ONE;
                String queryVehicleParams="select * from Vehicle where RegNum='" + RegNum + "'";
                try (
                    PreparedStatement stmt3=conn.prepareStatement(queryVehicleParams);
                    ResultSet rs3=stmt3.executeQuery()){
                    while(rs3.next()){
                        fuelType = rs3.getInt("FuelType");
                        consumption = rs3.getBigDecimal("Consumption");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Vehicle.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // Calculate travelCost
                BigDecimal travelCost = calculateTravelCost(fuelType, consumption, distanceBetweenDistricts);

                if(!lastOne)
                    return cost.subtract(travelCost);
                else
                    return cost.subtract(travelCost).subtract(travelCost);
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return profit;
    }

    private double getDistanceBetweenDistricts(int districtIdFrom, int districtIdTo) {
        Connection conn=DB.getInstance().getConnection();

        int x1 = 0, y1 = 0;
        int x2 = 0, y2 = 0;
                        
        String districtQuery1="SELECT X,Y FROM dbo.[District] where DistrictId="+districtIdFrom;
        try (
            PreparedStatement stmt1=conn.prepareStatement(districtQuery1);
            ResultSet rs1=stmt1.executeQuery()){
            while(rs1.next()){
                x1 = rs1.getInt(1);
                y1 = rs1.getInt(2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
        }
                        
        String districtQuery2="SELECT X,Y FROM dbo.[District] where DistrictId="+districtIdTo;
        try (
            PreparedStatement stmt2=conn.prepareStatement(districtQuery2);
            ResultSet rs2=stmt2.executeQuery()){
            while(rs2.next()){
                x2 = rs2.getInt(1);
                y2 = rs2.getInt(2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(District.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return  Util.euclidean(x1, y1, x2, y2);
        
    }

    private BigDecimal calculateTravelCost(int fuelType, BigDecimal consumption, double distanceBetweenDistricts) {
        switch (fuelType) {
            case 0: {
                return new BigDecimal(15.0 * distanceBetweenDistricts).multiply(consumption);
            }
            case 1: {
                return new BigDecimal(36.0 * distanceBetweenDistricts).multiply(consumption);
            }
            case 2: {
                return new BigDecimal(32.0 * distanceBetweenDistricts).multiply(consumption);
            }
            default: {
                return null;
            }
        }
    }
    
}
