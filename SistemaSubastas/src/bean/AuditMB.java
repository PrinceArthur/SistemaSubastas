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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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

@ManagedBean
@SessionScoped
public class AuditMB
{

	private Audit auditoria;
	private DataModel listaAudit;
	private List listaUsuario;
	private List listaCrud;
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
	
	public void archivoExcelPorCrud() throws FileNotFoundException, DocumentException
	{
		AuditService service = new AuditService();
		
		List<Audit> audit = service.auditCrud(operacion);
		
		
		
        HSSFWorkbook libro = new HSSFWorkbook();

        HSSFSheet hoja = libro.createSheet();

        HSSFRow fila = hoja.createRow(0);


        // Se crea una celda dentro de la fila
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
        
        for (int i = 0; i < audit.size(); ++i) {
            HSSFRow dataRow = hoja.createRow(i + 1);

            dataRow.createCell(0).setCellValue(audit.get(i).getId());
            dataRow.createCell(1).setCellValue(audit.get(i).getOperationCrud());
            dataRow.createCell(2).setCellValue(audit.get(i).getTableName());
            dataRow.createCell(3).setCellValue(audit.get(i).getTableId());
            dataRow.createCell(4).setCellValue(audit.get(i).getCreateDate().toString());
            dataRow.createCell(5).setCellValue(audit.get(i).getAddressIP());
        }
		

        try {
            FileOutputStream elFichero = new FileOutputStream("C://ReportesGeneradosSistemaSubastas/Reporte de operación - "+operacion+".xls");
            libro.write(elFichero);
          //  Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/"+operacion+".xls"));
            elFichero.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void archivoExcelPorUsuario() throws FileNotFoundException, DocumentException
	{
		AuditService service = new AuditService();
		
		List<Audit> audit = service.auditUser(usu);
		
		
		
        HSSFWorkbook libro = new HSSFWorkbook();

        HSSFSheet hoja = libro.createSheet();

        HSSFRow fila = hoja.createRow(0);


        // Se crea una celda dentro de la fila
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
        
        for (int i = 0; i < audit.size(); ++i) {
            HSSFRow dataRow = hoja.createRow(i + 1);

            dataRow.createCell(0).setCellValue(audit.get(i).getId());
            dataRow.createCell(1).setCellValue(audit.get(i).getOperationCrud());
            dataRow.createCell(2).setCellValue(audit.get(i).getTableName());
            dataRow.createCell(3).setCellValue(audit.get(i).getTableId());
            dataRow.createCell(4).setCellValue(audit.get(i).getCreateDate().toString());
            dataRow.createCell(5).setCellValue(audit.get(i).getAddressIP());
        }
		

        try {
            FileOutputStream elFichero = new FileOutputStream("C://ReportesGeneradosSistemaSubastas/Reporte de usuario "+usu+".xls");
            libro.write(elFichero);
          //  Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/"+usu+".xls"));
            elFichero.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	public void pdfCrud() throws FileNotFoundException, DocumentException
		{

//			ClassLoader loader = Thread.currentThread().getContextClassLoader();
//			URL url = loader.getResource("Acme-corp.png");
			Document pdfDoc = new Document(PageSize.A4);

			PdfWriter m = PdfWriter.getInstance(pdfDoc,
					new FileOutputStream("C://ReportesGeneradosSistemaSubastas/" + operacion + ".pdf"));

			pdfDoc.open();

			PdfPTable tableUsuario = new PdfPTable(7);
			PdfPCell titulo = new PdfPCell(new Phrase("REPORTE POR OPERACIÓN : "  + operacion ));
			titulo.setColspan(7);
			titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
			titulo.setBorderWidth(0);
			titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableUsuario.addCell(titulo);
			addTableHeaderCrud(tableUsuario);
			addRowsUserCrud(tableUsuario);
			pdfDoc.add(tableUsuario);
//			try
//			{
////				Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/"+operacion+".pdf"));
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			pdfDoc.close();
			m.close();

		}

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

	public void archivoPorUsuario() throws FileNotFoundException, DocumentException
	{

		Document pdfDoc = new Document(PageSize.A4);

		PdfWriter m = PdfWriter.getInstance(pdfDoc,
				new FileOutputStream("C://ReportesGeneradosSistemaSubastas/" + usu + ".pdf"));

		pdfDoc.open();

		PdfPTable tableUsuario = new PdfPTable(7);
		PdfPCell titulo = new PdfPCell(new Phrase("REPORTE POR USUARIO: "  + usu ));
		titulo.setColspan(7);
		titulo.setBackgroundColor(BaseColor.LIGHT_GRAY);
		titulo.setBorderWidth(0);
		titulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		tableUsuario.addCell(titulo);
		addTableHeader(tableUsuario);
		addRowsUser(tableUsuario);
		pdfDoc.add(tableUsuario);
//		try
//		{
////			Desktop.getDesktop().open(new File("C://ReportesGeneradosSistemaSubastas/"+usu+".pdf"));
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		pdfDoc.close();
		m.close();

	}

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


	public List getListaUsuario()
	{
		listaUsuario = new AuditService().auditUser(usu);
		return listaUsuario;
	}

	public void setListaUsuario(List listaUsuario)
	{
		this.listaUsuario = listaUsuario;
	}


	public List getListaCrud()
	{
		listaCrud = new AuditService().auditCrud(operacion);
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
