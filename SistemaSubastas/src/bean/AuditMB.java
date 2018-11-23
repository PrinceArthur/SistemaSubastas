package bean;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import dao.AuditDAO;
import dao.AuditDAOImpl;
import entity.Audit;
import entity.Salesueb;
import service.AuditService;
import service.SalesuebService;

/**
 * 
 * En está clase tenemos las listas de las auditorias
 * 
 * @author Guillermo Marcano, Richard Mora y Estefanía Pérez
 *
 */
@ManagedBean
@SessionScoped
public class AuditMB
{
	/**
	 * Auditoria
	 */
	private Audit auditoria;

	/**
	 * Lista de todas las auditorias
	 */
	private DataModel listaAudit;

	/**
	 * lista de las auditorias por usuario
	 */
	private List listaUsuario;

	/**
	 * Lista de auditorias por operación CRUD
	 */
	private List listaCrud;

	/**
	 * Lista filtrada por parámetro
	 */
	private List listaSubastasCreadas;

	/**
	 * Atributo para la operación del reporte
	 */
	private String operacion;

	/**
	 * Atributo para el usuario del reporte
	 */
	private String usu;

	/**
	 * Atributo para dar el mensaje en la capa de presentación
	 */
	private String mensajeError;

	private StreamedContent file;

	private Date inicio;
	private Date fin;

	/**
	 * Constructor de la clase
	 */
	public AuditMB()
	{
		auditoria = new Audit();
	}

	/**
	 * Método para agregar una auditoria
	 * 
	 * @param userName
	 * @param operationCrud
	 * @param tableName
	 * @param idUsuario
	 */
	public void adicionarAudit(String userName, String operationCrud, String tableName, int idUsuario)
	{
		AuditService service = new AuditService();
		Date date = new Date();
		auditoria.setCreateDate(date);
		auditoria.setUserName(userName);
		auditoria.setOperationCrud(operationCrud);
		auditoria.setTableId(idUsuario);
		auditoria.setTableName(tableName);
		try
		{
			auditoria.setAddressIP(InetAddress.getLocalHost().getHostAddress());
			AuditService audit = new AuditService();
			service.nuevo(auditoria);
		} catch (UnknownHostException e)
		{
		}
	}

	/**
	 * Método para generar archivo pdf del reeporte del CRUD
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void pdfSubastasCreadas() throws DocumentException, IOException
	{
		boolean generar = true;
		int dias = (int) ((fin.getTime() - inicio.getTime()) / 86400000);

		if (dias < 0)
		{
			generar = false;
			mensajeError = "La fecha de inicio debe ser menor que la fecha final";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}

		if (generar == true)
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in;

			Document document = new Document();

			PdfWriter.getInstance(document, out);

			Rectangle tamaño = PageSize.A4;
			document.setPageSize(tamaño);
			document.open();

			Font f = new Font();
			f.setStyle(Font.BOLDITALIC);
			f.setSize(30);
			f.setColor(244, 92, 66);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm:ss");
			String date = sdf.format(new Date());

			Paragraph tituloEmpresa = new Paragraph();
			tituloEmpresa.setFont(f);
			tituloEmpresa.add("ACME inc");
			document.add(tituloEmpresa);
			document.add(new Paragraph(date));
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			Font boldFont = new Font();
			boldFont.setStyle(Font.BOLD);
			boldFont.setSize(18);

			SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy");

			Paragraph titulo = new Paragraph();
			titulo.setFont(boldFont);
			titulo.add("REPORTE DE SUBASTAS CREADAS ENTRE " + format.format(inicio) + " - " + format.format(fin) + ".");
			titulo.setAlignment(Element.ALIGN_JUSTIFIED);
			titulo.setIndentationLeft(80);
			document.add(titulo);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			PdfPTable tableUsuario = new PdfPTable(7);
			addTableHeaderSubastasCreadas(tableUsuario);
			addRowsUserSubastasCreadas(tableUsuario);
			document.add(tableUsuario);
			document.close();

			in = new ByteArrayInputStream(out.toByteArray());
			file = new DefaultStreamedContent(in, "application/pdf", "ReporteDeSubastasCreadas.pdf");

			mensajeError = "Se ha generado el archivo correctamente.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}
	}

	/**
	 * Método para generar cabeceras en el reporte
	 * 
	 * @param table
	 */
	public void addTableHeaderSubastasCreadas(PdfPTable table)
	{
		Stream.of("ID", "Producto", "Descripción", "Valor base", "Valor ofertado", "Fecha inicio", "Fecha fin")
				.forEach(columnTitle ->
				{
					PdfPCell header = new PdfPCell();
					header.setBackgroundColor(BaseColor.LIGHT_GRAY);
					header.setBorderWidth(0);
					header.setHorizontalAlignment(Element.ALIGN_CENTER);
					header.setPhrase(new Phrase(columnTitle));
					table.addCell(header);
				});
	}

	/**
	 * Método para agegas las filas al reporte
	 * 
	 * @param table
	 */
	public void addRowsUserSubastasCreadas(PdfPTable table)
	{
		Iterator it = getListaSubastasCreadas().iterator();

		Salesueb x;

		while (it.hasNext())
		{
			x = (Salesueb) it.next();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell Name = new PdfPCell(new Phrase(x.getName()));
			PdfPCell descripcion = new PdfPCell(new Phrase("" + x.getDescriptionProduct()));
			PdfPCell base = new PdfPCell(new Phrase("" + x.getValueBase()));
			PdfPCell actual = new PdfPCell(new Phrase("" + x.getValueCurrent()));
			PdfPCell fIncio = new PdfPCell(new Phrase(x.getDateStart().toString()));
			PdfPCell fFin = new PdfPCell(new Phrase(x.getDateEnd().toString()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			Name.setBorder(Rectangle.NO_BORDER);
			Name.setHorizontalAlignment(Element.ALIGN_CENTER);
			descripcion.setBorder(Rectangle.NO_BORDER);
			descripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
			base.setBorder(Rectangle.NO_BORDER);
			base.setHorizontalAlignment(Element.ALIGN_CENTER);
			actual.setBorder(Rectangle.NO_BORDER);
			actual.setHorizontalAlignment(Element.ALIGN_CENTER);
			fIncio.setBorder(Rectangle.NO_BORDER);
			fIncio.setHorizontalAlignment(Element.ALIGN_CENTER);
			fFin.setBorder(Rectangle.NO_BORDER);
			fFin.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(Name);
			table.addCell(descripcion);
			table.addCell(base);
			table.addCell(actual);
			table.addCell(fIncio);
			table.addCell(fFin);
		}
	}

	/**
	 * Método para generar archivo excel del reporte de subastas creadas
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void archivoExcelSubastasCreadas() throws FileNotFoundException, DocumentException
	{
		boolean generar = true;
		int dias = (int) ((fin.getTime() - inicio.getTime()) / 86400000);

		if (dias < 0)
		{
			generar = false;
			mensajeError = "La fecha de inicio debe ser menor que la fecha final";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));
		}

		if (generar == true)
		{
			SalesuebService service = new SalesuebService();
			Iterator it = getListaSubastasCreadas().iterator();

			Salesueb x;
			HSSFWorkbook libro = new HSSFWorkbook();

			HSSFSheet hoja = libro.createSheet();

			HSSFRow fila = hoja.createRow(0);

			// Se crea una celda dentro de la fila
			HSSFCell id = fila.createCell((short) 0);
			HSSFCell name = fila.createCell((short) 1);
			HSSFCell descripcion = fila.createCell((short) 2);
			HSSFCell pro = fila.createCell((short) 3);
			HSSFCell base = fila.createCell((short) 4);
			HSSFCell current = fila.createCell((short) 5);
			HSSFCell fechaStar = fila.createCell((short) 6);
			HSSFCell fechaEnd = fila.createCell((short) 7);

			id.setCellValue(new HSSFRichTextString("ID"));
			name.setCellValue(new HSSFRichTextString("Producto"));
			descripcion.setCellValue(new HSSFRichTextString("Descripción"));
			pro.setCellValue(new HSSFRichTextString("Proveedor"));
			base.setCellValue(new HSSFRichTextString("Valor base"));
			current.setCellValue(new HSSFRichTextString("Valor ofertado"));
			fechaStar.setCellValue(new HSSFRichTextString("Fecha de inicio"));
			fechaEnd.setCellValue(new HSSFRichTextString("Fecha final"));
			int i = 0;
			while (it.hasNext())
			{
				HSSFRow dataRow = hoja.createRow(i + 1);
				x = (Salesueb) it.next();
				dataRow.createCell(0).setCellValue(x.getId());
				dataRow.createCell(1).setCellValue(x.getName());
				dataRow.createCell(2).setCellValue(x.getDescriptionProduct());
				dataRow.createCell(3).setCellValue(x.getIdentificationSales());
				dataRow.createCell(4).setCellValue(x.getValueBase());
				dataRow.createCell(5).setCellValue(x.getValueCurrent());
				dataRow.createCell(6).setCellValue(x.getDateStart().toString());
				dataRow.createCell(7).setCellValue(x.getDateEnd().toString());

				i++;
			}

			mensajeError = "Se ha generado el archivo correctamente.";
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

			try
			{
				FileOutputStream elFichero = new FileOutputStream(
						"C://ReportesGeneradosSistemaSubastas/ReporteSubastasCreadas.xls");
				libro.write(elFichero);
				Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/ReporteSubastasCreadas.xls"));
				elFichero.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	

	/**
	 * Dar la auditoria
	 * 
	 * @return auditoria
	 */
	public Audit getAuditoria()
	{
		return auditoria;
	}

	/**
	 * Modificar auditoria
	 * 
	 * @param auditoria
	 */
	public void setAuditoria(Audit auditoria)
	{
		this.auditoria = auditoria;
	}

	/**
	 * Dar lista de auditorias
	 * 
	 * @return listaAudit
	 */
	public DataModel getListarAuditorias()
	{
		List<Audit> lista = new AuditDAOImpl().lista();
		listaAudit = new ListDataModel(lista);
		return listaAudit;
	}

	/**
	 * Dar lista audorias por usuarios
	 * 
	 * @return listaUsuario
	 */
	public List getListaUsuario()
	{
		listaUsuario = new AuditService().auditUser(usu);
		return listaUsuario;
	}

	/**
	 * Modifica lista audorias por usuarios
	 * 
	 * @param listaUsuario
	 */
	public void setListaUsuario(List listaUsuario)
	{
		this.listaUsuario = listaUsuario;
	}

	/**
	 * Dar lista auditorias por CRUD
	 * 
	 * @return listaCrud
	 */
	public List getListaCrud()
	{
		listaCrud = new AuditService().auditCrud(operacion);
		return listaCrud;
	}

	/**
	 * Modifica lista auditorias por CRUD
	 * 
	 * @param listaCrud
	 */
	public void setListaCrud(List listaCrud)
	{
		this.listaCrud = listaCrud;
	}

	/**
	 * Dar el usuario
	 * 
	 * @return usu
	 */
	public String getUsu()
	{
		return usu;
	}

	/**
	 * Modifica el usuario
	 * 
	 * @param usu
	 */
	public void setUsu(String usu)
	{
		this.usu = usu;
	}

	/**
	 * Dar operacion CRUD
	 * 
	 * @return operacion
	 */
	public String getOperacion()
	{
		return operacion;
	}

	/**
	 * Modifica la operacion
	 * 
	 * @param operacion
	 */
	public void setOperacion(String operacion)
	{
		this.operacion = operacion;
	}

	/**
	 * Da el mensaje error
	 * 
	 * @return mensajeError
	 */
	public String getMensajeError()
	{
		return mensajeError;
	}

	/**
	 * Modifica el mensaje error
	 * 
	 * @param mensajeError
	 */
	public void setMensajeError(String mensajeError)
	{
		this.mensajeError = mensajeError;
	}

	public List inicializarSubastasCreadas()
	{
		listaSubastasCreadas = new ArrayList<>();
		SalesuebService service = new SalesuebService();
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
		List<Audit> listaFiltada = new AuditService().getAuditFilter("createDate BETWEEN '" + formato.format(inicio)
				+ "'AND '" + formato.format(fin) + "' AND operationCrud = 'CREAR' AND tableName = 'Salesueb'");

		Iterator<Audit> it = listaFiltada.iterator();

		while (it.hasNext())
		{
			Audit x = it.next();
			Salesueb elemento = service.listaSalesuebID(x.getTableId());
			listaSubastasCreadas.add(elemento);
		}

		return listaSubastasCreadas;
	}

	public List getListaSubastasCreadas()
	{
		listaSubastasCreadas = inicializarSubastasCreadas();

		return listaSubastasCreadas;
	}

	public void setListaSubastasCreadas(List listaFiltrada)
	{
		this.listaSubastasCreadas = listaFiltrada;
	}

	public Date getInicio()
	{
		return inicio;
	}

	public void setInicio(Date inicio)
	{
		this.inicio = inicio;
	}

	public Date getFin()
	{
		return fin;
	}

	public void setFin(Date fin)
	{
		this.fin = fin;
	}

	public StreamedContent getFile()
	{
		return file;
	}

	public void setFile(StreamedContent file)
	{
		this.file = file;
	}

}