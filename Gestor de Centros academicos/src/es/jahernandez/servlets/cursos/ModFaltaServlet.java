/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.jahernandez.servlets.cursos;

import java.io.IOException;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import es.jahernandez.datos.ConUsuVO;
import es.jahernandez.datos.FaltasVO;
import es.jahernandez.gestion.FaltasGestion;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author JuanAlberto
 */
public class ModFaltaServlet extends HttpServlet  {

     /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        response.setCharacterEncoding("utf-8");
        request.setCharacterEncoding("utf-8");
        
        HttpSession      sesion     = request.getSession();
        
        FaltasVO         faltaVO    = new FaltasVO();
        int              resEdi     = 0;

        Logger           log        = null;
        ConUsuVO         conUsoVO   = null;
        String           indPag     = "";   
        boolean          pagPestana = false;
        
        //Cargamos atributos de log
        if(sesion.getAttribute("logControl") != null && sesion.getAttribute("usuario") != null)
        {
            log = (Logger) sesion.getAttribute("logControl");
            conUsoVO = (ConUsuVO) sesion.getAttribute("usuario");
            
            log.info((conUsoVO.getUsuario() + "               " ).substring(0,10) + "Modificación falta individual" );
               
        }
        
        // Se comprueba que se hayan pasado los parámetros y se inicializan valores
        if(request.getParameter("txtCodAlu") != null)
        {
            faltaVO.setIdAlu(request.getParameter("txtCodAlu").trim());
        }
        
        if(request.getParameter("txtCodEdi") != null)
        {
            faltaVO.setIdEdi(request.getParameter("txtCodEdi").trim());
        }
        
        if(request.getParameter("codMod") != null)
        {
            faltaVO.setIdMod(request.getParameter("codMod").trim());
        }
               
        if(request.getParameter("fecha") != null &&
          !request.getParameter("fecha").equals(""))
        {
            String strFechaFal = request.getParameter("fecha");
            faltaVO.setFecha(new GregorianCalendar(new Integer(strFechaFal.substring(6,10)).intValue(),
                                                   new Integer(strFechaFal.substring(3,5)).intValue() - 1,
                                                   new Integer(strFechaFal.substring(0,2)).intValue()).getTime());
        }
        
        if(request.getParameter("chkJust" + faltaVO.devolverClave()) != null &&
           request.getParameter("chkJust" + faltaVO.devolverClave()).trim().equals("true"))
        {
            faltaVO.setJustificada(true);
        }
                
        if(request.getParameter("pagPest") != null)
        {
            pagPestana = true;
        }
        
        if(request.getParameter("valInfFalta") != null)
        {
            indPag = request.getParameter("valInfFalta").trim();
        }
        
        resEdi = FaltasGestion.editarFalta(faltaVO);

        if(resEdi > 0)
        {
            //Redireccionar a página de clases individuales
            if(pagPestana)
            {
                response.sendRedirect("./interesados/faltasFichaAlumno.jsp?codInt="      + faltaVO.getIdAlu()
                                                                       + "&codEdi="      + faltaVO.getIdEdi()
                                                                       + "&valInfFalta=" + indPag );
            }
            else
            {
                response.sendRedirect("./ediciones/gestionFaltas.jsp?codInt="      + faltaVO.getIdAlu()
                                                                 + "&codEdi="      + faltaVO.getIdEdi()
                                                                 + "&valInfFalta=" + indPag );
            }
        }
        else
        {
            //Redireccionar a página de clases individuales
            if(pagPestana)
            {
                response.sendRedirect("./interesados/faltasFichaAlumno.jsp?codInt="      + faltaVO.getIdAlu()
                                                                       + "&codEdi="      + faltaVO.getIdEdi()
                                                                       + "&valInfFalta=" + indPag 
                                                                       + "&errorEdi="    + resEdi);
            }
            else
            {
                response.sendRedirect("./ediciones/gestionCalifi.jsp?codInt="      + faltaVO.getIdAlu()
                                                                 + "&codEdi="      + faltaVO.getIdEdi()
                                                                 + "&valInfFalta=" + indPag 
                                                                 + "&errorEdi="    + resEdi );
            }
        }    
    }
        

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() 
    {
        return "Modificación falta individual Servlet";
    }// </editor-fold>
}
