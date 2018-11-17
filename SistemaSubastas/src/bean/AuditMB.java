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
	 * Método para generar archivo de Excel para reporte de CRUD
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void archivoExcelPorCrud() throws FileNotFoundException, DocumentException
	{
		AuditService service = new AuditService();

		List<Audit> audit = service.auditCrud(operacion);

		HSSFWorkbook libro = new HSSFWorkbook();

		HSSFSheet hoja = libro.createSheet();

		HSSFRow fila = hoja.createRow(0);

		HSSFCell id = fila.createCell((short) 0);
		HSSFCell operationCrud = fila.createCell((short) 1);
		HSSFCell tableName = fila.createCell((short) 2);
		HSSFCell tableId = fila.createCell((short) 3);
		HSSFCell createDate = fila.createCell((short) 4);
		HSSFCell addressIP = fila.createCell((short) 5);

		id.setCellValue(new HSSFRichTextString("ID"));
		operationCrud.setCellValue(new HSSFRichTextString("OperationCrud"));
		tableName.setCellValue(new HSSFRichTextString("TableName"));
		tableId.setCellValue(new HSSFRichTextString("TableId"));
		createDate.setCellValue(new HSSFRichTextString("CreateDate"));
		addressIP.setCellValue(new HSSFRichTextString("AddressIP"));

		for (int i = 0; i < audit.size(); ++i)
		{
			HSSFRow dataRow = hoja.createRow(i + 1);

			dataRow.createCell(0).setCellValue(audit.get(i).getId());
			dataRow.createCell(1).setCellValue(audit.get(i).getOperationCrud());
			dataRow.createCell(2).setCellValue(audit.get(i).getTableName());
			dataRow.createCell(3).setCellValue(audit.get(i).getTableId());
			dataRow.createCell(4).setCellValue(audit.get(i).getCreateDate().toString());
			dataRow.createCell(5).setCellValue(audit.get(i).getAddressIP());
		}

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		try
		{
			FileOutputStream elFichero = new FileOutputStream(
					"C://ReportesGeneradosSistemaSubastas/Reporte de operación - " + operacion + ".xls");
			libro.write(elFichero);
			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/" + operacion + ".xls"));
			elFichero.close();
		} catch (Exception e)
		{
		}
	}

	/**
	 * Método para generar archivo excel del reporte de auditoria por usuarios
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void archivoExcelPorUsuario() throws FileNotFoundException, DocumentException
	{
		AuditService service = new AuditService();

		List<Audit> audit = service.auditUser(usu);

		HSSFWorkbook libro = new HSSFWorkbook();

		HSSFSheet hoja = libro.createSheet();

		HSSFRow fila = hoja.createRow(0);

		HSSFCell id = fila.createCell((short) 0);
		HSSFCell operationCrud = fila.createCell((short) 1);
		HSSFCell tableName = fila.createCell((short) 2);
		HSSFCell tableId = fila.createCell((short) 3);
		HSSFCell createDate = fila.createCell((short) 4);
		HSSFCell addressIP = fila.createCell((short) 5);

		id.setCellValue(new HSSFRichTextString("ID"));
		operationCrud.setCellValue(new HSSFRichTextString("OperationCrud"));
		tableName.setCellValue(new HSSFRichTextString("TableName"));
		tableId.setCellValue(new HSSFRichTextString("TableId"));
		createDate.setCellValue(new HSSFRichTextString("CreateDate"));
		addressIP.setCellValue(new HSSFRichTextString("AddressIP"));

		for (int i = 0; i < audit.size(); ++i)
		{
			HSSFRow dataRow = hoja.createRow(i + 1);

			dataRow.createCell(0).setCellValue(audit.get(i).getId());
			dataRow.createCell(1).setCellValue(audit.get(i).getOperationCrud());
			dataRow.createCell(2).setCellValue(audit.get(i).getTableName());
			dataRow.createCell(3).setCellValue(audit.get(i).getTableId());
			dataRow.createCell(4).setCellValue(audit.get(i).getCreateDate().toString());
			dataRow.createCell(5).setCellValue(audit.get(i).getAddressIP());
		}

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		try
		{
			FileOutputStream elFichero = new FileOutputStream(
					"C://ReportesGeneradosSistemaSubastas/Reporte de usuario " + usu + ".xls");
			libro.write(elFichero);
			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/" + usu + ".xls"));
			elFichero.close();
		} catch (Exception e)
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
		titulo.setIndentationLeft(150);
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

	/**
	 * Método para generar cabeceras en el reporte
	 * 
	 * @param table
	 */
	public void addTableHeaderSubastasCreadas(PdfPTable table)
	{
		Stream.of("ID", "Producto", "Descripción", "Valor base", "Valor ofertado", "Fecha inicio", "Fecha fin").forEach(columnTitle ->
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
	 * Método para generar archivo pdf para reporte de usuarios
	 * 
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void archivoPdfUsuario() throws FileNotFoundException, DocumentException
	{

		Document pdfDoc = new Document(PageSize.A4);

		PdfWriter m = PdfWriter.getInstance(pdfDoc,
				new FileOutputStream("C://ReportesGeneradosSistemaSubastas/" + usu + ".pdf"));

		pdfDoc.open();

		PdfPTable tableUsuario = new PdfPTable(7);
		PdfPCell titulo = new PdfPCell(new Phrase("REPORTE POR USUARIO: " + usu));
		titulo.setColspan(7);
		titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
		titulo.setBorderWidth(0);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableUsuario.addCell(titulo);
		addTableHeader(tableUsuario);
		addRowsUser(tableUsuario);
		pdfDoc.add(tableUsuario);

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		try
		{
			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/" + usu + ".pdf"));
		} catch (IOException e)
		{
		}
		pdfDoc.close();
		m.close();

	}

	/**
	 * Método para poner cabecera en el archivo
	 * 
	 * @param table
	 */
	public void addTableHeader(PdfPTable table)
	{
		Stream.of("ID", "Usuario", "Tabla", "ID Tabla", "Operación", "Fecha", "Dirección IP").forEach(columnTitle ->
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
	 * Método para poner filas en el reporte
	 * 
	 * @param table
	 */
	public void addRowsUser(PdfPTable table)
	{
		Iterator it = getListaUsuario().iterator();

		Audit x;

		while (it.hasNext())
		{
			x = (Audit) it.next();
			PdfPCell id = new PdfPCell(new Phrase("" + x.getId()));
			PdfPCell userName = new PdfPCell(new Phrase(x.getUserName()));
			PdfPCell TableName = new PdfPCell(new Phrase(x.getTableName()));
			PdfPCell TableID = new PdfPCell(new Phrase("" + x.getTableId()));
			PdfPCell CRUD = new PdfPCell(new Phrase(x.getOperationCrud()));
			PdfPCell Fecha = new PdfPCell(new Phrase("" + x.getCreateDate()));
			PdfPCell IP = new PdfPCell(new Phrase(x.getAddressIP()));
			id.setBorder(Rectangle.NO_BORDER);
			id.setHorizontalAlignment(Element.ALIGN_CENTER);
			userName.setBorder(Rectangle.NO_BORDER);
			userName.setHorizontalAlignment(Element.ALIGN_CENTER);
			TableName.setBorder(Rectangle.NO_BORDER);
			TableName.setHorizontalAlignment(Element.ALIGN_CENTER);
			TableID.setBorder(Rectangle.NO_BORDER);
			TableID.setHorizontalAlignment(Element.ALIGN_CENTER);
			CRUD.setBorder(Rectangle.NO_BORDER);
			CRUD.setHorizontalAlignment(Element.ALIGN_CENTER);
			Fecha.setBorder(Rectangle.NO_BORDER);
			Fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
			IP.setBorder(Rectangle.NO_BORDER);
			IP.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(id);
			table.addCell(userName);
			table.addCell(TableName);
			table.addCell(TableID);
			table.addCell(CRUD);
			table.addCell(Fecha);
			table.addCell(IP);
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
				+ "'AND '" + formato.format(fin) + "' AND operationCrud = 'CREATE' AND tableName = 'Salesueb'");
		
		Iterator<Audit> it = listaFiltada.iterator();
		
		while(it.hasNext())
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