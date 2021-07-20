package student;

import java.sql.Connection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

public class General implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection conn=DB.getInstance().getConnection();

        try(Statement stmt = conn.createStatement();) {	
            String sql1 = "EXEC sys.sp_msforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL' ";
            stmt.executeUpdate(sql1);
            String sql2 = "EXEC sys.sp_msforeachtable 'delete from ?'";
            stmt.executeUpdate(sql2);
            String sql3 = "EXEC sys.sp_MSForEachTable 'ALTER TABLE ? CHECK CONSTRAINT ALL'";
            stmt.executeUpdate(sql3);
        } catch (SQLException e) {
           Logger.getLogger(General.class.getName()).log(Level.SEVERE, null, e);
        } 
                
    }

        
}
