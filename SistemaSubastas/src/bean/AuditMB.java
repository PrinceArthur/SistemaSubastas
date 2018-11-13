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
import service.AuditService;

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
	 * @throws FileNotFoundException
	 * @throws DocumentException
	 */
	public void pdfCrud() throws FileNotFoundException, DocumentException
	{

		Document pdfDoc = new Document(PageSize.A4);

		PdfWriter m = PdfWriter.getInstance(pdfDoc,
				new FileOutputStream("C://ReportesGeneradosSistemaSubastas/" + operacion + ".pdf"));

		pdfDoc.open();

		PdfPTable tableUsuario = new PdfPTable(7);
		PdfPCell titulo = new PdfPCell(new Phrase("REPORTE POR OPERACIÓN : " + operacion));
		titulo.setColspan(7);
		titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
		titulo.setBorderWidth(0);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableUsuario.addCell(titulo);
		addTableHeaderCrud(tableUsuario);
		addRowsUserCrud(tableUsuario);
		pdfDoc.add(tableUsuario);

		mensajeError = "Se ha generado el archivo correctamente.";
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Cuidado", mensajeError));

		try
		{
			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/" + operacion + ".pdf"));
		} catch (IOException e)
		{
		}
		pdfDoc.close();
		m.close();

	}

	/**
	 * Método para generar cabeceras en el reporte
	 * 
	 * @param table
	 */
	public void addTableHeaderCrud(PdfPTable table)
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
	 * Método para agegas las filas al reporte
	 * 
	 * @param table
	 */
	public void addRowsUserCrud(PdfPTable table)
	{
		Iterator it = getListaCrud().iterator();

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
	 * Método para generar archivo pdf para reporte de usuarios 
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
			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/"+usu+".pdf"));
		} catch (IOException e)
		{
		}
		pdfDoc.close();
		m.close();

	}
	
	/**
	 * Método para poner cabecera en el archivo
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
	 * @return auditoria
	 */
	public Audit getAuditoria()
	{
		return auditoria;
	}

	/**
	 * Modificar auditoria
	 * @param auditoria
	 */
	public void setAuditoria(Audit auditoria)
	{
		this.auditoria = auditoria;
	}

	/**
	 * Dar lista de auditorias
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
	 * @return listaUsuario
	 */
	public List getListaUsuario()
	{
		listaUsuario = new AuditService().auditUser(usu);
		return listaUsuario;
	}

	/**
	 * Modifica lista audorias por usuarios
	 * @param listaUsuario
	 */
	public void setListaUsuario(List listaUsuario)
	{
		this.listaUsuario = listaUsuario;
	}

	/**
	 * Dar lista auditorias por CRUD
	 * @return listaCrud
	 */
	public List getListaCrud()
	{
		listaCrud = new AuditService().auditCrud(operacion);
		return listaCrud;
	}

	/**
	 * Modifica lista auditorias por CRUD
	 * @param listaCrud
	 */
	public void setListaCrud(List listaCrud)
	{
		this.listaCrud = listaCrud;
	}

	/**
	 * Dar el usuario
	 * @return usu
	 */
	public String getUsu()
	{
		return usu;
	}

	/**
	 * Modifica el usuario
	 * @param usu
	 */
	public void setUsu(String usu)
	{
		this.usu = usu;
	}

	/**
	 * Dar operacion CRUD
	 * @return operacion
	 */
	public String getOperacion()
	{
		return operacion;
	}

	/**
	 * Modifica la operacion
	 * @param operacion
	 */
	public void setOperacion(String operacion)
	{
		this.operacion = operacion;
	}

	/**
	 * Da el mensaje error
	 * @return mensajeError
	 */
	public String getMensajeError()
	{
		return mensajeError;
	}

	/**
	 * Modifica el mensaje error
	 * @param mensajeError
	 */
	public void setMensajeError(String mensajeError)
	{
		this.mensajeError = mensajeError;
	}

}