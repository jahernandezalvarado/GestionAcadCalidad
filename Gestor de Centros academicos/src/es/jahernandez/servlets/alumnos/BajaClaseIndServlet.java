/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.jahernandez.servlets.alumnos;

import es.jahernandez.accesodatos.ClasesIndivDAO;
import es.jahernandez.datos.ClasesIndivVO;
import es.jahernandez.datos.ConUsuVO;
import es.jahernandez.gestion.ClasesIndivGestion;

import java.io.IOException;
import java.util.GregorianCalendar;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.log4j.Logger;
/**
 *
 * @author JuanAlberto
 */
public class BajaClaseIndServlet extends HttpServlet 
{
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
        
        HttpSession   sesion    = request.getSession();
        
        ClasesIndivVO clasIndVO = new ClasesIndivVO();
        
        int           resBaja   = -99;

        Logger        log       = null;
        ConUsuVO      conUsoVO  = null;
        String        indPag    = "";   
        String        strFecha  = "";
        
        //Cargamos atributos de log
        if(sesion.getAttribute("logControl") != null && sesion.getAttribute("usuario") != null)
        {
            log = (Logger) sesion.getAttribute("logControl");
            conUsoVO = (ConUsuVO) sesion.getAttribute("usuario");
            
            log.info((conUsoVO.getUsuario() + "               " ).substring(0,10) + "Baja clase individual" );
               
        }
        
        // Se comprueba que se hayan pasado los parámetros y se inicializan valores
        if(request.getParameter("codClaseInd") != null)
        {
            clasIndVO.setIdClaseInd(request.getParameter("codClaseInd").trim());
        }
        
        if(request.getParameter("codInt") != null)
        {
            clasIndVO.setIdAlu(request.getParameter("codInt").trim());
        }
        
        if(request.getParameter("valInfClasInd") != null)
        {
            indPag = request.getParameter("valInfClasInd").trim();
        }
        
        if(request.getParameter("strFecha") != null)
        {
            strFecha = request.getParameter("strFecha").trim();
        }
        
        
        resBaja = ClasesIndivGestion.eliminaClaseInd(clasIndVO.getIdClaseInd());
        
        //Redireccionar a página de clases individuales
        response.sendRedirect("interesados/clasIndFichaAlumno.jsp?codInt="        + clasIndVO.getIdAlu() 
                                                              + "&valInfClasInd=" + indPag
                                                              + "&resultBor="     + resBaja
                                                              + "&strFecha="      + strFecha);
 
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
        return "Edición clase individual Servlet";
    }// </editor-fold>
}
