package bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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

@ManagedBean
@SessionScoped
public class AuditMB
{

	private Audit auditoria;
	private DataModel listaAudit;
	private static List listaUsuario;
	private static List listaCrud;
	private static String usuario;
	private static String crud;
	private String operacion;

	private String usu;

	public AuditMB()
	{
		auditoria = new Audit();
	}

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

			e.getMessage();
		}
	}
	
	public void archivoPorCrud()
	{
		crud = operacion;
		
		try
		{
			pdfCrud("Algo");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (DocumentException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void pdfCrud(String algo) throws FileNotFoundException, DocumentException
		{

//			ClassLoader loader = Thread.currentThread().getContextClassLoader();
//			URL url = loader.getResource("Acme-corp.png");
			Document pdfDoc = new Document(PageSize.A4);

			PdfWriter m = PdfWriter.getInstance(pdfDoc,
					new FileOutputStream("C://ReportesGeneradosSistemaSubastas/" + crud + ".pdf"));

			pdfDoc.open();

			PdfPTable tableUsuario = new PdfPTable(7);
			PdfPCell titulo = new PdfPCell(new Phrase("REPORTE POR OPERACIÓN : "  + crud ));
			titulo.setColspan(7);
			titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
			titulo.setBorderWidth(0);
			titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableUsuario.addCell(titulo);
			addTableHeaderCrud(tableUsuario);
			addRowsUserCrud(tableUsuario);
			pdfDoc.add(tableUsuario);

			pdfDoc.close();
			m.close();

		}

		public static void addTableHeaderCrud(PdfPTable table)
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

		public static void addRowsUserCrud(PdfPTable table)
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

	public void archivoPorUsuario()
	{

		usuario = getUsu();

		try
		{
			archivoPorUsuario("Reporte por usuario");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void archivoPorUsuario(String fileName) throws FileNotFoundException, DocumentException
	{

//		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//		URL url = loader.getResource("Acme-corp.png");
		Document pdfDoc = new Document(PageSize.A4);

		PdfWriter m = PdfWriter.getInstance(pdfDoc,
				new FileOutputStream("C://ReportesGeneradosSistemaSubastas/" + usuario + ".pdf"));

		pdfDoc.open();

		PdfPTable tableUsuario = new PdfPTable(7);
		PdfPCell titulo = new PdfPCell(new Phrase("REPORTE POR USUARIO: "  + usuario ));
		titulo.setColspan(7);
		titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
		titulo.setBorderWidth(0);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableUsuario.addCell(titulo);
		addTableHeader(tableUsuario);
		addRowsUser(tableUsuario);
		pdfDoc.add(tableUsuario);

		pdfDoc.close();
		m.close();

	}

	public static void addTableHeader(PdfPTable table)
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

	public static void addRowsUser(PdfPTable table)
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

	public Audit getAuditoria()
	{
		return auditoria;
	}

	public void setAuditoria(Audit auditoria)
	{
		this.auditoria = auditoria;
	}

	public DataModel getListarAuditorias()
	{
		List<Audit> lista = new AuditDAOImpl().lista();
		listaAudit = new ListDataModel(lista);
		return listaAudit;
	}

	public String getUsuario()
	{
		return usuario;
	}

	public void setUsuario(String usuario)
	{
		this.usuario = usuario;
	}

	public static List getListaUsuario()
	{
		listaUsuario = new AuditService().auditUser(usuario);
		return listaUsuario;
	}

	public void setListaUsuario(List listaUsuario)
	{
		this.listaUsuario = listaUsuario;
	}

	public String getCrud()
	{
		return crud;
	}

	public void setCrud(String crud)
	{
		this.crud = crud;
	}

	public static List getListaCrud()
	{
		listaCrud = new AuditService().auditCrud(crud);
		return listaCrud;
	}

	public  void setListaCrud(List listaCrud)
	{
		this.listaCrud = listaCrud;
	}

	public String getUsu()
	{
		return usu;
	}

	public void setUsu(String usu)
	{
		this.usu = usu;
	}

	public String getOperacion()
	{
		return operacion;
	}

	public void setOperacion(String operacion)
	{
		this.operacion = operacion;
	}

	
}
