/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.jahernandez.accesodatos;

import es.jahernandez.datos.Conexion;
import es.jahernandez.datos.CurNivVO;
import es.jahernandez.tablas.TablaActividades;
import es.jahernandez.tablas.TablaBancos;

import es.jahernandez.tablas.TablaCursoNiveles;
import es.jahernandez.tablas.TablaNiveles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;

/**
 *
 * @author JuanAlberto
 */
public class BancosDAO 
{
    
     public static String devolverNombreBanco(String codBan, Connection con) throws Exception
     {
        PreparedStatement ps     = null; 
        ResultSet         rs     = null; 

        String            sql    = "SELECT " + TablaBancos.NOMBRE   + 
                                   " FROM "  + TablaBancos.TABLA    +
                                   " WHERE " + TablaBancos.CODBANCO + " = ? "; 
                                             
         
        int               regAct = 0;
        String            nomBan = "";
       
        try
        {
            ps  = con.prepareStatement(sql);

            //Se pasan los parámetros a la consulta sql
            ps.setString(1, codBan);

            rs = ps.executeQuery();

            if (rs.next()) //Existe
            {
                nomBan = rs.getString(TablaBancos.NOMBRE);
            }
           
            rs.close();
            ps.close();

            return nomBan;
        }
        catch (Exception exc)
        {
            try
            {
                rs.close();
                ps.close();
            }
            catch (SQLException ex)
            {
                Logger.getLogger(BancosDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw exc;
        }
    
     }
    
}
