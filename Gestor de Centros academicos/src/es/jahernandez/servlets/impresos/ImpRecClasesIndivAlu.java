/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.jahernandez.servlets.impresos;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import es.jahernandez.accesodatos.*;
import es.jahernandez.datos.*;
import es.jahernandez.gestion.AlumnosGestion;
import es.jahernandez.gestion.ClasesIndivGestion;
import es.jahernandez.gestion.CursosGestion;
import es.jahernandez.gestion.ListaCodPostGestion;
import es.jahernandez.gestion.ProfesoresGestion;
import java.awt.Color;
import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import jakarta.servlet.ServletContext;
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
public class ImpRecClasesIndivAlu extends HttpServlet {
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        
        HttpSession    sesion       = request.getSession();
        ServletContext sc           = null;
        
        AlumnosVO      aluVO        = null;
        CursosVO       curVO        = null;
        ClasesIndivVO  clasInd      = null;
        
        int            mesFiltro    = 0;
        int            annoFiltro   = 0;
   
        String         codAlu       = "";
        String         fecFilt      = ""; 
        
        
        boolean        generaPag    = false;

        
        
        ConUsuVO       conUsVO      = new ConUsuVO();
           
        String         mesAc        = new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis()));   
        String         annoAc       = new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));  
        

        Date           fechaHoy     = new Date(System.currentTimeMillis());

        Vector         vecClasInd   = new Vector();

        int            numRecMes    = 0;
        
        conUsVO =(ConUsuVO) sesion.getAttribute("usuario");

        Logger         log      = null;
       
        //Cargamos atributos de log
        if(sesion.getAttribute("logControl") != null && sesion.getAttribute("usuario") != null)
        {
            log = (Logger) sesion.getAttribute("logControl");
            conUsVO = (ConUsuVO) sesion.getAttribute("usuario");
            
            log.info((conUsVO.getUsuario() + "               " ).substring(0,10) + "Imprimir recibo clases independientes mes alumno" );
               
        }
        
        
        if(request.getParameter("strFecha") != null)
        {    
            fecFilt = request.getParameter("strFecha").trim();
        }

        if(request.getParameter("codInt") != null)
        {
            codAlu     =  request.getParameter("codInt").trim();

            vecClasInd = ClasesIndivGestion.devolverClasesIndAluMes(codAlu, fecFilt);
            mesFiltro = new Integer(fecFilt.substring(0,2)).intValue();
            annoFiltro = new Integer(fecFilt.substring(2,6)).intValue();
        }
        
        // step 1
        // need to write to memory first due to IE wanting
        // to know the length of the pdf beforehand
        Document document = new Document();

        //Instrucciones para meter los datos de concepto en una tabla
        PdfPTable tablaImporte   = new PdfPTable(2);
        PdfPCell  cellLugarExp   = null;
        Paragraph parLugExp      = null;
        PdfPCell  cellImpTot     = null;
        Paragraph parImpTot      = null;
        
        PdfPTable tablaConcepto  = new PdfPTable(1);
        PdfPCell  cellConcepto   = null;
        Paragraph parTitConcep   = new Paragraph("CONCEPTO");
        Paragraph parDetConcepto = null;
        
        PdfPTable tablaDatAlu    = new PdfPTable(1);
        PdfPCell  cellDatosAlu   = null;
        Paragraph parTitDatAlu   = new Paragraph("NOMBRE Y DOMICILIO DEL LIBRADO");
        Paragraph parNomAlu      = null;
        Paragraph parDniAlu      = null;
        Paragraph parDirecAlu    = null;
        Paragraph parCPPobAlu    = null;
        Paragraph parProvAlu     = null;
        
        PdfPTable tablaDetClas   = new PdfPTable( new float[]{36f, 12f, 38f, 14f });
        PdfPCell  cellTit        = null;
        PdfPCell  cellLinea      = null;
        Paragraph parCurso       = null;
        Paragraph parFecha       = null;
        Paragraph parProfesor    = null;
        Paragraph parDetalle     = null;
        
        Paragraph parIma         = new Paragraph();
        Image     logoImage      = null; // iTextSharp.text.Image.GetInstance(System.Web.HttpContext.Current.Server.MapPath("~/imagenes/logoEnTeST.jpg"));

        
        try
        {
            sc = getServletContext();
            
            // step 2: we set the ContentType and create an instance of the Writer
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            
            // step 3
            document.setMargins(32, 32, 16, 0);
            document.open();

            aluVO = AlumnosGestion.devolverDatosAlumno(codAlu);
           
            logoImage = Image.getInstance(sc.getRealPath("/imagenes/"+ InformacionConf.logo));
            logoImage.scaleAbsolute(150, 38);
            
            parIma = new Paragraph();
            parIma.setAlignment(Image.ALIGN_LEFT);
            parIma.add(logoImage);

            //par14 = new Paragraph(InformacionConf.nombEmp + " " + InformacionConf.dirEmp + " CIF: " + InformacionConf.CIFEmp, new Font(BaseFont.createFont(sc.getRealPath("/" + "fonts" + "/" + "cour.ttf"), BaseFont.IDENTITY_H, BaseFont.EMBEDDED)));
            cellLugarExp = new PdfPCell();
            parLugExp    = new Paragraph("LUGAR DE EXPEDICION\n" + InformacionConf.pobEmp);
            cellLugarExp.addElement(parLugExp);
            
            cellImpTot   = new PdfPCell();
            parImpTot    = new Paragraph("IMPORTE TOTAL " + new DecimalFormat("######0.00").format(ClasesIndivGestion.devolverImporteClaseAluMes(codAlu, fecFilt))+ " €" );
            parImpTot.setAlignment(Image.ALIGN_RIGHT);
            cellImpTot.addElement(parImpTot);
            tablaImporte.addCell(cellLugarExp);
            tablaImporte.addCell(cellImpTot);
            
            cellConcepto = new PdfPCell();
            parDetConcepto = new Paragraph("Clases individuales del mes de " + devuelveMes(mesFiltro) ); 
            parTitConcep.setAlignment(Image.ALIGN_CENTER);
            cellConcepto.addElement(parTitConcep);
            cellConcepto.addElement(parDetConcepto);
            tablaConcepto.addCell(cellConcepto);
            
            cellDatosAlu = new PdfPCell();
            parNomAlu    = new Paragraph(aluVO.getNombre() + " " + aluVO.getAp1Alu() + " ");
            parDniAlu    = new Paragraph(aluVO.getNumDocAlu());
            parDirecAlu  = new Paragraph(aluVO.getDirAlu() + " ");
            parCPPobAlu  = new Paragraph(aluVO.getCodPosAlu()+ " " + aluVO.getLocalAlu() + " ");
            parProvAlu   = new Paragraph(ListaCodPostGestion.devuelveNombreProv(aluVO.getCodProvAlu())+ " " );
            cellDatosAlu.addElement(parTitDatAlu);
            cellDatosAlu.addElement(parNomAlu);
            cellDatosAlu.addElement(parDniAlu);
            cellDatosAlu.addElement(parDirecAlu);
            cellDatosAlu.addElement(parCPPobAlu);
            cellDatosAlu.addElement(parProvAlu);
            tablaDatAlu.addCell(cellDatosAlu);
            
            //Detalle clases
            //Añadimoe el detalle de las clases
            cellTit = new PdfPCell();
            parTitDatAlu = new Paragraph("CURSO");
            parTitDatAlu.setAlignment(Image.ALIGN_CENTER);
            parTitDatAlu.font().setSize(10);
            cellTit.addElement(parTitDatAlu);
            tablaDetClas.addCell(cellTit);
            cellTit = new PdfPCell();
            parTitDatAlu = new Paragraph("FECHA");
            parTitDatAlu.setAlignment(Image.ALIGN_CENTER);
            parTitDatAlu.font().setSize(10);
            cellTit.addElement(parTitDatAlu);
            tablaDetClas.addCell(cellTit);
            cellTit = new PdfPCell();
            parTitDatAlu = new Paragraph("PROFESOR");
            parTitDatAlu.setAlignment(Image.ALIGN_CENTER);
            parTitDatAlu.font().setSize(10);
            cellTit.addElement(parTitDatAlu);
            tablaDetClas.addCell(cellTit);
            cellTit = new PdfPCell();
            parTitDatAlu = new Paragraph("IMPORTE");
            parTitDatAlu.setAlignment(Image.ALIGN_CENTER);
            parTitDatAlu.font().setSize(10);
            cellTit.addElement(parTitDatAlu);
            tablaDetClas.addCell(cellTit);
            
            for(int ind=0; ind < vecClasInd.size() ; ind ++)
            {
                clasInd = (ClasesIndivVO) vecClasInd.elementAt(ind);
                
                cellLinea = new PdfPCell();
                parCurso = new Paragraph(CursosGestion.devolverDatosCurso(clasInd.getIdCur()).getNomCur());
                parCurso.setAlignment(Image.ALIGN_LEFT);
                parCurso.font().setSize(10);
                cellLinea.addElement(parCurso);
                tablaDetClas.addCell(cellLinea);
                cellLinea = new PdfPCell();
                parFecha = new Paragraph(new SimpleDateFormat("dd/MM/yyyy").format(clasInd.getFecClase()));
                parFecha.setAlignment(Image.ALIGN_LEFT);
                parFecha.font().setSize(10);
                cellLinea.addElement(parFecha);
                tablaDetClas.addCell(cellLinea);
                cellLinea = new PdfPCell();
                
                if(clasInd.getIdProf().equals("-1") || clasInd.getIdProf().trim().equals("") )
                {
                    parProfesor = new Paragraph(" ");
                }
                else
                {
                    parProfesor = new Paragraph(ProfesoresGestion.devolverDatosProfesor(clasInd.getIdProf()).getNombre() + " " + ProfesoresGestion.devolverDatosProfesor(clasInd.getIdProf()).getApellidos());
                }
                
                parProfesor.setAlignment(Image.ALIGN_LEFT);
                parProfesor.font().setSize(10);
                cellLinea.addElement(parProfesor);
                tablaDetClas.addCell(cellLinea);
                cellLinea = new PdfPCell();
                parDetalle = new Paragraph(new DecimalFormat("#######0.00").format(clasInd.getTarifa()));
                parDetalle.setAlignment(Image.ALIGN_RIGHT);
                parDetalle.font().setSize(10);
                cellLinea.addElement(parDetalle);
                tablaDetClas.addCell(cellLinea);
            }
            
            
            
            //par14.font().setSize(8);
            parIma = new Paragraph();
            parIma.setAlignment(Image.ALIGN_LEFT);
            parIma.add(logoImage);

            // step 4

            //Formateamos el ancho de las tablas
            tablaImporte.setWidthPercentage(100);
            tablaConcepto.setWidthPercentage(100);
            tablaDatAlu.setWidthPercentage(100);
            tablaDetClas.setWidthPercentage(100);

            document.add(parIma);
            document.add(new Paragraph(" "));
            document.add(tablaImporte);
            document.add(new Paragraph(" "));
            document.add(tablaConcepto);
            document.add(new Paragraph(" "));
            document.add(tablaDatAlu);
            document.add(new Paragraph(" "));
            document.add(tablaDetClas);
                    
        }
        catch (DocumentException ex)
        {
              ex.getCause();
        }
        // step 5: Close document
        
        
        document.close();

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
    public String getServletInfo() {
        return "Imprimir recibos clases independientes servlet";
    }// </editor-fold>
    
    private String devuelveMes(int mes)
    {
        switch (mes)
        {
            case 1:  return "Enero";
            case 2:  return "Febrero";
            case 3:  return "Marzo";
            case 4:  return "Abril";
            case 5:  return "Mayo";
            case 6:  return "Junio";
            case 7:  return "Julio";
            case 8:  return "Agosto";
            case 9:  return "Septiembre";
            case 10: return "Octubre";
            case 11: return "Noviembre";
            case 12: return "Diciembre";
            default: return null;  
        }
    }
    
}
