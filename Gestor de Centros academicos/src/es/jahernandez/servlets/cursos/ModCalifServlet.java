/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.jahernandez.servlets.cursos;

import es.jahernandez.accesodatos.CalificacionesDAO;
import es.jahernandez.datos.CalificacionesVO;
import es.jahernandez.datos.ConUsuVO;
import es.jahernandez.gestion.CalificacionesGestion;

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
public class ModCalifServlet extends HttpServlet {

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
        
        CalificacionesVO califVO    = new CalificacionesVO();
        int              resEdi     = 0;

        Logger           log        = null;
        ConUsuVO         conUsoVO   = null;
        String           indPag     = "";   
        String           strFecha   = "";
        int              cargaEva   = -99;
        boolean          pagPestana = false;
        
        //Cargamos atributos de log
        if(sesion.getAttribute("logControl") != null && sesion.getAttribute("usuario") != null)
        {
            log = (Logger) sesion.getAttribute("logControl");
            conUsoVO = (ConUsuVO) sesion.getAttribute("usuario");
            
            log.info((conUsoVO.getUsuario() + "               " ).substring(0,10) + "Modificación calificación individual" );
               
        }
        
        // Se comprueba que se hayan pasado los parámetros y se inicializan valores
        if(request.getParameter("txtCodAlu") != null)
        {
            califVO.setIdAlu(request.getParameter("txtCodAlu").trim());
        }
        
        if(request.getParameter("txtCodEdi") != null)
        {
            califVO.setIdEdi(request.getParameter("txtCodEdi").trim());
        }
        
        if(request.getParameter("codMod") != null)
        {
            califVO.setIdMod(request.getParameter("codMod").trim());
        }
        
        if(request.getParameter("eva") != null)
        {
            califVO.setEvaluacion(new Integer(request.getParameter("eva").trim()).intValue());
        }
                
        if(request.getParameter("hidFecha" + califVO.devolverClave()) != null)
        {
            if (! request.getParameter("hidFecha" + califVO.devolverClave()).equals(""))
            {
                String strFechaCal = request.getParameter("hidFecha" + califVO.devolverClave());
                califVO.setFecha(new GregorianCalendar(new Integer(strFechaCal.substring(6,10)).intValue(),
                                                       new Integer(strFechaCal.substring(3,5)).intValue() - 1,
                                                       new Integer(strFechaCal.substring(0,2)).intValue()).getTime());
            }
        }
        
        if(request.getParameter("lstNota" + califVO.devolverClave()) != null)
        {
            califVO.setNota(new Integer(request.getParameter("lstNota" + califVO.devolverClave() ).trim()).intValue());
        }
        
        if(request.getParameter("feva") != null)
        {
            cargaEva = new Integer(request.getParameter("feva").trim()).intValue();
        }
        
        if(request.getParameter("pagPest") != null)
        {
            pagPestana = true;
        }
        
        if(request.getParameter("valInfCalif") != null)
        {
            indPag = request.getParameter("valInfCalif").trim();
        }
        
        resEdi = CalificacionesGestion.editarCalificacion(califVO);

        if(resEdi > 0)
        {
            //Redireccionar a página de clases individuales
            if(pagPestana)
            {
                response.sendRedirect("./interesados/califFichaAlumno.jsp?codInt=" + califVO.getIdAlu()
                                                                 + "&codEdi="      + califVO.getIdEdi()
                                                                 + "&eva="         + cargaEva 
                                                                 + "&valInfCalif=" + indPag );
            }
            else
            {
                response.sendRedirect("./ediciones/gestionCalifi.jsp?codInt="      + califVO.getIdAlu()
                                                                 + "&codEdi="      + califVO.getIdEdi()
                                                                 + "&valInfCalif=" + indPag );
            }
        }
        else
        {
            //Redireccionar a página de clases individuales
            if(pagPestana)
            {
                response.sendRedirect("./interesados/califFichaAlumno.jsp?codInt=" + califVO.getIdAlu()
                                                               + "&codEdi="        + califVO.getIdEdi()
                                                               + "&eva="           + cargaEva 
                                                               + "&valInfCalif="   + indPag 
                                                               + "&errorEdi="      + resEdi);
            }
            else
            {
                response.sendRedirect("./ediciones/gestionCalifi.jsp?codInt=" + califVO.getIdAlu()
                                                          + "&codEdi="        + califVO.getIdEdi()
                                                          + "&valInfCalif="   + indPag 
                                                          + "&errorEdi="      + resEdi);
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
        return "Modificación calificación individual Servlet";
    }// </editor-fold>
}
